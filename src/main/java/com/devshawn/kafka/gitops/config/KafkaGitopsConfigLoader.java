package com.devshawn.kafka.gitops.config;

import com.devshawn.kafka.gitops.exception.MissingConfigurationException;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaGitopsConfigLoader {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(KafkaGitopsConfigLoader.class);

    public static KafkaGitopsConfig load() {
        KafkaGitopsConfig.Builder builder = new KafkaGitopsConfig.Builder();
        setConfig(builder);
        return builder.build();
    }

    private static void setConfig(KafkaGitopsConfig.Builder builder) {
        Map<String, Object> config = new HashMap<>();
        AtomicReference<String> username = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();

        Map<String, String> environment = System.getenv();

        environment.forEach((key, value) -> {
            if (key.equals("KAFKA_SASL_JAAS_USERNAME")) {
                username.set(value);
            } else if (key.equals("KAFKA_SASL_JAAS_PASSWORD")) {
                password.set(value);
            } else if (key.startsWith("KAFKA_")) {
                String newKey = key.substring(6).replace("_", ".").toLowerCase();
                config.put(newKey, value);
            }
        });

        handleDefaultConfig(config);
        handleAuthentication(username, password, config);

        log.info("Kafka Config: {}", config);

        builder.putAllConfig(config);
    }

    private static void handleDefaultConfig(Map<String, Object> config) {
        if (!config.containsKey(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG)) {
            config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        }

        if (!config.containsKey(CommonClientConfigs.CLIENT_ID_CONFIG)) {
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, "kafka-gitops");
        }
    }

    private static void handleAuthentication(AtomicReference<String> username, AtomicReference<String> password, Map<String, Object> config) {
        if (username.get() != null && password.get() != null) {
            String value = String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                    username.get(), password.get());
            config.put("sasl.jaas.config", value);
        } else if (username.get() != null) {
            throw new MissingConfigurationException("KAFKA_SASL_JAAS_PASSWORD");
        } else if (password.get() != null) {
            throw new MissingConfigurationException("KAFKA_SASL_JAAS_USERNAME");
        }
    }
}
