package Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
}
