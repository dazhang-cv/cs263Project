package cs263w16;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

public class MessageServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// get the default datastore and memcache
		DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		List<String> paraList = Collections.list(req.getParameterNames());
		
		resp.setContentType("text/html");
		resp.getWriter().println("<html><body>");
		
		// add a message to datastore
		if (paraList.size() == 6){
			String usr = req.getParameter("usr");
			String content = req.getParameter("content");
			int type = Integer.parseInt(req.getParameter("type"));
			double longitude = Double.parseDouble(req.getParameter("longitude"));
			double latitude = Double.parseDouble(req.getParameter("latitude"));
			String options = req.getParameter("options");
			Date date = new Date();
			
			String keyname = usr + date;
			
			Entity newEntity = new Entity("Message",keyname);
			newEntity.setProperty("usr", usr);
			newEntity.setProperty("content", content);
			newEntity.setProperty("type", type);
			newEntity.setProperty("date", date);
			newEntity.setProperty("longitude", longitude);
			newEntity.setProperty("latitude", latitude);
			newEntity.setProperty("options", options);
			
			dataStore.put(newEntity);
			syncCache.put(keyname, newEntity);
		}
		
		// search for messages with same sender
		if (paraList.size() == 1 && paraList.get(0).equals("usr")){
			String q_usr = req.getParameter("usr");
			resp.getWriter().println("<h2>User: " + q_usr + "</h2>");
			Query q = new Query("Message");
			PreparedQuery pq = dataStore.prepare(q);
			for (Entity result:pq.asIterable()){
				String usr = (String)result.getProperty("usr");
				if (usr.equals(q_usr)){
					String content = (String)result.getProperty("content");
					long type = (long)result.getProperty("type");
					Date date = (Date)result.getProperty("date");
					double longitude = (double)result.getProperty("longitude");
					double latitude = (double)result.getProperty("latitude");
					String options = (String)result.getProperty("options");
					resp.getWriter().println("<p>Content: " + content + "</p>");
				}
			}
		}
			
		resp.getWriter().println("</body></html>");
	}
	
	
}