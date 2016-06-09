package at.ac.tuwien.wmpm;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@Profile("fakeMongo")
public class FakeMongoConfig extends AbstractMongoConfiguration {

//    @Autowired
//    public FakeMongoConfig(MongoDbFactory mongo, Environment env) {
//        this.mongo = mongo;
//        this.env = env;
//    }
	
    @Autowired
	private Environment env;

    @Override
    protected String getDatabaseName() {
    	String name = "test";
    	return name;
//        return env.getRequiredProperty("spring.data.mongodb.uri");
    }
	
	@Override
    public Mongo mongo() throws Exception {
		try {
			MongoClient client = new MongoClient();
			DB db = client.getDB(getDatabaseName());
			db.getStats();
			return db.getMongo();
		} catch (Exception e) {
			LoggerFactory.getLogger(FakeMongoConfig.class).warn("no db running, using in memory fake");
		}
		return new Fongo(getDatabaseName()).getMongo();
    }
}
