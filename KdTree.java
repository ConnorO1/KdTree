import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root;
    private int size;

    // construct an empty set of points
    public KdTree() {
    }

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;
        private Node rt;
        // o level true if on an odd level - will comp by x
        private boolean oLevel;

        public Node(Point2D p, boolean oLevel) {
            this.p = p;
            this.oLevel = oLevel;
        }
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    private Node insert(Node x, Point2D p, boolean level, Node parent) {

        // if null we have rectangle from previous node
        if (x == null) {
            size++;
            Node pnew = new Node(p, level);

            // if parent is null it's the root and rect is whol square
            if (parent == null) {
                pnew.rect = new RectHV(0, 0, 1.0, 1.0);
                return pnew;
            }

            // if x xomparison made -> change min or max x
            if (parent.oLevel) {
                if (Point2D.X_ORDER.compare(p, parent.p) < 0) {
                    pnew.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(),
                                           parent.rect.ymax());
                }
                else {
                    pnew.rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(),
                                           parent.rect.ymax());
                }
            }
            // y comp made -> change min or max y
            else {
                if (Point2D.Y_ORDER.compare(p, parent.p) < 0) {
                    pnew.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                           parent.rect.xmax(), parent.p.y());
                }
                else {
                    pnew.rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(),
                                           parent.rect.ymax());
                }

            }
            return pnew;
        }

        int cmp;
        if (x.oLevel) {
            cmp = Point2D.X_ORDER.compare(p, x.p);
        }
        else {
            cmp = Point2D.Y_ORDER.compare(p, x.p);
        }

        if (cmp < 0) {
            x.lb = insert(x.lb, p, !level, x);
        }
        // if point bigger OR EQUAL go right
        else {
            x.rt = insert(x.rt, p, !level, x);
        }
        return x;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!contains(p)) {
            root = insert(root, p, true, null);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return get(root, p) != null;
    }

    // will simply return the same point if contained in tree
    private Point2D get(Node x, Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (x == null) {
            return null;
        }
        if (p.equals(x.p)) return p;

        int cmp;
        if (x.oLevel) {
            cmp = Point2D.X_ORDER.compare(p, x.p);
        }
        else {
            cmp = Point2D.Y_ORDER.compare(p, x.p);
        }

        if (cmp < 0) {
            return get(x.lb, p);
        }
        // if point bigger OR EQUAL go right
        else {
            return get(x.rt, p);
        }
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        Stack<Node> nStack = new Stack<Node>();
        nodeIter(root, nStack);
        for (Node n : nStack) {
            StdDraw.point(n.p.x(), n.p.y());
        }
        StdDraw.setPenRadius();
        for (Node n : nStack) {
            if (n.oLevel) {
                // draw a line at x coord between ymin and ymax
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
            }
            else {
                // draw line at y coord between xmin and xmax
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
            }
        }

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Stack<Point2D> point2DStack = new Stack<Point2D>();
        range(root, rect, point2DStack);
        return point2DStack;

    }

    private void range(Node x, RectHV rect, Stack<Point2D> point2DS) {
        if (x == null) {
            return;
        }
        if (rect.contains(x.p)) {
            point2DS.push(x.p);
        }
        if (x.lb != null) {
            if (rect.intersects(x.lb.rect)) {
                range(x.lb, rect, point2DS);
            }
        }
        if (x.rt != null) {
            if (rect.intersects(x.rt.rect)) {
                range(x.rt, rect, point2DS);
            }
        }
    }

    // return iterable of all nodes - for drawing
    private void nodeIter(Node x, Stack<Node> nodeStack) {
        if (x == null) {
            return;
        }
        nodeStack.push(x);
        nodeIter(x.lb, nodeStack);
        nodeIter(x.rt, nodeStack);

    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            return null;
        }
        return nearest2(root, p, root).p;

    }

    private Node nearest2(Node x, Point2D p, Node min) {
        if (x == null) {
            return min;
        }
        if (x.p.equals(p)) {
            return x;
        }

        double dist = x.p.distanceSquaredTo(p);
        if (dist < min.p.distanceSquaredTo(p)) {
            min = x;
        }

        int cmp;
        if (x.oLevel) {
            cmp = Point2D.X_ORDER.compare(p, x.p);
        }
        else {
            cmp = Point2D.Y_ORDER.compare(p, x.p);
        }


        if (cmp < 0) {
            min = nearest2(x.lb, p, min);
            if (x.rt != null) {
                double dist2 = x.rt.rect.distanceSquaredTo(p);
                if (dist2 < min.p.distanceSquaredTo(p)) {
                    min = nearest2(x.rt, p, min);
                }
            }
        }
        else {
            min = nearest2(x.rt, p, min);
            if (x.lb != null) {
                double dist2 = x.lb.rect.distanceSquaredTo(p);
                if (dist2 < min.p.distanceSquaredTo(p)) {
                    min = nearest2(x.lb, p, min);
                }
            }
        }

        return min;

    }

    public static void main(String[] args) {
        KdTree pp = new KdTree();
        Point2D a = new Point2D(0.7, 0.2);
        Point2D b = new Point2D(0.5, 0.4);
        Point2D c = new Point2D(0.2, 0.3);
        Point2D e = new Point2D(0.4, 0.7);
        Point2D f = new Point2D(0.9, 0.6);

        pp.insert(a);
        pp.insert(b);
        pp.insert(c);
        pp.insert(e);
        pp.insert(f);
        System.out.println(pp.root.lb.rt.p.toString());

    }
}
