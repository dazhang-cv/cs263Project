<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	String username = request.getParameter("usr");
%>



<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Crowdsourcing Online Inquiry</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<script src="http://use.edgefonts.net/source-sans-pro:n6:default.js"
	type="text/javascript"></script>
</head>


<body>

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
		<%
			if (request.getParameter("exist") != null){
		%>
		<h2>Welcome back <%= username %>! </h2>
		<% } else{%>
		<h2>Welcome <%= username %>! </h2>
		<% } %>	
		<h2>Click the button below to share your location and join our platform.</h2>
		
		<button class="joinButton" onClick="getLocation()">I agree to share my location</button>
	</div>
	
	
	<script>
	
	var userCords;
	var usr = "<%= username%>"
	
	function getLocation(){
		if (navigator.geolocation){
			navigator.geolocation.getCurrentPosition(success,error);
		} else{
			alert("Geolocation is not supported in your browser.");
		}
	}
	
	function success(pos){	
		userCords = pos.coords;
		console.log(userCords.latitude + " " + userCords.longitude);
		window.location = "/postquery.jsp?usr=" + usr + "&lat="+userCords.latitude+"&lon="+userCords.longitude;
	}
	
	function error(err){
		switch(err.code){
			case err.PERMISSION_DENIED:
				alert("User denied the request for Geolocation.");
				break;
			case err.POSITION_UNAVAILABLE:
				alert("Location information is unavailable.");
				break;
			case err.TIMEOUT:
				alert("The request to get user location timed out.")
				break;
			case error.UNKNOWN_ERROR:
				alert("An unknown error occurred.");
				break;
		}
	}
	
	</script>

</body>
</html>