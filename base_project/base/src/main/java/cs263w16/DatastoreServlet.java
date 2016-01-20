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
	  
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
      
      //resp.getWriter().println("<h2>Hello World?</h2>"); //remove this line
      if (req.getParameterNames().hasMoreElements() == false){
    	  Query q = new Query("TaskData");
    	  PreparedQuery pq = datastore.prepare(q);
    	  for (Entity result:pq.asIterable()){
    		  String value = (String)result.getProperty("value");
    		  Date date = (Date)result.getProperty("date");
    		  resp.getWriter().println("<h2>" + value + "\t" + date + "</h2>");
    	  }
      } else if (req.getParameter("keyname") != null && req.getParameter("value") == null){
    	  String reqStr = req.getParameter("keyname");
    	  Key reqKey = KeyFactory.createKey("TaskData", reqStr);
    	  Entity reqEntity;
    	  try{
    		  reqEntity = datastore.get(reqKey);
    		  String value = (String)reqEntity.getProperty("value");
    		  Date date = (Date)reqEntity.getProperty("date");
    		  resp.getWriter().println("<h2>" + value + "\t" + date + "</h2>");
    	  } catch (Exception e){
    		  
    	  }
      } else if (req.getParameter("keyname") != null && req.getParameter("value") != null){
    	  String reqStr = req.getParameter("keyname");
    	  String valueStr = req.getParameter("value");
    	  Entity reqEntity = new Entity("TaskData", reqStr);
    	  reqEntity.setProperty("value", valueStr);
    	  reqEntity.setProperty("date", new Date());
    	  datastore.put(reqEntity);
    	  resp.getWriter().println("<h2>Stored " + reqStr + " and " + valueStr + " in Datastore</h2>");
      } else {
    	  resp.getWriter().println("<h2>Error: please pass in the right parameters!</h2>");
      }
      
      //Add your code here

      resp.getWriter().println("</body></html>");
  }
}