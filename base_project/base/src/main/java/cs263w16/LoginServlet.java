package cs263w16;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class LoginServlet extends HttpServlet{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		boolean invalidUserName = false;
		boolean existingUser = false;
		
		String username = request.getParameter("username").trim();
		
		// check username validity
		if (!Validator.isValidName(username)){
			request.setAttribute("invalid_username", Boolean.TRUE);
			invalidUserName = true;
		}
		if (invalidUserName){
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}
		
		// check if the username has already been used or not
		Query q = new Query("User").addSort("name",Query.SortDirection.ASCENDING);
		List<Entity> users = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(100));
		for (Entity entity:users){
			if (((String)entity.getProperty("name")).equals(username)){
				existingUser = true;
				break;
			}
		}
		
		if (existingUser){
			response.sendRedirect("/sharelocation.jsp?usr="+username+"&exist=true");
		} else{
			// Create user
			Entity user = new Entity("User",username);
			int count = 0;
			user.setProperty("name", username);
			user.setProperty("count", count);
			datastore.put(user);
			response.sendRedirect("/sharelocation.jsp?usr="+username);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
	}
}