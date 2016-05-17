package at.ac.tuwien.wmpm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
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
@EnableJms
@EnableWs
@PropertySource(value = { "classpath:/config/db.properties" })
public class App extends RepositoryRestMvcConfiguration {
	
	@Autowired
	private ActiveMQConnectionFactory connectionFactory;
	
	@PostConstruct
	private void setTrustedPackagesForJMS() {
		// because using object messages needs this for security
		// purposes
		Class<?> cs[] = new Class<?>[] {
			VoteRequest.class, 
			List.class
		};
		List<String> trustedPackages = Arrays.stream(cs)
				.map(c -> c.getPackage().getName())
				.collect(Collectors.toList());
		connectionFactory.setTrustedPackages(trustedPackages);
	}

	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	/**
	 * http://localhost:8080/ws/votes.wsdl
	 */
	@Bean(name = "votes")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema schema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("VotePort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace(schema.getTargetNamespace());
		wsdl11Definition.setSchema(schema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema votesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("schema/votes.xsd"));
	}

	@Bean
	public EndpointAdapter messageEndpointAdapter() {
		return new MessageEndpointAdapter();
	}

	@Bean
	public CamelEndpointMapping endpointMapping() {
		return new CamelEndpointMapping();
	}

	@Bean
	@ConditionalOnClass(value = DataSource.class)
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:sql/create-tables.sql")
				.addScript("classpath:sql/insert-data.sql")
				.build();
	}

	public static void main(String[] args) {
		// clean previous states
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
