package dev.onat.onalps.config.third.fal;

import ai.fal.client.AsyncFalClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FalConfig {

    @Bean
    public AsyncFalClient asyncFalClient() {
        return AsyncFalClient.withEnvCredentials();
    }
}
