package at.ac.tuwien.wmpm;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(CamelConfig.class)
public class CamelConfigTest {

	@Produce(uri = CamelConfig.DIRECT_REMOVE_PERSONAL_INFORMATION)
	private ProducerTemplate removePersonalInformationRoute;
	
	@Test
	public void test() {
		
		/* TODO Task 1. 
		 * 		define any class that has a vote-field
		 * 		and other fields. 
		 * 		
		 * 		given an objects of such a class 
		 * 		when sent to this route
		 * 		then only the content of the vote field is stored in mongodb
		 */
		removePersonalInformationRoute.sendBody("This is an ex-parrot!");
	}
}
