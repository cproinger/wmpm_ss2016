package at.ac.tuwien.wmpm;

import com.icegreen.greenmail.util.GreenMailUtil;

public class MailTool {

	public static void main(String[] args) {
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", "subject",
	            "Trump,40\nLugner,13"
	    );
	}
}
