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
	// Get user's city.
	String city = request.getHeader("X-AppEngine-City");
	if (city == null){
		city = "others";
	}
	
	// Get all queries in user's city from the datastore
	Key cityKey = KeyFactory.createKey("City", city);
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query q = new Query("Query", cityKey);
	List<Entity> queries = datastore.prepare(q)
			.asList(FetchOptions.Builder.withLimit(500));
	
	// Get all queries the user created
	List<Entity> my_queries = new ArrayList<Entity>();
	String poster_name = "";
	for (Entity query:queries){
		poster_name = (String)query.getProperty("title");
		if (poster_name.startsWith(username)){
			my_queries.add(query);
		}
	}
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
					<li><a href="postquery.jsp<%= hyper %>" >Post a query</a></li>
					<li><a href="myquery.jsp<%= hyper %>" class="thispage">My Queries</a></li>
					<li><a href="receivequery.jsp<%= hyper %>" >Received Queries</a></li>
				</ul>
			</nav>
		</header>
		<div class="well well-lg" style="overflow: auto; height:400px; width:99%;">
				<div id="map-canvas"></div>
		</div>
		<h2>Your posted queries, click to review: </h2>
		<div class="well well-lg" style="height: 20vh; overflow: auto">
			<ul id="querylist">
				<%
				String nextquestion = "";
				String nextquery = "";
				String poster = "";
				for (Entity query:my_queries){
					nextquestion = (String)query.getProperty("question");
					nextquery = (String)query.getProperty("title");
				%>
				<li id = "<%= nextquery %>"><linkbutton onclick="submitForm('<%= nextquery%>','<%= nextquestion%>')"><%= nextquestion %> (stored as <%= nextquery %>)</linkbutton></li>
				<% } %>
			</ul>
		</div>
		
		<form action="/receive" method="post" id="ansform">
			<input type="hidden" name="city" id="city">
			<input type="hidden" name="question" id="question">
			<input type="hidden" name="username" id="username">
			<input type="hidden" name="title" id="title">
		</form>
		
	</div>
	
	<script type="text/javascript">
	function submitForm(title,question){
		document.getElementById("city").value = "<%= city%>";
		document.getElementById("username").value = "<%= username%>";
		document.getElementById("title").value = title;
		document.getElementById("question").value = question;
		document.getElementById("ansform").submit();
	}
	</script>
	
	
	
	<%-- Javascript for Google Maps --%>
	<script type="text/javascript">
		var queries;
		var markers;
		var map;

		function initialize() {
			var myPosition = new google.maps.LatLng(<%=lat%>,<%=lon%>);
			var mapOptions = {
				zoom : 14,
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
				title: "my location",
				icon:image
			});
			
			queries = [
			           <%
			           for (int i = 0; i < my_queries.size(); ++i){
			           %>
			           {lat: <%= ((GeoPt)my_queries.get(i).getProperty("location")).getLatitude() %>,
			        	   lon: <%= ((GeoPt)my_queries.get(i).getProperty("location")).getLongitude() %>,
			        	   question: "<%= my_queries.get(i).getProperty("question") %>" }
			           <%
			           if (i+1 < my_queries.size()) {
			           %>
			           ,
			           <%
			           }
			           }
			           %>
			           ];
			
			markers = [];
			for (i = 0; i < queries.length; i++){
				markers[markers.length] = new google.maps.Marker({
					position: new google.maps.LatLng(queries[i].lat,queries[i].lon),
					map:map,
					title:queries[i].question
				})
			}
		}
		
		google.maps.event.addDomListener(window, 'load', initialize);
		

		
	</script>




</body>
</html>