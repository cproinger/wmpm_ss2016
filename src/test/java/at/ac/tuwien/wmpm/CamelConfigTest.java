package at.ac.tuwien.wmpm;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//TODO needs cleanup
//@RunWith(CamelSpringJUnit4ClassRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CamelConfigTest.class
		)
//@ContextConfiguration(
//		classes = CamelConfig.class,
//		loader = CamelSpringDelegatingTestContextLoader.class
//		)
//@BootstrapWith(CamelTestContextBootstrapper.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//@MockEndpoints() //mock endpoints somehow does not work with spring boot
//@DisableJmx(true)
//@EnableAutoConfiguration 
@EnableAutoConfiguration
@ComponentScan
@ActiveProfiles("test")
public class CamelConfigTest /* extends AbstractJUnit4SpringContextTests */ {
	
	@Produce(uri = CamelConfig.REMOVE_PERSONAL_INFORMATION_ENDPOINT)
	private ProducerTemplate removePersonalInformationRoute;
	
	@Test
	public void testStripPersonalInformationAndSave() {
		
		/* TODO Task 1. 
		 * 		define any class that has a vote-field
		 * 		and other fields. 
		 * 		
		 * 		given an objects of such a class 
		 * 		when sent to this route
		 * 		then only the content of the vote field is stored in mongodb
		 */
		removePersonalInformationRoute.sendBody("This is an ex-parrot!");
	}
	
	@Produce(uri = CamelConfig.PUBLISH_CURRENT_PROJECTION_ENDPOINT)
	private ProducerTemplate publishCurrentProjectionRoute;
	
	@EndpointInject(uri = CamelConfig.SLACK_ENDPOINT)
    protected MockEndpoint publishToSlackMock;
	
	@Test
	public void testEndResultDataToPublishableString() throws InterruptedException {
		
		/*
		 * TODO Task 2. 
		 * 		define a table in /wmpm/src/main/resources/sql/create-tables.sql
		 * 		use a jdbc-template for example to change the Data accordingly
		 * 		add transformations
		 */
		Date now = new Date();
//		MockEndpoint publishToSlackMock = getMockEndpoint("mock:direct:push_to_slack__mockable");
//		
		publishToSlackMock.expectedBodiesReceived(now.toString() + " someData");

		publishCurrentProjectionRoute.sendBody(now);
		
		publishToSlackMock.assertIsSatisfied();
	}
}
