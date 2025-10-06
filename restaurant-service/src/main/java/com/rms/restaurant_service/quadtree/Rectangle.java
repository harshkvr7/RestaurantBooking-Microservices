package com.rms.restaurant_service.quadtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rectangle {
    
    private double x; // Center x (longitude)
    private double y; // Center y (latitude)
    private double w; // Half-width
    private double h; // Half-height

    public boolean contains(Point point) {
        return (point.getX() >= this.x - this.w &&
                point.getX() <= this.x + this.w &&
                point.getY() >= this.y - this.h &&
                point.getY() <= this.y + this.h);
    }

    public boolean intersects(Rectangle range) {
        return !(range.getX() - range.getW() > this.x + this.w ||
                 range.getX() + range.getW() < this.x - this.w ||
                 range.getY() - range.getH() > this.y + this.h ||
                 range.getY() + range.getH() < this.y - this.h);
    }
}
