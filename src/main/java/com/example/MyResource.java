package com.example;

import com.example.Biz.Utils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class MyResource {
    @GET
    @Path("/seq/num-objects/{numObjects}/delay/{delay}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getItSequential(
        @PathParam("numObjects") Integer numObjects,
        @PathParam("delay") Integer delay)
    {
        return Utils.computeSequentially(numObjects, delay);
    }

    @GET
    @Path("/concurrent/num-objects/{numObjects}/delay/{delay}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getItConcurrent(
        @PathParam("numObjects") Integer numObjects,
        @PathParam("delay") Integer delay)
        {
            return Utils.computeWithMultiThreaded(numObjects, delay);
        }


    @GET
    @Path("/fork-join/num-objects/{numObjects}/delay/{delay}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getItForkJoin(
            @PathParam("numObjects") Integer numObjects,
            @PathParam("delay") Integer delay)
    {
        return Utils.computeWithForkJoin(numObjects, delay);
    }

    @GET
    @Path("/write/fork-join/num-objects/{numObjects}/lines/{lines}")
    @Produces(MediaType.TEXT_PLAIN)
    public String writeForkJoin(
            @PathParam("numObjects") Integer numObjects,
            @PathParam("lines") Integer lines)
    {
        return Utils.writeWithForkJoin(numObjects, lines);
    }

    @GET
    @Path("/write/seq/num-objects/{numObjects}/lines/{lines}")
    @Produces(MediaType.TEXT_PLAIN)
    public String writeSequentially(
            @PathParam("numObjects") Integer numObjects,
            @PathParam("lines") Integer lines)
    {
        return Utils.writeSequntially(numObjects, lines);
    }
}
