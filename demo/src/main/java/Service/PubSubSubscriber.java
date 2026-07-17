package Service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PubSubSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(PubSubSubscriber.class);
    private final PubSubTemplate pubSubTemplate;

    private static final String SUBSCRIPTION_NAME = "topic-1-sub";

    @Autowired
    public PubSubSubscriber(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void subscribeToQueue() {
        logger.info("Registering background listener for subscription: {}", SUBSCRIPTION_NAME);

        pubSubTemplate.subscribe(SUBSCRIPTION_NAME, basicAckMessage -> {
            String payload = basicAckMessage.getPubsubMessage().getData().toStringUtf8();
            logger.info("Subscriber thread received message from {}: {}", SUBSCRIPTION_NAME, payload);

            try {
                processMessage(payload);
                // Acknowledge on success to clean from queue
               // basicAckMessage.ack();
            } catch (Exception e) {
                logger.error("Error processing message. Returning to queue.", e);
                // Send back to queue for retry
                basicAckMessage.nack();
            }
        });
    }

    private void processMessage(String payload) {
        logger.info("Successfully processed payload internally: {}", payload);
    }
}
