package net.exposedrecords.web.configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.exposedrecords.web.configuration.ApplicationSettings.Persistence;
import net.exposedrecords.web.domain.SubscriptionRepository;
import net.exposedrecords.web.repository.InMemorySubscriptionRepository;
import net.exposedrecords.web.repository.MongoDBSubscriptionRepository;

@Configuration
public class SubscriptionRepositoryFactory {

    private Persistence persistence;
    private SubscriptionRepository subscriptionRespository;

    @Resource
    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.persistence = applicationSettings.getPersistence();
    }

    @PostConstruct
    public void init() {
        switch (persistence) {
        case InMemory:
            subscriptionRespository = new InMemorySubscriptionRepository();
            break;
        case MongoDB:
            subscriptionRespository = new MongoDBSubscriptionRepository();
            break;
        }
    }

    @Bean
    public SubscriptionRepository getSubscriptionRepository() {
        return subscriptionRespository;
    }
}
