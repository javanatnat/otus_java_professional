package ru.otus.cachehw;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCache.class);

    private static final String PUT = "put";
    private static final String REMOVE = "remove";
    private static final String GET = "get";

    private final Map<K,V> cache;
    private final List<WeakReference<HwListener<K,V>>> listeners;

    public MyCache() {
        cache = new WeakHashMap<>();
        listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key);

        cache.put(key, value);
        notifyListeners(key, value, PUT);
    }

    @Override
    public void remove(K key) {
        Objects.requireNonNull(key);

        V value = cache.get(key);
        cache.remove(key);

        notifyListeners(key, value, REMOVE);
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key);

        V value = cache.get(key);
        notifyListeners(key, value, GET);

        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        Objects.requireNonNull(listener);

        var ref = new WeakReference<>(listener);
        listeners.add(ref);

        LOGGER.info("listeners count after add: {}", listeners.size());
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        Objects.requireNonNull(listener);

        WeakReference<HwListener<K,V>> findRef = findWeakListener(listener);
        removeWeakListener(findRef);

        LOGGER.info("listeners count after remove: {}", listeners.size());
    }

    private WeakReference<HwListener<K,V>> findWeakListener(HwListener<K,V> listener) {
        for (var ref : listeners) {
            if (ref.refersTo(listener)) {
                return ref;
            }
        }
        return null;
    }

    private void removeWeakListener(WeakReference<HwListener<K,V>> ref) {
        if (ref != null) {
            listeners.remove(ref);
        }
    }

    private void notifyListeners(K key, V value, String action) {
        for (var ref : listeners) {
            HwListener<K,V> listener = ref.get();
            if (listener != null) {
                listener.notify(key, value, action);
            }
        }
        LOGGER.info("notify all listeners, action: " + action);
    }
}
