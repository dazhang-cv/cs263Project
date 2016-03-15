package cs263w16;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Path("/query")
public class QueryRestApi{
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	/**
	 * Get query
	 * 
	 * @param querytitle The title of the query
	 * @param city The name of the city
	 * @return
	 */
	
	@GET
	@Path("/{city}/{querytitle}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getQueryInCityWithTitle(
			@PathParam("querytitle") String querytitle,
			@PathParam("city") String city) {
		// try to find the entity in datastore
		Key queryKey = new KeyFactory.Builder("City", city).addChild("Query",
				querytitle).getKey();
		Query q = new Query("Query", queryKey);
		Entity queryEntity = datastore.prepare(q).asSingleEntity();
		if (queryEntity == null) {
			return "";
		}
		return new Gson().toJson(queryEntity.getProperties());
	}
	
	/**
	 * Get all queries in city
	 * 
	 * @param city The name of the city
	 * @return All queries in city
	 */
	@GET
	@Path("/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getQueriesInCity(
			@PathParam("city") String city){
		city = city.toLowerCase();
		System.out.println(city);
		Key cityKey = KeyFactory.createKey("City", city);
		Query q = new Query("Query",cityKey);
		
		List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		
		List<Map<String,Object>> queries = new ArrayList<Map<String,Object>>();
		
		int count = 0;
		
		for (Entity entity:entities){
			count = count + 1;
			System.out.println(count + "");
			queries.add(entity.getProperties());
		}
		
		return new Gson().toJson(queries);
	}
	
	/**
	 * Create a new query in city
	 * @param city The name of the city
	 * @return
	 */
	
	@POST
	@Path("/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewQueryInCity(
			String body,
			@PathParam("city") String city){
		QueryData queryData;
		try{
			queryData = new Gson().fromJson(body, QueryData.class);
		} catch (JsonSyntaxException e){
			return Response.status(Status.BAD_REQUEST).build();
		}
		// Input Validation
		
		// Check if there is already a query with the given title in the given city
		Key queryKey = new KeyFactory.Builder("City",city).addChild("Query", queryData.title).getKey();
		Query q = new Query("Query",queryKey);
		Entity entity = datastore.prepare(q).asSingleEntity();
		if (entity != null){
			return Response.notModified().build();
		} else{
			// Send the task of creating the query to datastore to the task queue.
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/worker")
					.param("city", city)
					.param("querytitle", queryData.title)
					.param("latitude", String.valueOf(queryData.location.getLatitude()))
					.param("longitude", String.valueOf(queryData.location.getLongitude()))
					.param("question", queryData.question));
			
			try {
				return Response.created(URI.create("/rest/room/" + URLEncoder.encode(city,"UTF-8") + "/" + queryData.title)).build();
			} catch (UnsupportedEncodingException e){
				return Response.created(URI.create("/")).build();
			}
		}
	}
	
}