package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.model.Client;

public class HwListenerClient implements HwListener<String, Client> {
    private static final Logger logger = LoggerFactory.getLogger(HwListenerClient.class);

    @Override
    public void notify(String key, Client value, String action) {
        logger.info("key:{}, value:{}, action: {}", key, value, action);
    }

    @Override
    public String toString() {
        return "HwListener<String, Client>()";
    }
}
