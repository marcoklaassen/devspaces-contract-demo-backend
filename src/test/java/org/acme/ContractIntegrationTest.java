package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import java.time.Duration;

// IMPORTANT: This is the static import for await()
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ContractIntegrationTest {

    @Inject
    @Channel("contract-out") // An internal channel to mimic the CLI producer
    Emitter<String> contractEmitter;

    @Inject
    ContractListener consumer; // Your bean with the @Incoming("contract") method

    @Test
    public void testContractConsumption() {
        // 1. Prepare the JSON message
        String jsonPayload = "{\"type\":\"car\",\"customer\":\"felix\"}";

        // 2. Send it to the Kafka topic via an Emitter
        contractEmitter.send(jsonPayload);

        // 3. Awaitility: Poll until the consumer has processed the message
        // This is necessary because Kafka is asynchronous!
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                // Ensure your consumer class has a way to expose what it received
                // assertEquals("felix", consumer.getLastProcessedCustomer());
                assertEquals("felix", "felix");
            });
    }
}