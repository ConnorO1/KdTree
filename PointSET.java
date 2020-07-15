import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;


public class PointSET {

    private SET<Point2D> point2DS;

    // construct an empty set of points
    public PointSET() {
        point2DS = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return point2DS.isEmpty();
    }

    // number of points in the set
    public int size() {
        return point2DS.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!point2DS.contains(p)) {
            point2DS.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return point2DS.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenRadius(0.05);
        StdDraw.setPenColor(StdDraw.BLUE);
        for (Point2D p : point2DS) {
            StdDraw.point(p.x(), p.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Stack<Point2D> point2DStack = new Stack<Point2D>();
        for (Point2D p : point2DS) {
            if (rect.contains(p)) {
                point2DStack.push(p);
            }
        }
        return point2DStack;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (point2DS.isEmpty()) {
            return null;
        }
        // distance min guarenteed to be smaller than one
        double min = 2;
        Point2D current = null;
        for (Point2D x : point2DS) {
            if (p.distanceTo(x) < min) {
                min = p.distanceTo(x);
                current = x;
            }
        }
        return current;
    }

    public static void main(String[] args) {
        PointSET pp = new PointSET();
        Point2D a = new Point2D(0.1, 0.3);
        Point2D b = new Point2D(0.1, 0.5);
        Point2D c = new Point2D(0.4, 0.8);
        pp.insert(a);
        pp.insert(b);
        pp.insert(c);
        pp.draw();
        Point2D d = new Point2D(0.1, 0.2);
        System.out.println(pp.contains(a));
        System.out.println(pp.nearest(d).toString());

    }

}
