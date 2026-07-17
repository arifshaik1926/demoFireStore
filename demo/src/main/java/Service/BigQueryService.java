package Service;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.TableId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BigQueryService {

    private final BigQuery bigQuery;

    public BigQueryService(BigQuery bigQuery) {
        this.bigQuery = bigQuery;
    }

    public void insertNewCustomer(String customerId, String email, double totalSpend) {
        // 1. Target your specific dataset and table
        TableId tableId = TableId.of("retail_analytics", "customers");

        // 2. Map the fields to match your schema exact column names
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("customer_id", customerId);
        rowContent.put("email", email);
        rowContent.put("total_spend", totalSpend);

        // 3. Package the row and stream it into the table
        InsertAllResponse response = bigQuery.insertAll(
                InsertAllRequest.newBuilder(tableId)
                        .addRow(rowContent)
                        .build()
        );

        // 4. Verify if the streaming insertion encountered row errors
        if (response.hasErrors()) {
            response.getInsertErrors().forEach((rowIndex, errors) -> {
                System.err.println("Row " + rowIndex + " failed injection: " + errors);
            });
            throw new RuntimeException("Failed to stream data into BigQuery table.");
        } else {
            System.out.println("Customer data streamed successfully into retail_analytics.customers!");
        }
    }
}
