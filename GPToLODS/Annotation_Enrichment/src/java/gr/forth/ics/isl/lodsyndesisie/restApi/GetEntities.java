
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

@Path("/getEntities")
public class GetEntities extends ArrayList<String> {

    Functionality func = new Functionality();

    @GET
    @Path("/")
    @Produces({"text/tsv", "application/n-triples"})
    public Response getE(@QueryParam("text") String text, @QueryParam("ERtools") String tool, @QueryParam("equivalentURIs") String equiv, @QueryParam("provenance") String prov, @HeaderParam("Accept") String acceptHeader) {
        Response.Status status = Response.Status.OK;
        if (text == null) {
            status = Response.Status.NOT_FOUND;
            //   JSONObject job=new JSONObject();
            //  job.put("error","Unknown Query Parameter. Only the following query parameters are accepted: [uri,provenance (optional)]");
            return Response.status(status).type("text/tsv").entity("Error").build();
        } else {
            boolean equivalent = false, provenance = false;

            if (equiv != null && equiv.equals("true")) {
                equivalent = true;
            }
            if (prov != null && prov.equals("true")) {
                provenance = true;
            }
            String[][] entities;
            String triple = "";
            if (acceptHeader != null) {
                try {
                    entities = func.getEntities(text, tool, equivalent, provenance);
//                    triple += "Entity\tDBpedia URI\tLODsyndesisURI";
//                    if (equivalent == true) {
//                        triple += "\tEquivalentURI";
//                    }
//                    if (provenance == true) {
//                        triple += "\tProvevance";
//                    }
                   // triple += "\n";
                    for (int i = 0; i < entities.length; i++) {
                        triple += entities[i][0] + "\t" + entities[i][1]+ "\t" + entities[i][2];//+"\n";
                        if (equivalent == true) {
                            triple += "\t" + entities[i][3];
                        }
                        if (provenance == true && equivalent == true) {
                            triple += "\t" + entities[i][4];
                        } else if (provenance == true) {
                            triple += "\t" + entities[i][3];
                        }
                        triple += "\n";
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GetEntities.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JSONException ex) {
                    Logger.getLogger(GetEntities.class.getName()).log(Level.SEVERE, null, ex);
                }

                return Response.status(status).type("text/tsv").entity(triple).build();
            } else if (acceptHeader.equalsIgnoreCase("application/n-triples")) {
                String result = "";
                try {
                    result = func.getEntitiesRDF(text, tool, equivalent, provenance);
                } catch (IOException ex) {
                    Logger.getLogger(GetEntities.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JSONException ex) {
                    Logger.getLogger(GetEntities.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Response.status(status).type("application/n-triples").entity(result).build();

            }
        }
//        else{
//            if (acceptHeader != null && (acceptHeader.equalsIgnoreCase("application/xml"))) {
//                List<Entity> triples = all.getURIsXML(uri);
//                GenericEntity<List<Entity>> list = new GenericEntity<List<Entity>>(triples) {
//                };
//                return Response.status(status).type(MediaType.APPLICATION_XML).entity(list).build();
//            } 
//            else if (acceptHeader != null && (acceptHeader.equalsIgnoreCase("application/json"))) {
//                String triples = all.getURIsJson(uri);
//                
//                return  Response.status(status).type(MediaType.APPLICATION_JSON).entity(triples).build();
//            } 
//            
//            else {
//                return Response.status(status).type("application/n-triples").entity(all.getURIsPlain(uri)).build();
//                //return Response.status(status).entity(all.getURIsPlain(uri)).build();
//            }
//        }
        return null;
    }
}
