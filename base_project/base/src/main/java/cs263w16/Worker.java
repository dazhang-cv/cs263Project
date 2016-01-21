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
        String key = request.getParameter("keyname");
        String value = request.getParameter("value");
        // Do something with key. Put a new entity to datastore, also memcache
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        
        Entity reqEntity = new Entity("TaskData", key);
        
        reqEntity.setProperty("value", value);
  	  	reqEntity.setProperty("date", new Date());
        
  	  	datastore.put(reqEntity);
  	  	syncCache.put(key, reqEntity);
    }
}