package at.ac.tuwien.wmpm;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.annotation.Configuration;

import at.ac.tuwien.wmpm.service.IllegalVoteInfoException;

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

  public static final String ROUTES_AFTER_CANDIDATE_INSERT_ENDPOINT = "{{routes.after_candidate_insert}}";

  public static final String POP3_URI = "pop3://{{mail.host}}:{{mail.port}}?consumer.delay={{mail.poll-interval}}&username={{mail.user}}&password={{mail.password}}";

  public static final String BALLOTS_QUEUE = "jms:queue:ballots";

  public static final String ILLEGAL_VOTE_INFO_ENDPOINT = "mock:IllegalVoteInfoException";

  public static final String VOTES_WEB_SERVICE_ENDPOINT = "spring-ws:rootqname:{http://tuwien.ac.at/wmpm/ss2016/}VoteRequest?endpointMapping=#endpointMapping";


  @Override
  public RouteBuilder route() {
    return new MyRouterBuilder();
  }

  private class MyRouterBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

      //samples();

      //Create a JAXB data format for a vote request from the votes schema.
      JaxbDataFormat jaxb = new JaxbDataFormat();
      jaxb.setContextPath(VoteRequest.class.getPackage().getName());
      jaxb.setSchema("classpath:schema/votes.xsd");

      //setup web service endpoint route.
      from(VOTES_WEB_SERVICE_ENDPOINT)
              .unmarshal(jaxb)
              .to("bean:personInfoValidationService?method=validate(${body})");

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

      // mail csv to database
      from(POP3_URI)
              .unmarshal().csv()
              .split(body())

              .setHeader("candidate", simple("${body.get(0)}"))
              .setHeader("vote_count", simple("${body.get(1)}"))
              .setBody(simple("insert into polls(candidate, vote_count) values (:?candidate, :?vote_count);"))
              .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                  exchange.getContext();
                }
              })
//                    .to("direct:log");
              .to("jdbc:dataSource?useHeadersAsParameters=true")
//                    .to("log:test?level=INFO&showAll=true")
              .to(ROUTES_AFTER_CANDIDATE_INSERT_ENDPOINT);

      from(BALLOTS_QUEUE)
              .onException(IllegalVoteInfoException.class)
                .to(ILLEGAL_VOTE_INFO_ENDPOINT)
                .end()
              .to("bean:extractCandidateVoteService?method=extract(${body})")
              .to("bean:verifyCandidateVoteItemService?method=lookupCandidate(${body})")
              .to("mock:VoteExtracted");

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
}
