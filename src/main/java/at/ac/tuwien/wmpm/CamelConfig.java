package at.ac.tuwien.wmpm;

import java.util.concurrent.Callable;

import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CamelConfig extends FatJarRouter {
	

	@Override
	public void configure() throws Exception {
//		Predicate isQuit = body().isEqualTo("quit");
		from("stream:in?promptMessage=Enter something: ")
//			.transform().simple("ref:myBean")
			.setHeader("header1").constant("test")
//			.choice().when(isQuit).to("jms:queue:shutdown")
//			.otherwise()
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
			.bean(SampleCountryRepository.class, "findCountry")
			.transform().spel("#{@responseFactory.createCountryResponse(body)}")
			.marshal(jaxb)
			.wireTap("direct:log").end()
			.transform(body())
			;
		
		from("direct:log")
			.convertBodyTo(String.class)
			.to("log:getCountryRequest?level=INFO");
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
