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

public class QueryServlet extends HttpServlet{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		// get the user count and create the corresponding querytitle
		String username = request.getParameter("username").trim();
		String titlename = "";
		Key user_key = KeyFactory.createKey("User",username);
		Entity user_entity;
		try {
			user_entity = datastore.get(user_key);
			int count = Integer.parseInt(user_entity.getProperty("count").toString());
			titlename = username + "-" + count;
			count ++;
			user_entity.setProperty("count",count);
			datastore.put(user_entity);
		} catch(Exception e){}
		
		String mylat = request.getParameter("mylat").trim();
		String mylon = request.getParameter("mylon").trim();
		
		String question = request.getParameter("question").trim();
		boolean nullPosition = false;
		if (Validator.isNullString(request.getParameter("latitude")) || Validator.isNullString(request.getParameter("longitude"))){
			request.setAttribute("null_position", Boolean.TRUE);
			nullPosition = true;
		}
		if (nullPosition){
			request.getRequestDispatcher("/postquery.jsp?usr=" + username + "&lat="+mylat+"&lon="+mylon).forward(request, response);
			return;
		}
		float lat = Float.valueOf(request.getParameter("latitude"));
		float lon = Float.valueOf(request.getParameter("longitude"));
		String city = request.getHeader("X-AppEngine-City");
		if (city == null){
			city = request.getParameter("city");
		}
		if (city == null){
			city = "others";
		}
		
		boolean nullQuestion = false;
		
				
		// check if the user doesn't type anything as a question
		if (Validator.isNullString(question)){
			request.setAttribute("null_question", Boolean.TRUE);
			nullQuestion = true;
		}
		if (nullQuestion){
			request.getRequestDispatcher("/postquery.jsp?usr=" + username + "&lat="+mylat+"&lon="+mylon).forward(request, response);
			return;
		}
		
		// create a new query
		Key cityKey = KeyFactory.createKey("City", city);
		Entity queryEntity = new Entity("Query", titlename,cityKey);
		queryEntity.setProperty("title", titlename);
		queryEntity.setProperty("location", new GeoPt(lat,lon));
		queryEntity.setProperty("city", city);
		queryEntity.setProperty("question", question);
		// save to query datastore
		datastore.put(queryEntity);
		
		request.getSession().setAttribute(city+":"+titlename, username);
		request.getSession().setAttribute(titlename, question);
		response.sendRedirect("/query/"+city+"/"+titlename);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String[] cityAndQuery = request.getPathInfo().substring(1).split("/");
		String cityname = cityAndQuery[0];
		String querytitle = cityAndQuery[1];
		
		if (querytitle.endsWith("/")){
			querytitle = querytitle.substring(0, querytitle.length() - 1);
		}
		if (request.getSession().getAttribute(cityname + ":" + querytitle) != null){
			request.getRequestDispatcher("/query.jsp?cityname=" + URLEncoder.encode(cityname,"UTF-8") + "&querytitle=" + querytitle).forward(request,response);
		} else{
			response.sendRedirect("/");
		}
	}
}


