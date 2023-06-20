
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
//import org.json.simple.JSONObject;

@Path("/findRelatedFacts")
public class FindRelatedFacts extends ArrayList<String> {

    Functionality func= new Functionality();

    @GET
    @Path("/")
    @Produces({"application/n-triples"})
    public Response getE(@QueryParam("text") String text, @QueryParam("ERtools") String tool, @QueryParam("keyEntity") String keyEntity, @HeaderParam("Accept") String acceptHeader) {
        Response.Status status = Response.Status.OK;
        System.out.println(tool);
        if(text==null){
             status = Response.Status.NOT_FOUND;
          //   JSONObject job=new JSONObject();
           //  job.put("error","Unknown Query Parameter. Only the following query parameters are accepted: [uri,provenance (optional)]");
            return Response.status(status).type("application/n-triples").entity("").build();
        }
        else{
            String entity="";
            if(keyEntity!=null){
                entity=keyEntity;
            }
            String output="";
            try {
                output = func.FindRelatedFacts(text, tool,entity);
 
            } catch (IOException ex) {
                Logger.getLogger(FindRelatedFacts.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(FindRelatedFacts.class.getName()).log(Level.SEVERE, null, ex);
            }
           
                return Response.status(status).type("text/tsv").entity(output).build();
       
        }

    }
}
