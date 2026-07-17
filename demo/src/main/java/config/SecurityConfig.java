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

        if (serviceAccount == null) {
            throw new RuntimeException("serviceAccountKey.json not found in resources!");
        }

        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseId("firestore01") // <-- ADD THIS LINE WITH YOUR DATABASE ID
                .build();

        return firestoreOptions.getService();
    }
}
