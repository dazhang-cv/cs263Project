package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;



@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
      
      //resp.getWriter().println("<h2>Hello World?</h2>"); //remove this line
      if (req.getParameterNames().hasMoreElements() == false){
    	  // initiate a query to get all TaskData Entities
    	  Query q = new Query("TaskData");
    	  PreparedQuery pq = datastore.prepare(q);
    	  List<String> keyList = new ArrayList<String>();
    	  resp.getWriter().println("<h2>Datastore Entries</h2>");
    	  for (Entity result:pq.asIterable()){
    		  String keyStr = result.getKey().getName();
    		  keyList.add(keyStr);
    		  String value = (String)result.getProperty("value");
    		  Date date = (Date)result.getProperty("date");
    		  resp.getWriter().println("<p>TaskData(\"" + keyStr + "\") "+ keyStr + " " + date + " " + value +  "</p>");
    	  }
    	  // display the memCache entities
    	  resp.getWriter().println("<h2>Memcache Entries</h2>");
    	  for (String keyName:keyList){
    		  if (syncCache.get(keyName) != null){
    			  resp.getWriter().println("<p>key: " + keyName + " Entity(date,val): " + ((Entity) syncCache.get(keyName)).getProperty("date") + " " + ((Entity) syncCache.get(keyName)).getProperty("value") + "</p>");
    		  }
    	  }
      } else if (req.getParameter("keyname") != null && req.getParameter("value") == null){
    	  // get a TaskData Entity with a key name
    	  String reqStr = req.getParameter("keyname");
    	  Key reqKey = KeyFactory.createKey("TaskData", reqStr);
    	  Entity reqEntity;
    	  try{
    		  reqEntity = datastore.get(reqKey);
    		  String value = (String)reqEntity.getProperty("value");
    		  Date date = (Date)reqEntity.getProperty("date");
    		  // check if memcache has stored the data
    		  String tag = "DataStore";
    		  if (syncCache.get(reqStr) != null){
    			  tag = "Both";
    		  } else {
    			  syncCache.put(reqStr, reqEntity);
    		  }
    		  resp.getWriter().println("<p>key: " + reqStr + " Entity date: " + date + " value: " + value + " (" + tag + ")</p>");
    	  } catch (Exception e){
    		  resp.getWriter().println("<p>key: " + reqStr + " not found</p>");
    	  }
    	  System.out.println("OK");
    	  
      } else if (req.getParameter("keyname") != null && req.getParameter("value") != null){
    	  // put a new TaskData Entity using a key name and an value
    	  String reqStr = req.getParameter("keyname");
    	  String valueStr = req.getParameter("value");
    	  Entity reqEntity = new Entity("TaskData", reqStr);
    	  reqEntity.setProperty("value", valueStr);
    	  reqEntity.setProperty("date", new Date());
    	  datastore.put(reqEntity);
    	  // put the Entity to memCache also
    	  syncCache.put(reqStr,reqEntity);
    	  // display the message on html page
    	  resp.getWriter().println("<p>Stored key: " + reqStr + " Entity date: "+ reqEntity.getProperty("date") + " value: " + valueStr + "</p>");
      } else {
    	  resp.getWriter().println("<h2>Error: please pass in the right parameters!</h2>");
      }
      
      //Add your code here

      resp.getWriter().println("</body></html>");
  }
}