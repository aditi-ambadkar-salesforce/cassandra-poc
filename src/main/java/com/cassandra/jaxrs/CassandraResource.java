package com.cassandra.jaxrs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/cassandra")
public class CassandraResource {
    private final CqlSession session;

    public CassandraResource() {
        this.session = CqlSession.builder()
                .withKeyspace("riq2")  // Replace with your actual keyspace
                .build();
    }

    @GET
    @Path("/orgtimelinepage")
    @Produces("application/json")
    public Response fetchOrgTimelineItem(
            @QueryParam("id") String id,
            @QueryParam("page") String page,
            @QueryParam("time") String time,
            @QueryParam("type") String type,
            @QueryParam("hash") String hash) {

        if (id == null || page == null || time == null || type == null || hash == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All query parameters (id, page, time, type, hash) must be provided.")
                    .build();
        }

        String query = "SELECT * FROM orgtimelinepage WHERE id = ? AND page = ? AND time = ? AND type = ? AND hash = ? LIMIT 1;";

        // Create a prepared statement with parameters
        Statement<?> statement = session.prepare(query)
                .bind(id, page, time, type, hash);

        try {
            Row row = session.execute(statement).one();
            if (row != null) {
                // Convert the row to a suitable JSON object or Map to return
                // Here, we'll just return the row as a string for simplicity
                return Response.ok(row.toString()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No data found for the given parameters.")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error executing query: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/scoped_data_policy")
    @Produces("application/json")
    public Response getScopedDataPolicy(
            @QueryParam("scope_id") String scopeId,
            @QueryParam("id") String id) {

        if (scopeId == null || id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both 'scope_id' and 'id' query parameters are required.")
                    .build();
        }

        String query = "SELECT * FROM scoped_data_policy WHERE scope_id = ? AND id = ?;";

        // Prepare and bind the parameters to the query
        Statement<?> statement = session.prepare(query)
                .bind(scopeId, id);

        try {
            Row row = session.execute(statement).one();
            if (row != null) {
                // Return the row as a simple JSON string for demonstration
                return Response.ok(row.toString()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No data found for the given scope_id and id.")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error executing query: " + e.getMessage())
                    .build();
        }
    }

    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}