/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function fillForm(long, lat, type) {
    
    var input_long = document.getElementById("editlocationsform:long");
    var input_lat = document.getElementById("editlocationsform:lat");
    var input_polen = document.getElementById("editlocationsform:polen");
    
    input_long.value = long;
    input_lat.value = lat;
    input_polen.value = type.toString();
}