package cs263w16;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReceiveServlet extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		String city = request.getParameter("city");
		String titlename = request.getParameter("title");
		String username = request.getParameter("username");
		String question = request.getParameter("question");
		
		request.getSession().setAttribute(city+":"+titlename, username);
		request.getSession().setAttribute(titlename, question);
		response.sendRedirect("/query/"+city+"/"+titlename);
	}
}
