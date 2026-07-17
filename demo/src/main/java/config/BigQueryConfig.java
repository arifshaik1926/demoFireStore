package config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.InputStream;

@Configuration
public class BigQueryConfig {

    @Bean
    public BigQuery bigQuery() throws Exception {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        BigQueryOptions.Builder builder = BigQueryOptions.newBuilder();

        if (serviceAccount != null) {
            // Local fallback
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccount);
            builder.setCredentials(credentials)
                    .setProjectId(credentials.getProjectId());
        } else {
            // Cloud Run environment setup
            String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
            if (projectId == null) {
                projectId = com.google.cloud.ServiceOptions.getDefaultProjectId();
            }
            builder.setCredentials(GoogleCredentials.getApplicationDefault())
                    .setProjectId(projectId);
        }

        return builder.build().getService();
    }
}