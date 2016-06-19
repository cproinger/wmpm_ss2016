package at.ac.tuwien.wmpm.service;

import java.util.List;
import java.util.Map;

public interface SlackConstructor {
	
	String marshal(List<Map<String, Object>> data);
}
