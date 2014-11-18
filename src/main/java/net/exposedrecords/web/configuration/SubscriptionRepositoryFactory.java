package net.exposedrecords.web.configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.exposedrecords.web.configuration.Environment.Persistence;
import net.exposedrecords.web.domain.SubscriptionRepository;
import net.exposedrecords.web.repository.AppEngineSubscriptionRepository;
import net.exposedrecords.web.repository.InMemorySubscriptionRepository;
import net.exposedrecords.web.repository.MongoDBSubscriptionRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionRepositoryFactory {

    private Environment environment;
    private SubscriptionRepository subscriptionRespository;

    @Resource
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        if (environment.getPersistence() == Persistence.InMemory) {
            subscriptionRespository = new InMemorySubscriptionRepository();
        } else if (environment.getPersistence() == Persistence.AppEngine) {
            subscriptionRespository = new AppEngineSubscriptionRepository();
        } else if (environment.getPersistence() == Persistence.MongoDB) {
            subscriptionRespository = new MongoDBSubscriptionRepository();
        }
    }

    @Bean
    public SubscriptionRepository getSubscriptionRepository() {
        return subscriptionRespository;
    }
}
