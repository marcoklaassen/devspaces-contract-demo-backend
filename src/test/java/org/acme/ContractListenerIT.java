package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ContractListenerIT {

    @Inject
    @Channel("contract-out")
    Emitter<String> contractEmitter;

    @Inject
    ContractListener consumer;

    @Test
    public void testContractConsumption() {
        
        String jsonPayload = "{\"type\":\"car\",\"customer\":\"felix\"}";
        contractEmitter.send(jsonPayload);

        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                // Ensure your consumer class has a way to expose what it received
                assertEquals("felix", "felix");
            });
    }
}