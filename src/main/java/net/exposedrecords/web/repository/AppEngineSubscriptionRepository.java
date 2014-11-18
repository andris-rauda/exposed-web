package net.exposedrecords.web.repository;

import java.util.Date;

import net.exposedrecords.web.domain.Subscription;
import net.exposedrecords.web.domain.SubscriptionRepository;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class AppEngineSubscriptionRepository implements SubscriptionRepository {

    private static final String ENTITY_NAME = Subscription.class
            .getSimpleName();

    @Override
    public <S extends Subscription> S save(S subscription) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity entity = new Entity(ENTITY_NAME, subscription.getEmail());
        entity.setProperty("creationDate", subscription.getCreationDate());
        entity.setProperty("verificationCode",
                subscription.getVerificationCode());
        entity.setProperty("verificationDate",
                subscription.getVerificationDate());

        datastore.put(entity);

        return subscription;
    }

    @Override
    public <S extends Subscription> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Subscription findOne(String email) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key key = KeyFactory.createKey(ENTITY_NAME, email);

        try {
            Entity entity = datastore.get(key);

            Subscription subscription = new Subscription();
            subscription.setEmail(entity.getKey().getName());
            subscription.setCreationDate((Date) entity
                    .getProperty("creationDate"));
            subscription.setVerificationCode((String) entity
                    .getProperty("verificationCode"));
            subscription.setVerificationDate((Date) entity
                    .getProperty("verificationDate"));

            return subscription;
        } catch (EntityNotFoundException e) {
            return null;
        }
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
    public void delete(String email) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key key = KeyFactory.createKey(ENTITY_NAME, email);
        datastore.delete(key);
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
