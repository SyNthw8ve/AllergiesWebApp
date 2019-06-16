/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

/**
 *
 * @author nuno1
 */
public class SMath {
    
    private final static float earth_radius = 6371000;
    
    public SMath() {
        
        
    }
    
    public static double haversine(double longA, double latA, double longB, double latB) {

        double longRadA = to_radian(longA);
        double longRadB = to_radian(longB);
        double latRadA = to_radian(latA);
        double latRadB = to_radian(latB);

        double havLat = Math.pow(Math.sin((latRadA - latRadB) / 2), 2);
        double havLong = Math.pow(Math.sin((longRadA - longRadB) / 2), 2);

        double square = Math.sqrt(havLat + Math.cos(latA) * Math.cos(latB) * havLong);

        return 2 * earth_radius * Math.asin(square);
    }
    
    public static double to_radian(double x) {

        return x * (Math.PI / 180);
    }
}
