package at.ac.tuwien.wmpm;

import javax.annotation.PostConstruct;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
public class Integration {

	private static Integration SINGLETON;
	
	@EndpointInject(uri = "jms:queue:simple")
	private ProducerTemplate producer;
	
	public void loaded(Long id) {
		producer.sendBody("loaded " + id);
	}
	
	@PostConstruct
	private void inited() {
		SINGLETON = this;
	}
	
	public static Integration get() {
		return SINGLETON;
	}
}
