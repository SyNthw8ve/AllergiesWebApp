/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var mymap = L.map('mapid').setView([38.56667,  -7.9], 13);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1Ijoic3ludGg4dmUiLCJhIjoiY2p3ZG9jdzU1MDljZzQzbW9ycWF0cTRvMSJ9.tNErrHXjbYjmidsy8qa39w'
}).addTo(mymap);

var marker = L.marker([51.5, -0.09]).addTo(mymap);

function onMapClick(e) {
    console.log(e.latlng);
    
    //L.marker([e.latlng.lat, e.latlng.lng]).addTo(mymap);
}

mymap.on('click', onMapClick);

