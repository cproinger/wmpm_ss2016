package at.ac.tuwien.wmpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import io.spring.guides.gs_producing_web_service.Country;

@Repository
public class SampleCountryRepository {

	@Autowired
	private ApplicationContext ctx;

	private Logger log = LoggerFactory.getLogger(SampleCountryRepository.class);

	public Country findCountry(String name) {
		log.info("called "  + (ctx == null));
		Country c = new Country();
		c.setName(name);
		return c;
	}
}
