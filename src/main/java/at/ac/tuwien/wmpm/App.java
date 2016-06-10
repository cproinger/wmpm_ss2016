
package at.ac.tuwien.wmpm;

import java.io.File;

import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;

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