package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.*;
import java.util.logging.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.xml.bind.JAXBElement;

public class TaskDataResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String keyname;

  public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.keyname = kname;
  }
  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public TaskData getTaskDataHTML() {
    //add your code here (get Entity from datastore using this.keyname)
    // throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    //if not found
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  Key reqKey = KeyFactory.createKey("TaskData", this.keyname);
	  Entity reqEntity;
	  try{
		  reqEntity = datastore.get(reqKey);
		  String value = (String)reqEntity.getProperty("value");
		  Date date = (Date)reqEntity.getProperty("date");
		  return new TaskData(keyname,value,date);
	  } catch (Exception e){
		  throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
	  }
  }
  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public TaskData getTaskData() {
    //same code as above method
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  Key reqKey = KeyFactory.createKey("TaskData", this.keyname);
	  Entity reqEntity;
	  try{
		  reqEntity = datastore.get(reqKey);
		  String value = (String)reqEntity.getProperty("value");
		  Date date = (Date)reqEntity.getProperty("date");
		  return new TaskData(keyname,value,date);
	  } catch (Exception e){
		  throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
	  }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTaskData(String val) {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	Key reqKey = KeyFactory.createKey("TaskData", this.keyname);
    Response res = null;
    Entity putEntity;
    //add your code here
    //first check if the Entity exists in the datastore
    //if it is not, create it and 
    //signal that we created the entity in the datastore
    try{
    	putEntity = datastore.get(reqKey);
    	res = Response.noContent().build();
    } catch (Exception e){
    	putEntity = new Entity("TaskData", keyname);
    	res = Response.created(uriInfo.getAbsolutePath()).build();
    }    
    //else signal that we updated the entity
    putEntity.setProperty("value", val);
	putEntity.setProperty("date", new Date());
	datastore.put(putEntity);
	syncCache.put(keyname, putEntity);
	
    return res;
  }

  @DELETE
  public void deleteIt() {

    //delete an entity from the datastore
    //just print a message upon exception (don't throw)
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  Key reqKey = KeyFactory.createKey("TaskData", this.keyname);
	  
	  datastore.delete(reqKey);
    }
  } 