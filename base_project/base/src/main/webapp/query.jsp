<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.channel.ChannelService"%>
<%@ page import="com.google.appengine.api.channel.ChannelServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page
	import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="com.google.appengine.api.memcache.MemcacheService"%>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory"%>


<%--
This jsp generates the query view.

This page consists:
- query poster, city and user name and the question and a "go back" button
- The list of users currently open this query
- The chat box containing all messages
- The input field for a new text message
--%>

<%
	// Getting the parameters
	String querytitle = request.getParameter("querytitle");
	String cityname = request.getParameter("cityname");
	String username = (String) (request.getSession()
			.getAttribute(cityname + ":" + querytitle));
	String question = (String) (request.getSession()
			.getAttribute(querytitle));
	
	boolean flag = false;
	if (querytitle.startsWith(username)){
		flag = true;
	}
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	Entity entity;
	String history = "";
	if (syncCache.get(cityname + ":" + querytitle) != null){
		entity = (Entity)syncCache.get(cityname + ":" + querytitle);
		history = (String)entity.getProperty("content");
	} 

	// Setting up the channel and datastoreservices
	ChannelService channelService = ChannelServiceFactory
			.getChannelService();
	DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	String token = channelService.createChannel(cityname + ":"
			+ querytitle);

	// Get a list of users in the room
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><%=querytitle%></title>
<script src='//code.jquery.com/jquery-1.11.1.min.js'></script>
<script type="text/javascript" src="/_ah/channel/jsapi"></script>
<link href="https://bootswatch.com/yeti/bootstrap.min.css"
	rel="stylesheet">
<link type="text/css" rel="stylesheet" href="/stylesheets/query.css" />
<script src="http://use.edgefonts.net/source-sans-pro:n6:default.js"
	type="text/javascript">
</script>
</head>

<body>
	<%-- query header --%>
	<div class="col-lg-6 col-lg-offset-3">
		<b>Question:</b> <span id='question'><%=question%></span>
	</div>

	<div class="col-lg-6 col-lg-offset-3">
		<div class="col-lg-4">
			<b>Poster:</b> <span id='poster'><%=cityname + ":" + querytitle%></span>
		</div>
		<div class="col-lg-4">
			<b>User:</b> <span id='username'><%=username%></span>
		</div>
		<div class="col-lg-4">
			<button class="btn btn-default" onclick="leaveQuery()">Back to Login</button>
		</div>
	</div>

	<%-- List of current users --%>

	<%-- Chat box containing all messages --%>
	<div class="col-lg-6 col-lg-offset-3">
		<div class="bs-component">
			<div class="well well-lg" style="height: 60vh; overflow: auto"
				id="chatbox">
				<b>Chat:</b><br>
			</div>
		</div>
	</div>

	<%-- Input field for text massages --%>
	<div class="col-lg-6 col-lg-offset-3">
		<input class='col-lg-11 col-md-11 col-sm-11' type='text'
			id='userInput' value=''
			onkeydown='if (event.keyCode == 13) send("message", function(){})' />
		<input class='col-lg-1 col-md-1 col-sm-1 btn btn-default'
			style='padding-top: 2px; padding-bottom: 2px;' type='button'
			onclick='send("message", function(){})' value='send' />
	</div>

	<script>
	
	function escapeHtml(str) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    };
// Setting up the client side channel
		var token = "<%=token%>";

		channel = new goog.appengine.Channel(token);
		socket = channel.open();
		var chatbox = document.getElementById('chatbox');
		// Callback function performed on opening of socket.
		// Sends a "join" message to the server
		socket.onopen = function() {
			send("join", function() {
			});
			<% 
			if (flag){ 
			%>
			chatbox.innerHTML += "<%= history %>";
			chatbox.scrollTop = chatbox.scrollHeight;
			<%
			}
			%>
		};

		// Callback function performed on receiving a message
		socket.onmessage = function(message) {
			var messageObject = JSON && JSON.parse(message.data)
					|| $.parseJSON(message.data);
			messageObject.username = messageObject.username.trim();

			if (messageObject.type != "image") {
				messageObject.content = escapeHtml(messageObject.content);
			}
			if (messageObject.type == "chat"){
				chatbox.innerHTML += "[" + messageObject.time + "] [" + messageObject.username + "] " + messageObject.content + "<br>";
				chatbox.scrollTop = chatbox.scrollHeight;
			}
			else if (messageObject.type == "leave"){
				chatbox.innerHTML += "[" + messageObject.time +"] " + messageObject.username + " has left the query.<br>";
				chatbox.scrollTop = chatbox.scrollHeight;
			}
			else if (messageObject.type == "join" && document.getElementById('username').innerHTML != messageObject.username){
				chatbox.innerHTML += "[" + messageObject.time + "] " + messageObject.username + " has entered the query.<br>";
				chatbox.scrollTop = chatbox.scrollHeight;
			}

		};
		
		socket.onerror = function(){
			chatbox.innerlHTML += "Channel error<br>";
		};
		
		socket.onclose = function(){
			chatbox.innerlHTML += "Channel closed<br>";
		}
		
		// function for sending messages to the server using ajax
		function send(type, onreadystate){
			var userInput = document.getElementById('userInput').value;
			var userName = document.getElementById('username').innerHTML;
			var queryTitle = document.getElementById('poster').innerHTML;
			
			var xhr = new XMLHttpRequest();
			xhr.onreadystatechange = onreadystate;
			xhr.open('POST','/channel/' + type + "?message="+userInput+"&usr=" + userName + "&querytitle=" + queryTitle,true);
			xhr.send();
			document.getElementById('userInput').value = '';
		}
		
		// Function called when this user leaves the query
		function leaveQuery(){
			send("leave", function() {window.location.replace('/')});
		}
		
	</script>

</body>

</html>








