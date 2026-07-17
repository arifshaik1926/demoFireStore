package controller;

import Service.BigQueryService;
import Service.CustomerRequest;
import Service.NativeFirestoreService;

import Service.PubSubPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class FireStoreController {

    private final NativeFirestoreService nativeFirestoreService;
    private final BigQueryService bigQueryService;
    private final PubSubPublisher publisher;
    private static final String DEFAULT_TOPIC = "topic-1";


    @Autowired
    public  FireStoreController(NativeFirestoreService nativeFirestoreService, BigQueryService bigQueryService, PubSubPublisher publisher){
        this.nativeFirestoreService = nativeFirestoreService;
        this.bigQueryService = bigQueryService;
        this.publisher = publisher;
    }
    @GetMapping("firestore/{collection}/{documentId}")
    public ResponseEntity<String> addData(@PathVariable String collection,
                                          @PathVariable String documentId) {
        try {
            String response=nativeFirestoreService.saveSystemData(collection,documentId);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving to Firestore: " + e.getMessage());
        }

    }

    @PostMapping("/bigquery/customers")
    public ResponseEntity<String> BigqueryData(@RequestBody CustomerRequest request){
        bigQueryService.insertNewCustomer(request.customerId(),
                request.email(),
                request.spend());
        return ResponseEntity.ok("working");
    }
    @PostMapping("pubsub/publish")
    public ResponseEntity<String> sendPayload(@RequestBody String payload) {
        try {
            // Block temporarily in the controller to respond with the actual metadata
            String messageId = publisher.publish(DEFAULT_TOPIC, payload).get();
            return ResponseEntity.ok("Published to " + DEFAULT_TOPIC + " successfully. Message ID: " + messageId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            if (e.getCause() != null) {
                return ResponseEntity.status(500).body("Error: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
