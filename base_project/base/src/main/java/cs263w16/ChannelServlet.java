package cs263w16;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;

public class ChannelServlet extends HttpServlet{
	
	private final String CHAT_MESSAGE = "chat";
	private final String LEAVE_MESSAGE = "leave";
	private final String JOIN_MESSAGE = "join";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		messageReceived(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		messageReceived(req, resp);
	}
	
	/**
	 * Handles message requests and determines the appropriate response.
	 * @param req The HttpServletRequest object from doGet/doPost
	 * @param resp The HttpServletResponse object from doGet/doPost
	 * @throws IOException
	 */
	private void messageReceived(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = req.getParameter("usr");
		String querytitle = req.getParameter("querytitle");
		
		// add the chat information to memcache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Entity entity;
		String cnt = "";
		
		//Check if the user is in room
		if (username == null){
			resp.sendRedirect("/");
			return;
		}
		
		Message message;
		// Get the message type from the last part of the requested url
		String type = req.getPathInfo();
		Calendar now = Calendar.getInstance();
		int hours = now.get(Calendar.HOUR_OF_DAY);
		int minutes = now.get(Calendar.MINUTE);
		String time = hours+":"+minutes;
		
		if (isMessage(type)){
			message = new Message(CHAT_MESSAGE, username, req.getParameter("message"), time);
			if (syncCache.get(querytitle) == null){
				entity = new Entity("Message", querytitle);
				entity.setProperty("content", "["+time+"] ["+username+"] " + req.getParameter("message") + "<br>");
				syncCache.put(querytitle, entity);
			} else{
				entity = (Entity)syncCache.get(querytitle);
				cnt = (String)entity.getProperty("content");
				cnt = cnt + "["+time+"] ["+username+"] " + req.getParameter("message") + "<br>";
				entity.setProperty("content", cnt);
				syncCache.put(querytitle, entity);
				//System.out.println(cnt);
			}
			sendMessage(querytitle,message);
		} else if (isJoin(type)){
			message = new Message(JOIN_MESSAGE, username,"",time);
			sendMessage(querytitle,message);
		} else if (isLeave(type)){
			message = new Message(LEAVE_MESSAGE, username,"",time);
			req.getSession().removeAttribute(querytitle);
			sendMessage(querytitle,message);
		}
	}
	
	private void sendMessage(String querytitle, Message message){
		
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		channelService.sendMessage(new ChannelMessage(querytitle,new Gson().toJson(message)));
	}
	
	private boolean isMessage(String type) {
		return type.equals("/message") || type.equals("/message/");
	}
	
	private boolean isLeave(String type) {
		return type.equals("/leave") || type.equals("/leave/");
	}
	
	private boolean isJoin(String type) {
		return type.equals("/join") || type.equals("/join/");
	}
}