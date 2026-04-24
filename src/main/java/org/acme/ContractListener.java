package org.acme;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContractListener {

    @Inject
    ContractResource contractResource;

    @Incoming("contract")
    @Blocking
    public void consume(Contract contract) {
        contractResource.create(contract);
    }

}
