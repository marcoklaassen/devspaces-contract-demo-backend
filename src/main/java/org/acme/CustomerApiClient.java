package org.acme;

import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "customer-api")
@RegisterClientHeaders
@Path("/customer")
@Produces("application/json")
@Consumes("application/json")
public interface CustomerApiClient {

    @GET
    @Path("/name")
    List<Customer> getCustomerByName(@QueryParam("name") String name);

    @POST
    Customer createCustomer(Customer customer);
}

