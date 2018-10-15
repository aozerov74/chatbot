package com.chatbot.api;

import com.chatbot.engine.ChatbotResponse;
import com.chatbot.engine.QueryServer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/chatbots")
public class ChatbotResource {
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public Response healthCheck() {
        return Response.ok("Ok").build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response query(@QueryParam("queryType") int queryType, @QueryParam("query") String query) {
        ChatbotResponse response = QueryServer.getResponse(queryType, query);
        return Response.ok(response).build();
    }
}
