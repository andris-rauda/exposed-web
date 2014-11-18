package net.exposedrecords.web.repository;

import net.exposedrecords.web.domain.Subscription;
import net.exposedrecords.web.domain.SubscriptionRepository;

public class MongoDBSubscriptionRepository implements SubscriptionRepository {

    @Override
    public <S extends Subscription> S save(S entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <S extends Subscription> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Subscription findOne(String id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean exists(String id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterable<Subscription> findAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterable<Subscription> findAll(Iterable<String> ids) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(Subscription entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(Iterable<? extends Subscription> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
