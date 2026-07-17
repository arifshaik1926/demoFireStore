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
            InputStream is = getClass().getClassLoader().getResourceAsStream(KEY_FILE_NAME);
            if (is != null) {
                try (is) {
                    return GoogleCredentials.fromStream(is);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load local key", e);
                }
            }
            try {
                return GoogleCredentials.getApplicationDefault();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load application default credentials", e);
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

                String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
                if (projectId == null) {
                    projectId = com.google.cloud.ServiceOptions.getDefaultProjectId();
                }
                return projectId;
            } catch (IOException e) {
                throw new RuntimeException("Failed to extract Project ID", e);
            }
        };
    }
}