var map;
var userCords;

if (navigator.geolocation) {
	function error(err) {
		console.warn('ERROR(' + err.code + '): ' + err.message);
	}

	function success(pos) {
		userCords = pos.coords;
		console.log(userCords.latitude + " " + userCords.longitude);
	}

	navigator.geolocation.getCurrentPosition(success, error);
} else {
	alert('Geolocation is not supported in your browser');
}

function initMap() {
	var mapOptions = {
		zoom : 5,
		center : new google.maps.LatLng(37.09024, -100.712891),
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
	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
}

$('#chooseZip').submit(function() {
	if (navigator.geolocation) {
		function error(err) {
			console.warn('ERROR(' + err.code + '): ' + err.message);
		}

		function success(pos) {
			userCords = pos.coords;
			console.log(userCords.latitude + " " + userCords.longitude);
		}

		navigator.geolocation.getCurrentPosition(success, error);
	} else {
		alert('Geolocation is not supported in your browser');
	}
	
	var userZip = $('textZip').val();
	var accessURL;

	return false;
});