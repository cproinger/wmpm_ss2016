package at.ac.tuwien.wmpm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.slack.SlackComponent;
import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.util.FileSystemUtils;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.server.EndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.MessageEndpointAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;

@SpringBootApplication
public class App {
	
	@Bean
	public CamelEndpointMapping endpointMapping() {
		return new CamelEndpointMapping();
	}


	public static void main(String[] args) {
		// clean previous state
		FileSystemUtils.deleteRecursively(new File("activemq-data"));

		SpringApplication app = new SpringApplication(App.class);
		app.setAdditionalProfiles("dev");
		ConfigurableApplicationContext ctx = app.run();
		assert ctx != null;
		CamelSpringBootApplicationController applicationController = ctx
				.getBean(CamelSpringBootApplicationController.class);
		 applicationController.blockMainThread();
	}
}
