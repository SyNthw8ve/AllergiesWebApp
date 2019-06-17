/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var locations = JSON.parse(document.getElementById('mapid').getAttribute('data'));

var markers = [];

var mymap = L.map('mapid').setView([38.56667, -7.9], 13);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1Ijoic3ludGg4dmUiLCJhIjoiY2p3ZG9jdzU1MDljZzQzbW9ycWF0cTRvMSJ9.tNErrHXjbYjmidsy8qa39w'
}).addTo(mymap);

add_locations();

function onMapClick(e) {

    
    var input_long = document.getElementById("risklocationsform:long");
    var input_lat = document.getElementById("risklocationsform:lat");
    
    input_long.value = e.latlng.lat;
    input_lat.value = e.latlng.lng;
}

function add_locations() {

    for (i = 0; i < locations.length; i++) {

        var new_marker = L.marker([locations[i].long, locations[i].lat]).addTo(mymap);
        markers.push(new_marker);
    }
}

mymap.on('click', onMapClick);

