package at.ac.tuwien.wmpm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.slack.SlackComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import at.ac.tuwien.wmpm.ss2016.VoteRequest;

@Configuration
@EnableJms
public class MessagingConfig {
	
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

	@Value(value = "${slack.url}")
	private String slackUrl;

	@Bean
	public SlackComponent slack() {
		SlackComponent sc = new SlackComponent();
		sc.setWebhookUrl(slackUrl);
		return sc;
	}
}
