package config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
@Configuration
public class SecurityConfig {
    @Bean
    public Firestore firestore() throws Exception {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        FirestoreOptions.Builder builder = FirestoreOptions.getDefaultInstance().toBuilder();

        if (serviceAccount != null) {
            // Used for local development in IntelliJ
            builder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
        } else {
            // Used automatically when running on Cloud Run
            String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
            if (projectId == null) {
                projectId = com.google.cloud.ServiceOptions.getDefaultProjectId();
            }
            builder.setCredentials(GoogleCredentials.getApplicationDefault())
                    .setProjectId(projectId);
        }

        builder.setDatabaseId("firestore01");
        return builder.build().getService();
    }
}
