package hulltest;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author Drakan
 */
public class CreateHull {

    public String createhullfrompoints(Points packets) {
        try {
            HashMap<String, float[]> polygon = new HashMap<String, float[]>();
            HashMap<String, float[]> packetsMap = new HashMap<String, float[]>();
            String result = "";
            HashMap<Long, Point> hPackets = new HashMap<Long, Point>();
            if (packets != null) {

                for (Point packet : packets.points) {
                    hPackets.put(packet.id, packet);
                }

                TreeMap<Double, Point> first = this.first(hPackets);
                
                for( Double p1: first.keySet()) {
                    float x = (float) (first.get(p1).x );
                    float y = (float) (first.get(p1).y);
                    result += "("+x+","+y+")";
                    polygon.put(p1 + "", new float[]{x, y});
                }

                for (Point packet : packets.points) {
                    float x = (float) (packet.x);
                    float y = (float) (packet.y);
                    packetsMap.put(packet.id + "", new float[]{x, y});
                }
            }

            

//            for (String key : polygon.keySet()) {
//                System.out.println("key:"+key);
//                String Spoint = "";
//                for (float f : polygon.get(key)) {
//                    Spoint+=f+"";
//                }
//                System.out.println(Spoint);
//                
//                
//            }
            return result;
        } catch (Exception e) {
            System.err.println("ERRRRRROOOOORRRR");
        }
        return null;
    }

    public TreeMap<Double, Point> first(HashMap<Long, Point> packets) throws Exception {
        TreeMap<Double, Point> result = new TreeMap<Double, Point>();
        //get the line 
        double leftx = 100000000;
        double lefty = 100000000;
        double rightx = -100000000;
        double righty = -100000000;

        double topx = -100000000;
        double topy = -100000000;
        double bottomx = 100000000;
        double bottomy = 100000000;

        TreeMap<Double, Point> polypoints = new TreeMap<Double, Point>();

        for (Long key : packets.keySet()) {
            Point packet = packets.get(key);
            double y = ((double) packet.y );
            double x = ((double) packet.x );

            if (x <= leftx) {
                lefty = y;
                leftx = x;
                packet.dir = 0; //0 = neutral
                polypoints.put(0.0, packet);
                polypoints.put(400.0, packet);
            }
            if (x >= rightx) {
                righty = y;
                rightx = x;
                packet.dir = 0; //0 = neutral
                polypoints.put(200.0, packet);
            }
            if (y >= topy) {
                topy = y;
                topx = x;
                packet.dir = 1;//1 = top 
                polypoints.put(300.0, packet);
            }
            if (y <= bottomy) {
                bottomy = y;
                bottomx = x;
                packet.dir = 2;//2 = bottom
                polypoints.put(100.0, packet);
            }
        }
//		System.out.println("left : " + leftx + "," + lefty);
//		System.out.println("right : " + rightx + "," + righty);
//		System.out.println("bottom : " + " : " + bottomx + " : " + bottomy);
//		System.out.println("top : " + " : " + topx + " : " + topy);
        for (Double col : polypoints.keySet()) {
            packets.remove(polypoints.get(col).id);
        }

        TreeMap<Double, Point> lowerHull = this.accb(polypoints, packets, -1, 0.0, 200.0);
        TreeMap<Double, Point> upperHull = this.accb(polypoints, packets, 1, 200.0, 400.0);
        result.putAll(lowerHull);
        result.remove(400.0);
        return result;

    }

    public void setpoint(TreeMap<Double, Point> polypoints, HashMap<Long, Point> packets, double id, Point newPoint) throws Exception {
        polypoints.put(id, newPoint);
        packets.remove(newPoint.id);
    }

    public TreeMap<Double, Point> accb(TreeMap<Double, Point> polypoints, HashMap<Long, Point> packets, double top, double start, double end) throws Exception {
        boolean cont = true;
        int limit = 10000;
        while (cont) // Repeats until no changes are needed
        {
            double Line = 0.0;
            double lastX = -1000000000.0;
            double lastY = -1000000000.0;
            double lastID = -1000000000.0;
            Point pointPacket = null;

            for (Double col : polypoints.keySet()) /// Segment Loop of new polygon
            {
                if (col <= end && col >= start) {
                    Point get = polypoints.get(col);

                    if (lastX != -1000000000.0 && lastY != -1000000000.0 && lastID != -1000000000.0) {
                        double low = 100000000.0;
                        double high = -1000000000.0;
                        double topx = 0.0;
                        double topy = 0.0;
                        double bottomx = 0.0;
                        double bottomy = 0.0;

                        for (Long key : packets.keySet()) // Loops through remaining points in original data
                        {
                            Point packet = packets.get(key);
                            double pointX = packet.x / 10000000.0;
                            double pointY = packet.y / 10000000.0;

                            double distToLine = this.getDistToLine(lastX, lastY, (get.x / 10000000.0), (get.y / 10000000.0), pointX, pointY);
                            //if (distToLine  > low && distToLine > 0)

                            distToLine *= top;
                            if (distToLine > high && distToLine > 0) {

                                high = distToLine;

                                Line = distToLine;
                                bottomx = pointX;
                                bottomy = pointY;
                                pointPacket = packet;
                            }
                        }
                        if (pointPacket != null) {
                            this.setpoint(polypoints, packets, (lastID + col) / 2.0, pointPacket);
                            break;
                        }
                    }

                    lastX = get.x ;
                    lastY = get.y ;
                    lastID = col;
                }
            }
            if (pointPacket == null) {
                cont = false;
            }

            limit--;
            if (limit <= 0) {
                cont = false;
                polypoints = null;
            }

        }

        return polypoints;
    }

    public double getDistToLine(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        double dist = 0;
        double above = 1;
        if (x1 == x2) // Vertical Line. Skip all formulas
        {
            dist = Math.abs(y2 - y1);
            dist = 0; // A vertical line is an exception. If you get a vertical line then it means that you will not find any other points that are outside the polygon
        } else if (y1 == y2) // Horizontal Line. Skip all formulas
        {
            dist = Math.abs(x2 - x1);
            if (pointY < y1) {
                above = -1;
            }
        } else // This is where it get's interisting
        {
            // o donates that it's the original line mentioned in the parameters
            // n is a new line that will be formed from the point disecting the original line at 90 degrees
            double om = (y1 - y2) / (x1 - x2);
            double oc = y1 - (om * x1);

            double nm = -1 / om;
            double nc = pointY - (nm * pointX);

            double ix = (oc - nc) / (nm - om);
            double iy = (om * ix) + oc;

            dist = Math.sqrt(Math.pow(pointX - ix, 2) + Math.pow(pointY - iy, 2));

            double ac = pointY - (om * pointX);
            if (ac < oc) {
                above = -1;
            }
        }
        dist *= above;
        return dist;
    }

    public Points createPackets() {

         Point[] colection = new Point[50];
        for (int i = 0; i < 50; i++) {
            int randox = 0;
            int randoy = 0;
            Random ran = new Random();
            if(i<=10){
                randox = ran.nextInt(10 - 3 + 1) + 0;
                randoy = ran.nextInt(10 - 2 + 1) + 0;
            }
            if (i>=10) {
                randox = ran.nextInt(10 - 0 + 1) + 0;
                randoy = ran.nextInt(10 - 0 + 1) + 0;
            }
            if (i>=20&&i<=30) {
                randox = 0-(randox+i);
                randoy = 0-(randoy+i);
            }
            if (i>=30 &&i<=40 ) {
                randox = 0-(randox+i);
            }
            if (i>=40) {
                randoy = 0-(randoy+i);
            }
            Point point = new Point();
            point.x = i+randox;
            point.y = i+randoy;
            point.id = i;
            colection[i] = point;
        }

        Points points = new Points();
        points.points = colection;
        return points;
    }

}
