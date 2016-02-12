<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Crowdsourcing Online Inquiry</title>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
    <script>var __adobewebfontsappname__="dreamweaver"</script><script src="http://use.edgefonts.net/source-sans-pro:n6:default.js" type="text/javascript"></script>
</head>

<body>

<div id="wrapper">
  <header id="top">
    <h1>Ask What you want</h1>
    <nav id="mainnav">
      <ul>
        <li><a href="index.html" class="thispage">Home</a></li>
        <li><a href="project.html">SelectA</a></li>
        <li><a href="publication.html">SelectB</a></li>
        <li><a href="talks.html">SelectC</a></li>
        <li><a href="PDF/DaZhang_CV.pdf">SelectD</a></li>
        <li><a href="contact.html">SelectE</a></li>
      </ul>
    </nav>
  </header>
  <div id="hero"><img src="/images/UCSB.jpg" alt=""/>  </div>

<%
    String guestbookName = request.getParameter("guestbookName");
    if (guestbookName == null) {
        guestbookName = "default";
    }
    pageContext.setAttribute("guestbookName", guestbookName);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);
%>

<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
} else {
%>
<p>Hello!
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
    to include your name with questions you post.</p>
<%
    }
%>


<form action="/enqueue" method="post">
	<p>
	<input type="text" name="usr">  User Name<br>
    <input type="text" name="content">	Message content<br>
    <input type="text" name="type">  Message type<br> 
    <input type="text" name="longitude">  Longitude<br>
    <input type="text" name="latitude">  Latitude<br>
    <input type="text" name="options">  Options<br>
    <input type="submit"></p>
</form>


<form action="/enqueue" method="post">
	<input type="text" name="usr">
	<input type="submit">
</form>

</body>
</html>