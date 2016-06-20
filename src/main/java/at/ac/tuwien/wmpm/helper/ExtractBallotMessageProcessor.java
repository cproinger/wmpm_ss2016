package at.ac.tuwien.wmpm.helper;

import org.apache.camel.Exchange;

public class ExtractBallotMessageProcessor implements org.apache.camel.Processor {
    @Override public void process(Exchange exchange) throws Exception {
        String message = exchange.getIn().getBody(String.class);
        int firstNewline = message.indexOf("\n");

        String id = message.substring(0, firstNewline).trim();
        String body = message.substring(firstNewline, message.length()).trim();

        exchange.getIn().setHeader("ballot_box_id", id.trim());
        exchange.getIn().setBody(body);
    }
}
