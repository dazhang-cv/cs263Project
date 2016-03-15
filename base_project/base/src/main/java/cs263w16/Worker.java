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
        /**
         * Create a new query
         */
    	
    	String city = request.getParameter("city");
        String querytitle = request.getParameter("querytitle");
        float latitude = Float.valueOf(request.getParameter("latitude"));
        float longitude = Float.valueOf(request.getParameter("longitude"));
        GeoPt location = new GeoPt(latitude,longitude);
        String question = request.getParameter("question");
        
        // Build entity
        Key cityKey = KeyFactory.createKey("City",city);
        Entity query = new Entity("Query", querytitle, cityKey);
        query.setProperty("title", querytitle);
        query.setProperty("location", location);
        query.setProperty("city", city);
        query.setProperty("question", question);
        // Put to memcache
        
        // Save to datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(query);
    }
}