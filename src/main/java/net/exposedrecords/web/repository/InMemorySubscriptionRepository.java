package net.exposedrecords.web.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.exposedrecords.web.domain.Subscription;
import net.exposedrecords.web.domain.SubscriptionRepository;

import org.springframework.stereotype.Repository;

@Repository
public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private Map<String, Subscription> map = Collections
            .synchronizedMap(new HashMap<String, Subscription>());

    @Override
    public <S extends Subscription> S save(S entity) {
        map.put(entity.getEmail(), entity);
        return entity;
    }

    @Override
    public <S extends Subscription> Iterable<S> save(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public Subscription findOne(String id) {
        return map.get(id);
    }

    @Override
    public boolean exists(String id) {
        return map.containsKey(id);
    }

    @Override
    public Iterable<Subscription> findAll() {
        return map.values();
    }

    @Override
    public Iterable<Subscription> findAll(Iterable<String> ids) {
        List<Subscription> list = new ArrayList<Subscription>();
        for (String id : ids) {
            list.add(map.get(id));
        }
        return list;
    }

    @Override
    public long count() {
        return map.size();
    }

    @Override
    public void delete(String id) {
        map.remove(id);
    }

    @Override
    public void delete(Subscription entity) {
        map.remove(entity.getEmail());
    }

    @Override
    public void delete(Iterable<? extends Subscription> entities) {
        for (Subscription entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
