package at.ac.tuwien.wmpm;

import org.springframework.stereotype.Component;

import io.spring.guides.gs_producing_web_service.Country;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

@Component
public class ResponseFactory {
	public GetCountryResponse createCountryResponse(Country c) {
		GetCountryResponse r = new GetCountryResponse();
		r.setCountry(c);
		return r;
	}
}