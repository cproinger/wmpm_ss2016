package at.ac.tuwien.wmpm.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("slackConstructor")
public class SlackConstructorImpl implements SlackConstructor {

	@Override
	public String marshal(List<Map<String, Object>> data){
		//the list 
		long voteSum = data.stream().mapToLong(e -> ((Long)e.get("vote_count")).longValue())
			.sum();
		StringBuilder sb = new StringBuilder("Current Projection:\n");
		if(voteSum == 0)
			sb.append("Votes not counted yet. ");
		for(Map<String, Object> entry : data) {
			String candidate = (String) entry.get("candidate");
			Long voteCount = (Long) entry.get("vote_count");
			double sumVote = ((((double)voteCount) / ((double)voteSum)) * 100) ;

			sb.append(candidate).append(" ")
			.append(String.format("%.2f", sumVote))
			.append(" % (")
			.append(voteCount)
			.append(")\n");
		}
		
		//return ;
		return sb.toString();//"Trump 64 %, Hillary 123 %";
	}
}
