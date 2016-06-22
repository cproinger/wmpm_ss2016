package at.ac.tuwien.wmpm;

import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import at.ac.tuwien.wmpm.helper.ExtractBallotMessageProcessor;
import at.ac.tuwien.wmpm.service.AlreadyVotedException;
import at.ac.tuwien.wmpm.service.BallotClosedException;
import at.ac.tuwien.wmpm.service.IllegalPersonInfoException;
import at.ac.tuwien.wmpm.service.IllegalVoteInfoException;
import at.ac.tuwien.wmpm.service.impl.VoteResponseFactory;
import at.ac.tuwien.wmpm.ss2016.VoteRequest;

@Configuration
public class CamelConfig extends SingleRouteCamelConfiguration {

	// these property keys are configured in the respective
	// application-${profile}.properties files
	// profile is for example "test".
	// through that test-cases (using @ActiveProfiles("test"))
	// can replace them with mocks.

	public static final String JDBC_QUERY = "jdbc:dataSource?useHeadersAsParameters=true";

	public static final String SLACK_ENDPOINT = "{{routes.push_to_slack}}";

	public static final String SEND_BALLOT_BOX_RESULT = "direct:sendMail";// "{{routes.sendBallotBoxResult}}";

	public static final String SEND_BALLOT_BOX_RESULT_MAIL_ROUTE = "mock:SEND_BALLOT_BOX_RESULT_MAIL_ROUTE";

	public static final String PUBLISH_CURRENT_PROJECTION_ENDPOINT = "{{routes.publishCurrentProjection}}";

	public static final String REMOVE_PERSONAL_INFORMATION_ENDPOINT = "direct:remove_personal_information";

	public static final String ROUTES_AFTER_CANDIDATE_INSERT_ENDPOINT = "{{routes.after_candidate_insert}}";

	public static final String POP3_URI = "pop3://{{mail.host}}:{{mail.port}}?consumer.delay={{mail.poll-interval}}&username={{mail.user}}&password={{mail.password}}&delete=true";

	public static final String BALLOTS_QUEUE = "jms:queue:ballots";

	public static final String ILLEGAL_VOTE_INFO_ENDPOINT = "{{routes.illegalVoteInfo}}";

	// listen for all requests which have a VoteRequest root element in their
	// body.
	public static final String VOTES_WEB_SERVICE_ENDPOINT = "spring-ws:rootqname:{http://tuwien.ac.at/wmpm/ss2016}VoteRequest?endpointMapping=#endpointMapping";

	public static final String OPEN_BALLOT_BOX = "direct:OpenBallotBox";

	public static final String INCREASE_VOTECOUNT_ENDPOINT = "{{routes.increaseVotecount}}";

	@Value("${processes.endresultCalulaction:true}")
	private boolean activateEndResultCalculationProcess;

	@Override
	public RouteBuilder route() {
		return new MyRouterBuilder();
	}

	private class MyRouterBuilder extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			votingProcess();

			countingProcess();

			if (activateEndResultCalculationProcess)
				endResultCalculationProcess();

		}

		private void votingProcess() {
			String schemaPath = "classpath:schema/votes.xsd";

			// Create a JAXB data format for a vote request from the votes
			// schema.
			JaxbDataFormat jaxb = new JaxbDataFormat(VoteRequest.class.getPackage().getName());

			// setup web service endpoint route.
			from(VOTES_WEB_SERVICE_ENDPOINT)
				.doTry()
					.to("validator:" + schemaPath)
				.doCatch(ValidationException.class)
					.bean(VoteResponseFactory.class, "createInvalidSchemaResponse")
				.end()
				.unmarshal(jaxb)
				.doTry()
					.to("bean:voteRequestService?method=checkIfBallotIsOpen()")
					.to("bean:voteRequestService?method=transformRequest(${body})")
					.split(simple("${body.getValidationObjectList()}"))
					.choice()
						.when(simple("${body.getType()} == 'PERSONAL_INFORMATION_ID'"))
							.to("bean:voteRequestService?method=validatePersonalId(${body.getValue()})")
						.otherwise()
							.to("bean:voteRequestService?method=validateVotingCardId(${body.getValue()})")
							.end()
					.end()
					.to("bean:voteRequestService?method=doVote(${body})")
					.bean(VoteResponseFactory.class, "createValidResponse")
				.endDoTry()
				.doCatch(IllegalPersonInfoException.class)
					.bean(VoteResponseFactory.class, "createPersonErrorResponse")
				.doCatch(AlreadyVotedException.class)
					.bean(VoteResponseFactory.class, "createAlreadyVotedResponse")
				.doCatch(BallotClosedException.class)
					.bean(VoteResponseFactory.class, "createBallotClosedResponse")
				.end()
				.marshal(jaxb);

			// this takes an Object
			from(REMOVE_PERSONAL_INFORMATION_ENDPOINT).marshal().json(JsonLibrary.Jackson)
				.to("jolt:stripAllButVoteInfo.json?inputType=JsonString&outputType=JsonString")
				.to("mongodb:mongo?database=test&collection=votes&operation=insert");
		}

		private void countingProcess() {
			from("{{routes.closeBallot}}")
				.to("bean:ballot?method=close()")
				.to(OPEN_BALLOT_BOX);

			from(OPEN_BALLOT_BOX)
				.to("bean:voteRepository?method=findAll()")
				.split(body())
				.to(BALLOTS_QUEUE);

			from(BALLOTS_QUEUE)
				.onException(IllegalVoteInfoException.class)
					.to(ILLEGAL_VOTE_INFO_ENDPOINT)
					.end()
				.to("bean:extractCandidateVoteService?method=extract(${body})")
				.to("bean:verifyCandidateVoteItemService?method=lookupCandidate(${body})")
				.to(INCREASE_VOTECOUNT_ENDPOINT);

			from("direct:IncreaseVotecount")
				.to("sql:update candidate set vote_count = vote_count + 1 where name = ${body.name}?dataSource=dataSource")
				.aggregate(new UseLatestAggregationStrategy())
					.inMessage()
					.completionSize(100)
					.completionTimeout(5000)
				.to(SEND_BALLOT_BOX_RESULT);

			// ballot box result to mail
			from(SEND_BALLOT_BOX_RESULT)
				.to("sql:select name, vote_count from candidate?dataSource=dataSource")
				.marshal().csv()
				.setBody(body().prepend(simple("${bean:ballotBoxIdentifierService?method=getUniqueBallotBoxId}\n")))
				.to("stream:out").to(SEND_BALLOT_BOX_RESULT_MAIL_ROUTE);
		}

		private void endResultCalculationProcess() {

			// mail csv to database
			from(POP3_URI).process(new ExtractBallotMessageProcessor()).unmarshal().csv()
				.setHeader("candidates", simple("${body.size()}")).setHeader("message", body())

				// delete old votes
				.setBody(simple("delete from polls where ballot_box_id = :?ballot_box_id"))
				.to(JDBC_QUERY)
				.setBody(header("message"))
				.split(body())
					.setHeader("candidate", simple("${body.get(0)}"))
					.setHeader("vote_count", simple("${body.get(1)}"))
					.setBody(simple("insert into polls(candidate, vote_count, ballot_box_id) values (:?candidate, :?vote_count, :?ballot_box_id);"))
					.to(JDBC_QUERY)
				.end()
				.setBody(simple("Inserted ${header.candidates} votes from ballot box ${header.ballot_box_id} via {{mail.user}}"))
				.to("stream:out")
				.to(ROUTES_AFTER_CANDIDATE_INSERT_ENDPOINT);

			// will be invoked by a timer route
			from(PUBLISH_CURRENT_PROJECTION_ENDPOINT).to(
				"sql:select candidate, sum(vote_count) vote_count from polls group by candidate?dataSource=dataSource")

				// TODO filter message if there are no results yet
				// .filter(body().isNotNull())

				// here we format a string be pushed to slack
				.to("bean:slackConstructor?method=marshal(${body})")

				// (image maybe later, don't know if apache-camel-slack lets
				// you do that)
				.to(SLACK_ENDPOINT);

			from("direct:push_to_slack").to("slack:#general");
		}
	}
}
