package com.ebremer.halcyon.hilbert;

import com.ebremer.halcyon.lib.Node;
import com.ebremer.halcyon.geometry.Point;
import com.ebremer.halcyon.geometry.Box;
import com.ebremer.halcyon.geometry.Vector2D;
import com.ebremer.halcyon.lib.hsPolygon;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.davidmoten.hilbert.HilbertCurve;
import org.davidmoten.hilbert.Range;
import org.davidmoten.hilbert.Ranges;
import org.davidmoten.hilbert.SmallHilbertCurve;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;

/**
 *
 * @author erich
 */
public final class HilbertSpace {
    public SmallHilbertCurve hc;
    public static final byte N = 0;
    public static final byte NE = 1;
    public static final byte E = 2;
    public static final byte SE = 3;
    public static final byte S = 4;
    public static final byte SW = 5;
    public static final byte W = 6;
    public static final byte NW = 7;
    
    public HilbertSpace() {
        this.hc = HilbertCurve.small().bits(31).dimensions(2);
    }
    
    public boolean inRange(LinkedList<Range> rr, Point p, Byte neighbor) {
        switch (neighbor) {
            case N: return contains(rr,p.x,p.y-1);
            case NE: return contains(rr,p.x+1,p.y-1);
            case E: return contains(rr,p.x+1,p.y);
            case SE: return contains(rr,p.x+1,p.y+1);
            case S: return contains(rr,p.x,p.y+1);
            case SW: return contains(rr,p.x-1,p.y+1);
            case W: return contains(rr,p.x-1,p.y);
            case NW: return contains(rr,p.x-1,p.y-1);
        }
        return false;
    }
    
    public boolean inRange(Ranges rr, Point p, Byte neighbor) {
        switch (neighbor) {
            case N: return contains(rr,p.x,p.y-1);
            case NE: return contains(rr,p.x+1,p.y-1);
            case E: return contains(rr,p.x+1,p.y);
            case SE: return contains(rr,p.x+1,p.y+1);
            case S: return contains(rr,p.x,p.y+1);
            case SW: return contains(rr,p.x-1,p.y+1);
            case W: return contains(rr,p.x-1,p.y);
            case NW: return contains(rr,p.x-1,p.y-1);
        }
        return false;
    }
    
    public Point getPoint(long p) {
        long[] c = hc.point(p);
        return new Point((int) c[0], (int) c[1]);
    }

    public Polygon getFatPoint(long p) {
        long[] c = hc.point(p);
        int a = (int) c[0];
        int b = (int) c[1];
        int[] x = new int[4];
        int[] y = new int[4];
        x[0] = a;
        y[0] = b;
        x[1] = a+1;
        y[1] = b;
        x[2] = a+1;
        y[2] = b+1;
        x[3] = a;
        y[3] = b+1;        
        return new Polygon(x,y,4);
    }
    
    public Polygon getSkinnyPoint(int a, int b) {
        int[] x = new int[4];
        int[] y = new int[4];
        x[0] = a;
        y[0] = b;
        x[1] = a;
        y[1] = b;
        x[2] = a;
        y[2] = b;
        x[3] = a;
        y[3] = b;        
        return new Polygon(x,y,4);
    }
    
    public Polygon getSkinnyPoint(long p) {
        long[] c = hc.point(p);
        int a = (int) c[0];
        int b = (int) c[1];
        int[] x = new int[4];
        int[] y = new int[4];
        x[0] = a;
        y[0] = b;
        x[1] = a;
        y[1] = b;
        x[2] = a;
        y[2] = b;
        x[3] = a;
        y[3] = b;        
        return new Polygon(x,y,4);
    }
    
    public Ranges Fatten(Ranges rr) {
        Iterator<Range> i = rr.iterator();
        LinkedList<Long> nr = new LinkedList();
        while (i.hasNext()) {
            Range r = i.next();
            for (long c = r.low(); c<=r.high();c++) {
                long[] p = hc.point(c);
                if (!nr.contains(c)) nr.add(c);
                p[0] = p[0]+1;
                long nn = hc.index(p);
                if (!nr.contains(nn)) nr.add(nn);
                p[0] = p[0]-1;
                p[1] = p[1]+1;
                nn = hc.index(p);
                if (!nr.contains(nn)) nr.add(nn);
            }
        }
        Collections.sort(nr);
        Ranges neo = new Ranges(nr.size());
        Iterator<Long> ii = nr.iterator();
        long first;
        long last;
        while (ii.hasNext()) {
            first = ii.next();
            last = first;
            while (ii.hasNext()) {
                long next = ii.next();
                if ((next-last)==1) {
                    last = next;
                }
            }
            neo.add(new Range(first,last));
        }
        return neo;
    }
    
    public Point NextPoint(Point p, Byte neighbor) {
        switch (neighbor) {
            case N: return new Point(p.x,p.y-1);
            case NE: return new Point(p.x+1,p.y-1);
            case E: return new Point(p.x+1,p.y);
            case SE: return new Point(p.x+1,p.y+1);
            case S: return new Point(p.x,p.y+1);
            case SW: return new Point(p.x-1,p.y+1);
            case W: return new Point(p.x-1,p.y);
            case NW: return new Point(p.x-1,p.y-1);
        }
        return null;
    }
    
    public Point GetXY(long p) {
        long[] c = hc.point(p);
        return new Point((int) c[0],(int) c[1]);
    }

    public boolean contains(LinkedList<Range> rr, int x, int y) {
        long target = hc.index(new long[] {x,y});
        for (Range r : rr) {
            if ((target>=r.low())&&(target<=r.high())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(Ranges rr, int x, int y) {
        long target = hc.index(new long[] {x,y});
        for (Range r : rr) {
            if ((target>=r.low())&&(target<=r.high())) {
                return true;
            }
        }
        return false;
    }
    
    public BufferedImage GetBI(int px, int py, int width, int height, Ranges ranges) {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(Color.RED);
        g2.fillRect(0, 0, width, height);
        for (Range pp : ranges) {
            for (long k=pp.low(); k<(pp.high()+1); k++) {
                long[] point = hc.point(k);
                int x = (int)(point[0]- px);
                int y = (int)(point[1]- py);
                if ((x<width)&&(x>0)&&(y<height)&&(y>0)) {
                    bi.setRGB(x, y, 0xFF0000FF);
                }
            }
        }
        return bi;
    }
    
    public hsPolygon Box(int x, int y, int width, int height) {
        long[] a = new long[] {x,y};
        long[] b = new long[] {x+width-1,y+height-1};
        Ranges rr = hc.query(a, b);
        return new hsPolygon(rr);
    }

    public BufferedImage GetBI(int px, int py, int width, int height, HashMap<Integer,LinkedList<Range>> ranges, HashMap<Integer,Integer> cvalues, HashMap<Integer,Float> pvalues) {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(new Color(128,0,128,128));
        g2.fillRect(0, 0, width, height);
        for (Integer id : ranges.keySet()) {
            float pc = pvalues.get(id);
            int classid = cvalues.get(id);
            int prob = (int) (pc*255f+0.5);
            int color = (0xFF000000)+(classid<<16)+(prob<<8);
            LinkedList<Range> rs = ranges.get(id);
            for (Range pp : rs) {
                long len = pp.high()-pp.low()+1;
                if ((pp.low()>0)&&(len<((width*height)+1))) {
                    for (long k=pp.low(); k<(pp.high()+1); k++) {
                        long[] point = hc.point(k);
                        int x = (int)(point[0]- px);
                        int y = (int)(point[1]- py);
                        if ((x<width)&&(x>=0)&&(y<height)&&(y>=0)) {
                            bi.setRGB(x, y, color);
                        }
                    }
                }
            }
        }
        return bi;
    }
    
    public BufferedImage GetBIbyClass(int px, int py, int width, int height, HashMap<Integer,LinkedList<Range>> ranges, HashMap<Integer,Integer> values) {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(new Color(128,0,128,128));
        g2.fillRect(0, 0, width, height);
        for (Integer id : ranges.keySet()) {
            int color = values.get(id);
            color = (0xFF000000)+(color<<16)+(color<<8)+(color);
            LinkedList<Range> rs = ranges.get(id);
            for (Range pp : rs) {
                long len = pp.high()-pp.low()+1;
                if ((pp.low()>0)&&(len<((width*height)+1))) {
                    for (long k=pp.low(); k<(pp.high()+1); k++) {
                        long[] point = hc.point(k);
                        int x = (int)(point[0]- px);
                        int y = (int)(point[1]- py);
                        if ((x<width)&&(x>=0)&&(y<height)&&(y>=0)) {
                            bi.setRGB(x, y, color);
                        }
                    }
                }
            }
        }
        return bi;
    }
    
    public BufferedImage GetBIbyProbility(int px, int py, int width, int height, HashMap<Integer,LinkedList<Range>> ranges, HashMap<Integer,Float> values) {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(new Color(128,0,128,128));
        g2.fillRect(0, 0, width, height);
        for (Integer id : ranges.keySet()) {
            float pc = values.get(id);
            int color = (int) (pc*255f+0.5);
            color = (0xFF000000)+(color<<16)+(color<<8)+(color);
            LinkedList<Range> rs = ranges.get(id);
            for (Range pp : rs) {
                long len = pp.high()-pp.low()+1;
                if ((pp.low()>0)&&(len<((width*height)+1))) {
                    for (long k=pp.low(); k<(pp.high()+1); k++) {
                        long[] point = hc.point(k);
                        int x = (int)(point[0]- px);
                        int y = (int)(point[1]- py);
                        if ((x<width)&&(x>=0)&&(y<height)&&(y>=0)) {
                            bi.setRGB(x, y, color);
                        }
                    }
                }
            }
        }
        return bi;
    }
    
    public Box BoundingBox(Ranges rr) {
        long start = System.nanoTime();
        Iterator<Range> i = rr.iterator();
        long c = 0;
        Box bb = new Box(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        while (i.hasNext()) {
            Range r = i.next();
            c = c + (r.high()-r.low()+1);
        }
        int cc = (int) c;
        int[] px = new int[cc];
        int[] py = new int[cc];
        int k = -1;
        i = rr.iterator();
        while (i.hasNext()) {
            k++;
            Range r = i.next();
            for (long a=r.low();a<r.high()+1;a++) {
                long[] wow = hc.point(a);
                px[k] = (int) wow[0];
                py[k] = (int) wow[1];
                bb.MinX = (int) Math.min(bb.MinX, px[k]);
                bb.MaxX = (int) Math.max(bb.MaxX, px[k]);
                bb.MinY = (int) Math.min(bb.MinY, py[k]);
                bb.MaxY = (int) Math.max(bb.MaxY, py[k]);
                bb.TopLeft = Vector2D.SmallestMag(bb.TopLeft, new Point(px[k],py[k]));
            }
        }
        System.out.println((System.nanoTime()-start)/1000000d);
        System.out.println(bb.toString());
        return bb;
    }
    
    public String JsonPolygons(int px, int py, int width, int height, HashMap<Integer,LinkedList<Range>> ranges, HashMap<Integer,Float> values, HashMap<Integer,Integer> classids) {      
        System.out.println("Convert to JSON String...");
        long start = System.nanoTime();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Integer id : ranges.keySet()) {
            LinkedList<Range> rs = ranges.get(id);
            Ranges rr = hTools.Tran(rs);
            /*
            System.out.println("PROCESSING POLYGON ID#"+id);
            if (id==301470) {
                rs.forEach(p->{
                    System.out.println("DELTA : "+p.low()+" "+p.high());
                });
                rr.forEach(p->{
                    System.out.println("EPSILON : "+p.low()+" "+p.high());
                });
            }*/
            Polygon p = getPolygon(rr);
            JsonArrayBuilder cab = Json.createArrayBuilder();
            if (p!=null) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                for (int k=0;k<p.npoints; k++) {
                    cab.add(Json.createArrayBuilder().add(p.xpoints[k]).add(p.ypoints[k]));
                }
                job.add("id",id);
                job.add("coordinates",cab);
                if (values.containsKey(id)) {
                    job.add("hasValue", values.get(id));
                } else {
                    System.out.println("something seriously wrong here....");
                }
                if (classids.containsKey(id)) {
                    job.add("hasClass", classids.get(id));
                } else {
                    System.out.println("something seriously wrong here with ClassIDS....");
                }
                jab.add(job);
            }
        }
        long delta = System.nanoTime() - start;
        delta = delta / 1000000000;
        System.out.println("DONE!!! "+delta);
        return jab.build().toString();
    }
        
    public void Print(Ranges rr) {
        for (Range r : rr) {
            System.out.println(r.low()+" "+r.high()+" "+(r.high()-r.low()));
            System.out.println("X");
            for (long wow = r.low(); wow<=r.high(); wow++) {
                long[] p = hc.point(wow);
                System.out.println(p[0]);
            }
            System.out.println("Y");
            for (long wow = r.low(); wow<=r.high(); wow++) {
                long[] p = hc.point(wow);
                System.out.println(p[1]);
            }
        }
    }
      
    public Point GetUpperLeft(Ranges rr) {
        Iterator<Range> i = rr.iterator();
        Point top = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
        while (i.hasNext()) {
            Range r = i.next();
            for (long c = r.low(); c<=r.high();c++) {
                long[] p = hc.point(c);
                if (p[1]<top.y) {
                    top.x = (int) p[0];
                    top.y = (int) p[1];
                } else if (p[1]==top.y) {
                    if (p[0]<top.x) {
                        top.x = (int) p[0];
                        top.y = (int) p[1];
                    }
                }
            }
        }
        return top;
    }
    
    public Node DetermineNeighbors(Long p) {
        Node node = new Node();
        
        return node;
    }
        
    public Polygon getPolygon(Ranges rr) {
      //  System.out.println("Processing................XXXX");
        if (rr.size()==1) {
            Iterator<Range> ri = rr.iterator();
            Range r = ri.next();
            if ((r.high()-r.low())==0) {
                return getSkinnyPoint(r.low());
            }
        }
        //Ranges big = Fatten(rr);
        Ranges big = rr;
        Point sp = GetUpperLeft(big);
        //System.out.println("STARTING POINT : "+(sp.x-65536)+", "+(sp.y-28672));
        Polygon p = new Polygon();
        Point cp = sp.clone();
        Visits v = new Visits();
        p.addPoint(sp.x, sp.y);
        v.visited(cp);
        Point lp = sp.clone();
        byte cd = -1;
        byte ld;
        do {
            boolean jumped;
            do {
                jumped = true;
            //    System.out.println((cp.x-65536)+", "+(cp.y-28672)+" ==> "+v.getNumVisits(cp));
              /*  
                System.out.println("CP : "+v.getNumVisits(cp));
                System.out.println("N  : "+v.getNumVisits(NextPoint(cp, N)));
                System.out.println("NE : "+v.getNumVisits(NextPoint(cp, NE)));
                System.out.println("E  : "+v.getNumVisits(NextPoint(cp, E)));
                System.out.println("SE : "+v.getNumVisits(NextPoint(cp, SE)));
                System.out.println("S  : "+v.getNumVisits(NextPoint(cp, S)));
                System.out.println("SW : "+v.getNumVisits(NextPoint(cp, SW)));
                System.out.println("W  : "+v.getNumVisits(NextPoint(cp, W)));
                System.out.println("NW : "+v.getNumVisits(NextPoint(cp, NW)));
                */
                
            if        (inRange(big, cp, N) &&!inRange(big,cp, NW) && (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, N)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.y--; cd = N;
            } else if (inRange(big, cp, NE)&&!inRange(big, cp, N) && (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, NE)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x++; cp.y--; cd = NE;
            } else if (inRange(big, cp, E) &&!inRange(big, cp, NE)&& (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, E)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x++; cd = E;
            } else if (inRange(big, cp, SE)&&!inRange(big, cp, E) && (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, SE)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x++; cp.y++; cd = SE;
            } else if (inRange(big, cp, S) &&!inRange(big, cp, SE)&& (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, S)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                        cp.y++; cd = S;
            } else if (inRange(big, cp, SW)&&!inRange(big, cp, S) && (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, SW)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x--; cp.y++; cd = SW;
            } else if (inRange(big, cp, W) &&!inRange(big, cp, SW)&& (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, W)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x--; cd = W;
            } else if (inRange(big, cp, NW)&&!inRange(big, cp, W) && (v.getNumVisits(cp)>v.getNumVisits(NextPoint(cp, NW)))) {
                ld = cd; lp.x = cp.x; lp.y = cp.y;
                cp.x--; cp.y--; cd = NW;
            } else {
             //   System.out.println("ERROR in polygon traversal....");
                if (v.getNumVisits(cp)>10) {
                    System.out.println("OBSURD ISSUE FAILOUT "+cp);
                    return getSkinnyPoint(cp.x,cp.y);
                }
                /*
                big.stream().forEach(r->{
                    System.out.println("Range : "+r.low()+" ---> "+r.high());
                    for (long f=r.low(); f<=r.high(); f++) {
                        Point g = getPoint(f);
                       System.out.println("POINT : "+(g.x-65536)+", "+(g.y-28672));
                    }
                });*/
                jumped = false;
                ld = -1;
            }
            v.visited(cp);
            } while (!jumped);
           // System.out.println("JUMPED TO "+(cp.x-65536)+", "+(cp.y-28672)+" ==> "+v.getNumVisits(cp));
            if ((cd>=0)&&(cd!=ld)) {
                p.addPoint(cp.x, cp.y);
            }
          //  System.out.println("ARE WE DONE? "+(cp.x-65536)+", "+(cp.y-28672)+"  <<<<>>>>> "+(sp.x-65536)+", "+(sp.y-28672));
        } while (!Point.isSamePoint(sp, cp));
        p.addPoint(cp.x, cp.y);
        return p;
    }
    
    public static String SpaceIT(String c) {
        String e = "";
        int units = c.length()/2;
        int s;
        if ((units*2)<c.length()) {
            e = "0"+c.substring(0,1);
            s=1;
        } else {
            s=0;
        }
        while(s<c.length()) {
            e = e+" "+c.substring(s,s+2);
            s=s+2;
        }
        return e.trim();
    }
    
    public final LinkedList<Range> Polygon2Hilbert(Polygon p, int scale) {
        if (scale>0) {
            for (int i=0; i<p.npoints; i++) {
                p.xpoints[i]=p.xpoints[i]>>scale;
                p.ypoints[i]=p.ypoints[i]>>scale;
            }
            return Polygon2Hilbert(p);
        } else {
            return Polygon2Hilbert(p);
        }
    }
    
    public static boolean mergable(Range a, Range b) {
        if (a.high()<b.low()) {
            return false;
        } else return b.high() >= a.low();
    }

    public static Range merge(Range a, Range b) {
        return new Range(Math.min(a.low(), b.low()),Math.max(a.high(), b.high()));
    }

    public static long Area(LinkedList<Range> list) {
        long area = 0;
        for (Range item : list) {
            area = area + item.high()-item.low()+1;
        }
        return area;
    }    
     
    public static LinkedList<Range> compact(LinkedList<Range> list) {
        if (list.size()==1) {
            return list;
        } else {
            LinkedList<Range> neo = new LinkedList<>();
            while (list.size()>1) {
                Range item = list.get(0);
                int i = 1;
                while ((list.size()>1)&&(i<list.size())) {
                    Range next = list.get(i);
                    if (mergable(item,next)) {
                        item = merge(item,next);
                        list.remove(i);
                        list.remove(0);
                        list.add(item);
                        i = 0;
                        item = list.get(0);
                    }
                    i++;
                }
                list.remove(0);
                neo.add(item);
            }
            if (list.size()==1) {
                neo.add(list.get(0));  // add last dangling item this created holes in images in early Halcyon builds
            }
            return neo;
        }
    }
    
    public static void print(LinkedList<Range> list) {
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        Iterator<Range> i = list.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
    
    public int len(int x1, int x2, int y1, int y2) {
        return (int) Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2)));
    }
    
    public boolean isSquare(Polygon p) {
        if (p.npoints!=4) return false;
        int s1 = len(p.xpoints[0],p.xpoints[1],p.ypoints[0],p.ypoints[1]);
        int s2 = len(p.xpoints[1],p.xpoints[2],p.ypoints[1],p.ypoints[2]);
        int s3 = len(p.xpoints[2],p.xpoints[3],p.ypoints[2],p.ypoints[3]);
        int s4 = len(p.xpoints[3],p.xpoints[0],p.ypoints[3],p.ypoints[0]);
        return (s1==s2)&&(s2==s3)&&(s3==s4);
    }
    
    public final LinkedList<Range> Polygon2Hilbert(Polygon p) {
        LinkedList<Range> rah = new LinkedList();
        Area a = new Area(p);
        Rectangle2D r = a.getBounds2D();
        int minx = (int) r.getMinX();
        int maxx = (int) r.getMaxX();
        int miny = (int) r.getMinY();
        int maxy = (int) r.getMaxY();
        LinkedList pp = new LinkedList();       
        for (int x=minx;x<maxx; x++) {
            for (int y=miny;y<maxy;y++) {
               if (a.contains(new java.awt.Point(x,y))) {
                   pp.add(hc.index(x,y));
               }
            }   
        }
        pp.sort(null);
        Collections.sort(pp);
        int i = 1;
        long sv = 0;
        long ev = 0;
        if (!pp.isEmpty()) {
            sv = (long) pp.get(0);
            ev = (long) pp.get(0);
        }
        while (i<pp.size()) {
            long nv = (long) pp.get(i);
            if ((nv-ev)==1) {
                ev = nv;
            } else {
                rah.add(new Range(sv,ev));
                sv = nv;
                ev = nv;
            }
            i++;
        }
        rah.add(new Range(sv,ev));
        return rah;
    }

    public final LinkedList<Range> Polygon2Hilbert(org.locationtech.jts.geom.Polygon p) {
        LinkedList<Range> rah = new LinkedList();
        GeometryFactory geometryFactory = new GeometryFactory();
        Envelope r = p.getEnvelopeInternal();
        int minx = (int) r.getMinX();
        int maxx = (int) r.getMaxX();
        int miny = (int) r.getMinY();
        int maxy = (int) r.getMaxY();
        LinkedList pp = new LinkedList();       
        for (int x=minx;x<maxx; x++) {
            for (int y=miny;y<maxy;y++) {
               if (p.contains(geometryFactory.createPoint(new Coordinate((double) x, (double) y)))) {
                   pp.add(hc.index(x,y));
               }
            }   
        }
        pp.sort(null);
        Collections.sort(pp);
        int i = 1;
        long sv = 0;
        long ev = 0;
        if (!pp.isEmpty()) {
            sv = (long) pp.get(0);
            ev = (long) pp.get(0);
        }
        while (i<pp.size()) {
            long nv = (long) pp.get(i);
            if ((nv-ev)==1) {
                ev = nv;
            } else {
                rah.add(new Range(sv,ev));
                sv = nv;
                ev = nv;
            }
            i++;
        }
        rah.add(new Range(sv,ev));
        return rah;
    }
    
    public final List<Range> Polygon2HilbertV2(org.locationtech.jts.geom.Polygon p) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Envelope r = p.getEnvelopeInternal();
        int minx = (int) r.getMinX();
        int maxx = (int) r.getMaxX();
        int miny = (int) r.getMinY();
        int maxy = (int) r.getMaxY();
        ArrayList<Long> pp = new ArrayList<>((maxx-minx)*(maxy-miny));       
        for (int x=minx;x<maxx; x++) {
            for (int y=miny;y<maxy;y++) {
               if (p.contains(geometryFactory.createPoint(new Coordinate((double) x, (double) y)))) {
                   pp.add(hc.index(x,y));
               }
            }   
        }
        pp.sort(null);
        Collections.sort(pp);
        int i = 1;
        long sv = 0;
        long ev = 0;
        if (!pp.isEmpty()) {
            sv = (long) pp.get(0);
            ev = (long) pp.get(0);
        }
        ArrayList<Range> rah = new ArrayList<>(pp.size());
        while (i<pp.size()) {
            long nv = (long) pp.get(i);
            if ((nv-ev)==1) {
                ev = nv;
            } else {
                rah.add(new Range(sv,ev));
                sv = nv;
                ev = nv;
            }
            i++;
        }
        rah.add(new Range(sv,ev));
        return rah;
    }
    
    public static void Hexes(long si[]) {
        for (int i=0; i<si.length; i++) {
            System.out.println(si[i]+" "+SpaceIT(Long.toBinaryString(si[i])));
        }
    }
}