package com.rms.restaurant_service.quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuadTree {
    private final Rectangle boundary;
    private final int capacity; 
    private final List<Point> points = new ArrayList<>();
    private boolean divided = false;

    private QuadTree northwest;
    private QuadTree northeast;
    private QuadTree southwest;
    private QuadTree southeast;

    public QuadTree(Rectangle boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
    }

    public boolean insert(Point point) {
        if (!this.boundary.contains(point)) {
            return false;
        }

        if (this.points.size() < this.capacity) {
            this.points.add(point);
            return true;
        } else {
            if (!this.divided) {
                subdivide();
            }
            if (this.northeast.insert(point)) return true;
            if (this.northwest.insert(point)) return true;
            if (this.southeast.insert(point)) return true;
            if (this.southwest.insert(point)) return true;
        }

        return false;
    }

    private void subdivide() {
        double x = this.boundary.getX();
        double y = this.boundary.getY();
        double w = this.boundary.getW() / 2;
        double h = this.boundary.getH() / 2;

        Rectangle ne = new Rectangle(x + w, y + h, w, h);
        this.northeast = new QuadTree(ne, this.capacity);

        Rectangle nw = new Rectangle(x - w, y + h, w, h);
        this.northwest = new QuadTree(nw, this.capacity);

        Rectangle se = new Rectangle(x + w, y - h, w, h);
        this.southeast = new QuadTree(se, this.capacity);
        
        Rectangle sw = new Rectangle(x - w, y - h, w, h);
        this.southwest = new QuadTree(sw, this.capacity);

        this.divided = true;

        for (Point p : this.points) {
            this.northeast.insert(p);
            this.northwest.insert(p);
            this.southeast.insert(p);
            this.southwest.insert(p);
        }
        this.points.clear();
    }

    public List<Point> query(Rectangle range) {
        List<Point> found = new ArrayList<>();
        if (!this.boundary.intersects(range)) {
            return found; 
        }

        for (Point p : this.points) {
            if (range.contains(p)) {
                found.add(p);
            }
        }

        if (this.divided) {
            found.addAll(this.northwest.query(range));
            found.addAll(this.northeast.query(range));
            found.addAll(this.southwest.query(range));
            found.addAll(this.southeast.query(range));
        }
        return found;
    }

    public boolean remove(Point point) {
        if (!this.boundary.contains(point)) {
            return false;
        }

        for (int i = 0; i < this.points.size(); i++) {
            if (Objects.equals(this.points.get(i).getRestaurantId(), point.getRestaurantId())) {
                this.points.remove(i);
                return true;
            }
        }

        if (this.divided) {
            if (this.northeast.remove(point)) return true;
            if (this.northwest.remove(point)) return true;
            if (this.southeast.remove(point)) return true;
            if (this.southwest.remove(point)) return true;
        }

        return false;
    }
}
