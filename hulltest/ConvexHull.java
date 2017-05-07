/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hulltest;

/**
 *
 * @author Drakan
 */
public class ConvexHull {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(" HELLO ");
        CreateHull createHull = new CreateHull();
        Points packets = createHull.createPackets();
        String sPoints = "";
        for (Point point1 : packets.points) {
            sPoints += "("+point1.x+","+point1.y+")";
        }
        System.out.println("Random Points generated: "+sPoints);
        String createhullfrompoints = createHull.createhullfrompoints(packets);
        System.out.println("result  = "+createhullfrompoints);
    }
}
