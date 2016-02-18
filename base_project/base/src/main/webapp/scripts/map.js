$(document).ready(function(){
	 
	// map options
	var mapOptions = {
			zoom: 5,
			center: new google.maps.LatLng(37.09024, -100.712891),
			panControl: false,
			panControlOptions: {
				position: google.maps.ControlPosition.BOTTOM_LEFT
			},
			zoomControl: true,
			zoomControlOptions: {
				style: google.maps.ZoomControlStyle.LARGE,
				position: google.maps.ControlPosition.RIGHT_CENTER
			},
			scaleControl: false
	};
	
	infowindow = new google.mapsInfoWindow({
		content: "holding..."
	});
	
	
})