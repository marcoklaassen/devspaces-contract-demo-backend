package org.acme;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Path("/contract")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ContractResource {

    @Inject
    ContractRepository contractRepository;

    @Inject
    @RestClient
    CustomerApiClient customerApiClient;

    private static final Logger LOGGER = Logger.getLogger(ContractResource.class.getName());

    @GET
    public List<Contract> get() {
        return contractRepository.listAll(Sort.by("type"));
    }

    @GET
    @Path("{id}")
    public Contract getSingle(Long id) {
        Contract entity = contractRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Contract with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Contract contract) {
        if (contract.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        if (contract.customer == null || contract.customer.isBlank()) {
            throw new WebApplicationException("Customer name must be provided.", 422);
        }

        // Check if customer already exists
        try {
            List<Customer> existingCustomers = customerApiClient.getCustomerByName(contract.customer);
            
            if (existingCustomers != null && !existingCustomers.isEmpty()) {
                // Customer exists, set firstContractOfCustomer to true as per requirement
                contract.firstContractOfCustomer = false;
                LOGGER.info("Customer " + contract.customer + " already exists. Setting firstContractOfCustomer to true.");
            } else {
                // Customer doesn't exist, create it and set firstContractOfCustomer to false
                Customer newCustomer = new Customer(contract.customer);
                customerApiClient.createCustomer(newCustomer);
                contract.firstContractOfCustomer = true;
                LOGGER.info("Created new customer " + contract.customer + ". Setting firstContractOfCustomer to false.");
            }
        } catch (Exception e) {
            LOGGER.error("Error checking/creating customer: " + e.getMessage(), e);
            throw new WebApplicationException("Failed to check or create customer: " + e.getMessage(), 500);
        }

        contractRepository.persist(contract);
        return Response.ok(contract).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Contract update(Long id, Contract contract) {
        if (contract.type == null && contract.customer == null) {
            throw new WebApplicationException("Contract Type and Name were not set on request.", 422);
        }

        Contract entity = contractRepository.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Contract with id of " + id + " does not exist.", 404);
        }

        if(contract.type != null){
            entity.type = contract.type;
        }

        if(contract.customer != null){
            entity.customer = contract.customer;
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        Contract entity = contractRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Contract with id of " + id + " does not exist.", 404);
        }
        contractRepository.delete(entity);
        return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}
