package config;

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

        if (serviceAccount == null) {
            throw new RuntimeException("serviceAccountKey.json not found in resources!");
        }


        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccount);

        return BigQueryOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(credentials.getProjectId())
                .build()
                .getService();
    }
}