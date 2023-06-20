
/*  This code belongs to the Semantic Access and Retrieval (SAR) group of the
 *  Information Systems Laboratory (ISL) of the
 *  Institute of Computer Science (ICS) of the
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.lodsyndesisie.restApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;

@Path("/textEntitiesDatasetDiscovery")
public class TextEntitiesDatasetDiscovery extends ArrayList<String> {

    Functionality func = new Functionality();

    @GET
    @Path("/")
    @Produces({"text/csv"})
    public Response getTriples(
            @QueryParam("text") String text,
            @QueryParam("ERtools") String tools,
            @QueryParam("resultsNumber") int topK,
            @QueryParam("subsetK") int subsetSize,
            @QueryParam("measurementType") String type,
            @HeaderParam("Accept") String acceptHeader) {
        Response.Status status = Response.Status.OK;
        System.out.println(acceptHeader);
        if (text == null) {
            return Response.status(status).type("text/csv").entity("Error").build();
        }

        String output = "";
        if(subsetSize<=0){
           subsetSize=3;
        }
        if(topK<=0){
           topK=10;
        }
        if(type==null){
            type="coverage";
        }
           
        try {
            output=func.textEntitiesDD(text,tools,subsetSize,topK,type);
        } catch (IOException ex) {
            Logger.getLogger(TextEntitiesDatasetDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(TextEntitiesDatasetDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(status).type("text/csv").entity(output).build();

//            }
    }

}
