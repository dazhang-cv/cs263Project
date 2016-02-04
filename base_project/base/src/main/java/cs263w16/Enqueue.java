package cs263w16;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usr = request.getParameter("usr");
        String content = request.getParameter("content");
        String type = request.getParameter("type");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String options = request.getParameter("options");

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/worker").param("usr", usr).param("content", content).
        		param("type",type).param("longitude",longitude).param("latitude",latitude).param("options",options));

        response.sendRedirect("/done.html");
    }
}