package net.exposedrecords.web.domain;

import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends
        CrudRepository<Subscription, String> {

    // nothing special here
}
