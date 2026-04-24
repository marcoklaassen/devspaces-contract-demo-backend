package org.acme;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class ContractListenerIT {

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Inject
    ContractRepository contractRepository;

    @BeforeEach
    @Transactional
    public void cleanUp() {
        contractRepository.deleteAll();
    }

    @Test
    public void testContractConsumption() {
        String jsonPayload = "{\"type\":\"car\",\"customer\":\"felix\"}";

        companion.produce(String.class, String.class)
                .fromRecords(new ProducerRecord<>("contract", jsonPayload))
                .awaitCompletion();

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    List<Contract> contracts = QuarkusTransaction.requiringNew()
                            .call(() -> contractRepository.listAll());
                    assertFalse(contracts.isEmpty());
                    assertEquals("felix", contracts.get(0).customer);
                    assertEquals("car", contracts.get(0).type);
                });
    }
}