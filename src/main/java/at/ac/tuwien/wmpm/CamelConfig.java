package at.ac.tuwien.wmpm;

import java.util.concurrent.Callable;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig extends SingleRouteCamelConfiguration {
	
	//these property keys are configured in the respective
	//application-${profile}.properties files
	//profile is for example "test". 
	//through that test-cases (using @ActiveProfiles("test"))
	//can replace them with mocks. 
	
	public static final String SLACK_ENDPOINT = "{{routes.push_to_slack}}";

	public static final String PUBLISH_CURRENT_PROJECTION_ENDPOINT = "direct:publish_current_projection";
	
	public static final String REMOVE_PERSONAL_INFORMATION_ENDPOINT = "direct:remove_personal_information";

	@Override
	public RouteBuilder route() {
		return new MyRouterBuilder();
	}
	
	private class MyRouterBuilder extends RouteBuilder {
		@Override
		public void configure() throws Exception {
//			samples();

			
			//this takes an Object
			from(REMOVE_PERSONAL_INFORMATION_ENDPOINT)
				//TODO tranform to JSON
				//TODO use JOLT to strip peronal information
				.to("direct:replace_this_with_mongodb_store")
//				.to("direct:log")
				;
			
			//will be invoked by a timer route
			from(PUBLISH_CURRENT_PROJECTION_ENDPOINT)
				//TODO select data from the end-results table(s)
				//TODO filter message if there are no results yet
				//TODO format a string be pushed to slack 
				//(image maybe later, don't know if apache-camel-slack lets you do that)
				.to(SLACK_ENDPOINT);	
			
			from("direct:push_to_slack")
				.to("log:Slack?level=INFO");
		}
		
		
		private void samples() {
//			Predicate isQuit = body().isEqualTo("quit");
			from("stream:in?promptMessage=Enter something: ")
//				.transform().simple("ref:myBean")
				.setHeader("header1").constant("test")
//				.choice().when(isQuit).to("jms:queue:shutdown")
//				.otherwise()
				.to("jms:queue:simple");
			
			from("jms:queue:simple")
				.to("log:simpleMessages?showHeaders=true");
			
			//http://camel.apache.org/spring-web-services.html
			JaxbDataFormat jaxb = new JaxbDataFormat(false);
			jaxb.setContextPath("io.spring.guides.gs_producing_web_service");
			
			//spring-ws web-service ruft ein repository auf und liefert
			//das ergebnis zurück
			from("spring-ws:rootqname:"
					+ "{http://spring.io/guides/gs-producing-web-service}"
					+ "getCountryRequest?endpointMapping=#endpointMapping")
				.wireTap("direct:log").end()
				.unmarshal(jaxb)
				.transform().simple("body.name")
				//.bean(SampleCountryRepository.class, "findCountry")
				.transform().spel("#{@responseFactory.createCountryResponse(body)}")
				.marshal(jaxb)
				.wireTap("direct:log").end()
				.transform(body())
				;
			
			from("direct:log")
				.convertBodyTo(String.class)
				.to("log:Camel?level=INFO");
		}
	}
	
	


	@Bean
	public String myBean() {
		return "Hello!";
	}
	
	@Bean
	public Callable<Integer> valueGetter() {
		return new Callable<Integer>() {

			public Integer call() {
				return 42;
			}
		};
	}
}
