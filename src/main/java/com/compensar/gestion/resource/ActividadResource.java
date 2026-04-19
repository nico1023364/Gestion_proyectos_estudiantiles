package com.compensar.gestion.resource;

import com.compensar.gestion.model.Actividad;
import com.compensar.gestion.service.ActividadService;
import com.compensar.gestion.service.BusinessException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadResource {

    private final ActividadService service = new ActividadService();

    @GET
    public Response listar() {
        return Response.ok(service.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        try {
            return Response.ok(service.buscarPorId(id)).build();
        } catch (BusinessException e) {
            return error(Response.Status.NOT_FOUND, e.getMessage());
        }
    }

    @POST
    public Response crear(Actividad actividad) {
        try {
            return Response.status(Response.Status.CREATED).entity(service.crear(actividad)).build();
        } catch (BusinessException e) {
            return error(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Long id, Actividad actividad) {
        try {
            return Response.ok(service.actualizar(id, actividad)).build();
        } catch (BusinessException e) {
            Response.Status estado = e.getMessage().contains("No existe") ? Response.Status.NOT_FOUND : Response.Status.BAD_REQUEST;
            return error(estado, e.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        try {
            service.eliminar(id);
            return Response.ok(Map.of("mensaje", "Actividad eliminada correctamente.")).build();
        } catch (BusinessException e) {
            Response.Status estado = e.getMessage().contains("No existe") ? Response.Status.NOT_FOUND : Response.Status.BAD_REQUEST;
            return error(estado, e.getMessage());
        }
    }

    private Response error(Response.Status status, String mensaje) {
        return Response.status(status).entity(Map.of("error", mensaje)).build();
    }
}
