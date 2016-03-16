package cs263w16;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;

public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        String enqueue = "enqueue";
        String querytitle = request.getParameter("querytitle");
        String city = "goleta";
        String longitude = "45.00";
        String latitude = "45.00";
        String question = "where is my vehicle?";

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/worker")
				.param("city", city)
				.param("querytitle", querytitle)
				.param("latitude", String.valueOf(latitude))
				.param("longitude", String.valueOf(longitude))
				.param("question", question));

        //response.sendRedirect("/index.jsp");
    }
}
