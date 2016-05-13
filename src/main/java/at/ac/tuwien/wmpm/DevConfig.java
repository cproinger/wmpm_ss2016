package at.ac.tuwien.wmpm;

import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Developer Resources/Services that should be replaced with
 * real ones in production. 
 *
 */
@Profile("dev")
@Configuration
public class DevConfig {

	
	@Bean(initMethod="start", destroyMethod="stop")
	public GreenMail greenmail() {
		GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);
		greenMail.setUser("to@localhost.com", "to@localhost.com");
		return greenMail;
	}
}
