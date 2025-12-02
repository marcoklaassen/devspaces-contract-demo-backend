package org.acme;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContractListener {

    @Inject
    ContractResource contractResource;

    @Incoming("contract")
    public void consume(Contract contract) {
        contractResource.create(contract);
    }

}
