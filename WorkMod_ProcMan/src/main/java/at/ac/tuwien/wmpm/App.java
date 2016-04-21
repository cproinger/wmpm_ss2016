package at.ac.tuwien.wmpm;

import java.io.File;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
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

@SpringBootApplication
@EnableJms
@EnableWs
@PropertySource(value = { "classpath:/config/db.properties" })
public class App extends RepositoryRestMvcConfiguration {

	@Autowired
	private CamelContext camelContext;

	// @Bean(initMethod = "start")
	// public BrokerService broker() throws Exception {
	// BrokerService broker = new BrokerService();
	// broker.setPersistent(false);
	// broker.addConnector("tcp://localhost:9876");
	// return broker;
	// }

	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	/**
	 * http://localhost:8080/ws/countries.wsdl
	 */
	@Bean(name = "countries")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CountriesPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("schema/countries.xsd"));
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
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:sql/create-tables.sql").addScript("classpath:sql/insert-data.sql").build();
	}

	public static void main(String[] args) {
		// clean previous states
		FileSystemUtils.deleteRecursively(new File("activemq-data"));

		ConfigurableApplicationContext app = SpringApplication.run(App.class);
		assert app != null;
		CamelSpringBootApplicationController applicationController = app
				.getBean(CamelSpringBootApplicationController.class);
		// applicationController.blockMainThread();
	}
}
