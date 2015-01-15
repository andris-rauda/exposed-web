package net.exposedrecords.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClientOptions;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoClientOptions mongoClientOptions() {
        // override to more reasonable connection timeout (default is 10 seconds)
        return MongoClientOptions.builder().connectTimeout(1500).build();
    }
}
