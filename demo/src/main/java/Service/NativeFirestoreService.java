package Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class NativeFirestoreService {
    private Firestore firestore;
    @Autowired
    NativeFirestoreService(Firestore firestore){
        this.firestore=firestore;
    }

    public String saveSystemData(String collection, String documentId) throws ExecutionException, InterruptedException {

        Map<String, Object> payload = new HashMap<>();
        payload.put("threads", 12);
        payload.put("timestamp", System.currentTimeMillis());

        ApiFuture<WriteResult> futureResult = firestore.collection(collection)
                .document(documentId)
                .set(payload);
        WriteResult result = futureResult.get();
        return "data inserted into firestore successfully"+result.getUpdateTime();
    }

    public List<Map<String, Object>> getAllDocuments() throws ExecutionException, InterruptedException {
        // Replace "your_collection_name" with the actual name of your Firestore collection
        ApiFuture<QuerySnapshot> future = firestore.collection("DemoSchool01").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(QueryDocumentSnapshot::getData)
                .collect(Collectors.toList());
    }
}
