package Service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PubSubPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PubSubPublisher.class);
    private final PubSubTemplate pubSubTemplate;

    @Autowired
    public PubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public CompletableFuture<String> publish(String topicName, String payload) {
        logger.info("Publishing message to topic {}: {}", topicName, payload);

        CompletableFuture<String> future = pubSubTemplate.publish(topicName, payload);

        future.whenComplete((messageId, ex) -> {
            if (ex != null) {
                logger.error("Failed to publish message to topic: {}", topicName, ex);
            } else {
                logger.info("Successfully published message with ID: {}", messageId);
            }
        });

        return future;
    }
}