package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.*;
import java.util.Date;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usr = request.getParameter("usr");
        String content = request.getParameter("content");
        String type = request.getParameter("type");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String options = request.getParameter("options");
        // Do something with key. Put a new entity to datastore, also memcache
        DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        
        System.out.println("------------>" + type);
        
        Date date = new Date();
        String keyname = usr + date;
        Entity newEntity = new Entity("Message", keyname);
        
		newEntity.setProperty("usr", usr);
		newEntity.setProperty("content", content);
		newEntity.setProperty("type", Integer.parseInt(type));
		newEntity.setProperty("date", date);
		newEntity.setProperty("longitude", Double.parseDouble(longitude));
		newEntity.setProperty("latitude", Double.parseDouble(latitude));
		newEntity.setProperty("options", options);
        
  	  	dataStore.put(newEntity);
  	  	syncCache.put(keyname, newEntity);
    }
}