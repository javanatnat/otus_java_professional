package ru.otus.antibruteforce.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "cash.schedulingEnabled",
        havingValue = "true",
        matchIfMissing = true)
public class SchedulingConfig {
}
