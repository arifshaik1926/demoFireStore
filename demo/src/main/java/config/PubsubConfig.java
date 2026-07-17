package config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class PubsubConfig {

    private static final String KEY_FILE_NAME = "serviceAccountKey.json";

    @Bean
    public CredentialsProvider gcpCredentialsProvider() {
        return () -> {
            try (InputStream is = new ClassPathResource(KEY_FILE_NAME).getInputStream()) {
                return GoogleCredentials.fromStream(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load serviceAccountKey.json from resources", e);
            }
        };
    }

    @Bean
    public GcpProjectIdProvider gcpProjectIdProvider(CredentialsProvider credentialsProvider) {
        return () -> {
            try {
                GoogleCredentials credentials = (GoogleCredentials) credentialsProvider.getCredentials();
                if (credentials instanceof ServiceAccountCredentials) {
                    return ((ServiceAccountCredentials) credentials).getProjectId();
                }
                throw new IllegalStateException("Service account key does not contain a valid Project ID.");
            } catch (IOException e) {
                throw new RuntimeException("Failed to extract Project ID from credentials", e);
            }
        };
    }
}