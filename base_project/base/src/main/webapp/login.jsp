<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Crowdsourcing Online Inquiry</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<script src="http://use.edgefonts.net/source-sans-pro:n6:default.js"
	type="text/javascript"></script>
</head>


<body>
	<%
	String username = request.getParameter("usr");
	%>

	<div id="wrapper">
		<header id="top">
			<h1>Ask What you want</h1>
			<nav id="mainnav">
				<ul>
					<li><b>Welcome to our Geo-location based online querying platform</b></li>
				</ul>
			</nav>
		</header>
		<div id="hero">
			<img src="/images/UCSB.jpg" alt="" />
		</div>
		
		<h2>Choose your unique username and enjoy the amazing crowdsourcing platform.</h2>

		<form action="/login" method="post">
			<input type="text" name="username" class="joinName" placeholder="Username">
			<button class="joinButton" type="submit">Confirm and use this name</button> 
			<%
			if (request.getAttribute("invalid_username") != null){
			%>
			<br>
			Only letters a-z(A-Z) and numbers 0-9 are allowed in user names.
			<% } %>	
		</form>
	</div>
	
	
</body>
</html>