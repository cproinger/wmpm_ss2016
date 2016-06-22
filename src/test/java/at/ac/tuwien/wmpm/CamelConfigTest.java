
package at.ac.tuwien.wmpm;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import at.ac.tuwien.wmpm.domain.Vote;
import at.ac.tuwien.wmpm.repository.VoteRepository;
import at.ac.tuwien.wmpm.ss2016.VoteInfo;
import at.ac.tuwien.wmpm.ss2016.VoteInfo.Item;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;

//TODO needs cleanup
//@RunWith(CamelSpringJUnit4ClassRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
// @ContextConfiguration(
// classes = CamelConfig.class,
// loader = CamelSpringDelegatingTestContextLoader.class
// )

/*
 * warning!! this starts a separate camel context leading to beans being
 * instantiated twice
 */
// @BootstrapWith(CamelTestContextBootstrapper.class)

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
// @MockEndpoints() //mock endpoints somehow does not work with spring boot
// @DisableJmx(true)
// @EnableAutoConfiguration
@EnableAutoConfiguration
@ComponentScan
@ActiveProfiles({ "test"
		// this profiles makes you use a fake mongo-server
		// if no real server is available
		, "fakeMongo" })
public class CamelConfigTest /* extends AbstractJUnit4SpringContextTests */ {

	private static final int AWAIT_TIME = 5;

	@Produce(uri = CamelConfig.REMOVE_PERSONAL_INFORMATION_ENDPOINT)
	private ProducerTemplate removePersonalInformationRoute;

	@Autowired
	private VoteRepository voteRepo;

	@Produce(uri = CamelConfig.OPEN_BALLOT_BOX)
	private ProducerTemplate openBallotBox;

	@Test

	public void testStoreVote() {
		System.out.println(voteRepo.findAll());
		VoteInfo vi = new VoteInfo();
		Item i = new Item();
		vi.getItem().add(i);
		i.setCandiate("asdf");
		Item i2 = new Item();
		i2.setCandiate("cvcv122");
		i2.setMark("xxx");
		vi.getItem().add(i2);
		voteRepo.save(new Vote(vi));

		openBallotBox.sendBody("test");
	}

	@Test
	// @Ignore("fails for now")
	public void testStripPersonalInformationAndSave() {

		/*
		 * TODO Task 1. define any class that has a vote-field and other fields.
		 * 
		 * given an objects of such a class when sent to this route then only
		 * the content of the vote field is stored in mongodb
		 */
		VoteRequest vr = new VoteRequest();
		VoteInfo vi = new VoteInfo();
		Item i = new Item();
		i.setCandiate("adsf");
		vi.getItem().add(i);
		vr.setVoteInfo(vi);

		long countBefore = voteRepo.count();
		removePersonalInformationRoute.sendBody(vr);
		assertEquals(countBefore + 1, voteRepo.count());
		voteRepo.findAll().stream().map(v -> v.getVoteInfo().getItem().get(0).getCandiate())
				.collect(Collectors.toList()).toString();
	}

	@Produce(uri = CamelConfig.PUBLISH_CURRENT_PROJECTION_ENDPOINT)
	private ProducerTemplate publishCurrentProjectionRoute;

	@EndpointInject(uri = CamelConfig.SLACK_ENDPOINT)
	protected MockEndpoint publishToSlackMock;

	@Test
	// @Ignore("fails for now")
	public void testEndResultDataToPublishableString() throws InterruptedException {
		jdbcTemplate.execute("insert into polls(candidate, vote_count) values('Aarenson', 497)");
		jdbcTemplate.execute("insert into polls(candidate, vote_count) values('Ali', 521)");

		/*
		 * Task 2. define a table in
		 * /wmpm/src/main/resources/sql/create-tables.sql use a jdbc-template
		 * for example to change the Data accordingly add transformations
		 */
		Date now = new Date();
		// MockEndpoint publishToSlackMock =
		// getMockEndpoint("mock:direct:push_to_slack__mockable");
		//
		publishToSlackMock.expectedBodyReceived().body().contains("Aarenson 48,82 %\nAli 51,18 %\n");

		publishCurrentProjectionRoute.sendBody(now);

		publishToSlackMock.assertIsSatisfied();
	}

	// @Rule
	@ClassRule
	public static GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP_POP3_IMAP);

	@EndpointInject(uri = CamelConfig.ROUTES_AFTER_CANDIDATE_INSERT_ENDPOINT)
	protected MockEndpoint afterCandidateInsert;

	@Test
	public void testFromMailWithCSV_toEndResultTables() throws MessagingException, InterruptedException {
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", "subject",
				"ballot-box-1\nTrump,10\nLugner,13");

		afterCandidateInsert.expectedMinimumMessageCount(1);
		afterCandidateInsert.await(5, TimeUnit.SECONDS);
		afterCandidateInsert.assertIsSatisfied();

		assertEquals(10, (long) jdbcTemplate.queryForObject("select vote_count from polls where candidate = 'Trump'",
				Long.class));
		assertEquals(13, (long) jdbcTemplate.queryForObject("select vote_count from polls where candidate = 'Lugner'",
				Long.class));
		assertEquals("ballot-box-1",
				jdbcTemplate.queryForObject("select ballot_box_id from polls where candidate = 'Trump'", String.class));
		assertEquals("ballot-box-1", jdbcTemplate
				.queryForObject("select ballot_box_id from polls where candidate = 'Lugner'", String.class));
	}

	@Inject
	private JdbcTemplate jdbcTemplate;

	@Produce(uri = CamelConfig.BALLOTS_QUEUE)
	private ProducerTemplate ballotsQueueProducer;

	@EndpointInject(uri = CamelConfig.INCREASE_VOTECOUNT_ENDPOINT)
	protected MockEndpoint voteExtractedMock;

	@Test
	public void testBallotVerification_onlyOneValid_works() throws InterruptedException {
		voteExtractedMock.expectedMessageCount(1);
		VoteInfo info = new VoteInfo();
		info.getItem().add(createVoteItem("Beavis", "x"));
		info.getItem().add(createVoteItem("Butthead", ""));
		ballotsQueueProducer.sendBody(info);
		voteExtractedMock.await(AWAIT_TIME, TimeUnit.SECONDS);
		voteExtractedMock.assertIsSatisfied();
	}

	@EndpointInject(uri = CamelConfig.ILLEGAL_VOTE_INFO_ENDPOINT)
	private MockEndpoint illegalVoteInfoException;

	@Test
	public void testBallotVerification_twoValid_exception() throws InterruptedException {
		voteExtractedMock.expectedMinimumMessageCount(1);
		illegalVoteInfoException.expectedMessageCount(1);

		VoteInfo info = new VoteInfo();
		info.getItem().add(createVoteItem("Beavis", "x"));
		info.getItem().add(createVoteItem("Butthead", "x"));
		ballotsQueueProducer.sendBody(info);

		voteExtractedMock.await(AWAIT_TIME, TimeUnit.SECONDS);
		voteExtractedMock.assertIsNotSatisfied();
		illegalVoteInfoException.assertIsSatisfied();
	}

	@Test
	public void testBallotVerification_oneValidOneNonExistent_exception() throws InterruptedException {
		voteExtractedMock.expectedMinimumMessageCount(1);
		illegalVoteInfoException.expectedMessageCount(1);

		VoteInfo info = new VoteInfo();
		info.getItem().add(createVoteItem("Beavis", "x"));
		info.getItem().add(createVoteItem("Trump", "x"));
		ballotsQueueProducer.sendBody(info);

		voteExtractedMock.await(AWAIT_TIME, TimeUnit.SECONDS);
		voteExtractedMock.assertIsNotSatisfied();
		illegalVoteInfoException.assertIsSatisfied();
	}

	@Test
	public void testBallotVerification_valid_voteExtracted() throws InterruptedException {
		voteExtractedMock.expectedMinimumMessageCount(1);
		illegalVoteInfoException.expectedMessageCount(1);

		VoteInfo info = new VoteInfo();
		info.getItem().add(createVoteItem("Beavis", "x"));
		ballotsQueueProducer.sendBody(info);

		voteExtractedMock.await(AWAIT_TIME, TimeUnit.SECONDS);
		voteExtractedMock.assertIsSatisfied();
	}

	private VoteInfo.Item createVoteItem(String name, String mark) {
		VoteInfo.Item e = new VoteInfo.Item();
		e.setCandiate(name);
		e.setMark(mark);
		return e;
	}
}