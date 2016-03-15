<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="cs263w16.Validator"%>

<%
	String username = request.getParameter("usr");
	String lat_str = request.getParameter("lat");
	String lon_str = request.getParameter("lon");

	// check valid latitude and longitude
	if (lat_str == null
			|| lon_str == null
			|| !Validator.isNumeric(lat_str)
			|| !Validator.isNumeric(lon_str)){
		response.sendRedirect("/");
		return;
	}
	
	float lat = Float.valueOf(lat_str);
	float lon = Float.valueOf(lon_str);
	
	// check valid range of latitude and longitude
	if (!Validator.isValidLatitude(lat)
			|| !Validator.isValidLongitude(lon)){
		response.sendRedirect("/");
		return;
	}
	
	String hyper = "?usr=" + username + "&lat=" + lat_str + "&lon=" + lon_str;
%>

<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Crowdsourcing Online Inquiry-Main</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<link type="text/css" rel="stylesheet" href="/stylesheets/query.css" />
<script src="http://use.edgefonts.net/source-sans-pro:n6:default.js"
	type="text/javascript">
	
</script>

    <style type="text/css">
      html,body, #map-canvas {height: 100%; margin: 0; padding: 0;}
    </style>

<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDrtO84dZdFTPEcB4MxU6sabLpLs958w5Y">
	
</script>
</head>

<body>
	<div id="wrapper">
		<header id="top">
			<h1>Ask What you want</h1>
			<nav id="mainnav">
				<ul>
					<li><a href="postquery.jsp<%= hyper %>" class="thispage">Post a query</a></li>
					<li><a href="myquery.jsp<%= hyper %>">My Queries</a></li>
					<li><a href="receivequery.jsp<%= hyper %>">Received Queries</a></li>
				</ul>
			</nav>
		</header>
		
		<div class="well well-lg" style="overflow: auto; height:400px; width:99%;">
				<div id="map-canvas"></div>
		</div>
		
		<h2>Welcome <%= username %>! </h2>
		<h2>To post a query, please enter your query messages and pick a location on map.</h2>
		
		<form action="/query" method="post">
			<input type="text" name="question" class="titleName" placeholder="Briefly describe your query here">
			<input type="text" id="latitude" name="latitude" class="numberName" placeholder="Latitude will be shown once you click on map">
			<input type="text" id="longitude" name="longitude" class="numberName" placeholder="Longitude will be shown once you click on map">
			<%
			if (request.getAttribute("null_question") != null){
			%>
			<br>
			Your query cannot be empty, please describe your query.
			<% } %>	
			<%
			if (request.getAttribute("null_position") != null){
			%>
			<br>
			Please pick a location on map to post your query.
			<% } %>	
			<input type="hidden" name="username" value="<%= username %>">
			<input type="hidden" name="mylat" value="<%= lat_str %>">
			<input type="hidden" name="mylon" value="<%= lon_str %>">
			<button class="joinButton" type="submit">Post My Query</button> 
		</form>
	</div>
	
	
	
	
	<%-- Javascript for Google Maps --%>
	<script type="text/javascript">
		var queries;
		var markers;
		var infowindow;
		var map;

		function initialize() {
			var myPosition = new google.maps.LatLng(<%=lat%>,<%=lon%>);
			var mapOptions = {
				zoom : 13,
				center : myPosition,
				panControl : false,
				panControlOptions : {
					position : google.maps.ControlPosition.BOTTOM_LEFT
				},
				zoomControl : true,
				zoomControlOptions : {
					style : google.maps.ZoomControlStyle.LARGE,
					position : google.maps.ControlPosition.RIGHT_CENTER
				},
				scaleControl : false
			};
			map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions);
			
			<%-- An array of queries needed to generate markers on the map --%>
			var image = 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png';
			
			var myMarker = new google.maps.Marker({
				position: new google.maps.LatLng(<%= lat %>, <%= lon %>),
				map:map,
				title:"my location",
				icon:image
			});
			
			google.maps.event.addListener(map, 'click', function(event) {
				
				var latitude = event.latLng.lat();
			    var longitude = event.latLng.lng();

			    radius = new google.maps.Rectangle({map: map,
			        fillColor: '#0000FF',
			        fillOpacity: 0.1,
			        strokeColor: '#0000FF',
			        strokeOpacity: 0.8,
			        strokeWeight: 2,
			        bounds: new google.maps.LatLngBounds(
			                new google.maps.LatLng(latitude-0.01,longitude-0.01),
			                new google.maps.LatLng(latitude+0.01,longitude+0.01))
			    });

			    // Center of map
			    map.panTo(new google.maps.LatLng(latitude,longitude));
			    
			    document.getElementById("latitude").value = latitude;
			    document.getElementById("longitude").value = longitude;
			});
		}
		
		google.maps.event.addDomListener(window, 'load', initialize);
		

		
	</script>




</body>
</html>