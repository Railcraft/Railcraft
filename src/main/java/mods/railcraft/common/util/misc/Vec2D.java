/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * A mutable 2D Vector class that supports a broader range of math operations
 * than Minecraft's immutable Vec3D class.
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Vec2D extends Point2D.Double
{

    public static final float DEGREES_45 = (float)(0.25 * Math.PI);
    public static final float DEGREES_90 = (float)(0.5 * Math.PI);
    public static final float DEGREES_135 = (float)(0.75 * Math.PI);
    public static final float DEGREES_180 = (float)(Math.PI);
    public static final float DEGREES_270 = (float)(1.5 * Math.PI);

    public Vec2D()
    {
    }

    public Vec2D(final Point2D p)
    {
        super(p.getX(), p.getY());
    }

    public Vec2D(double x, double y)
    {
        super(x, y);
    }

    @Override
    public Vec2D clone()
    {
        return new Vec2D(x, y);
    }

    public static Vec2D fromPolar(double angle, float magnitude)
    {
        Vec2D v = new Vec2D();
        v.setFromPolar(angle, magnitude);
        return v;
    }

    static public Vec2D add(final Point2D a, final Point2D b)
    {
        return new Vec2D(a.getX() + b.getX(), a.getY() + b.getY());
    }

    static public Vec2D subtract(final Point2D a, final Point2D b)
    {
        return new Vec2D(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

//==============================================================================
    public void setFromPolar(double angle, float magnitude)
    {
        float x = (float)Math.cos(angle) * magnitude;
        float y = (float)Math.sin(angle) * magnitude;
        setLocation(x, y);
    }

    public void zero()
    {
        x = y = 0;
    }

    public void normalize()
    {
        double mag = magnitude();
        if(mag != 0) {
            setLocation(x / mag, y / mag);
        }
    }

    public Vec2D unitVector()
    {
        Vec2D v = this.clone();
        v.normalize();
        return v;
    }

    public double magnitude()
    {
        return Math.sqrt(x * x + y * y);
    }

    public void setMagnitude(float mag)
    {
        setFromPolar(angle(), mag);
    }

    public double magnitudeSq()
    {
        return x * x + y * y;
    }

    public void negate()
    {
        x = -x;
        y = -y;
    }

    public float angle()
    {
        return (float)Math.atan2(y, x);
    }

    public void rotate(double theta)
    {
        double nx = x * (float)Math.cos(theta) - y * (float)Math.sin(theta);
        double ny = x * (float)Math.sin(theta) + y * (float)Math.cos(theta);
        setLocation(nx, ny);
    }

    public void rotate90()
    {
        double ox = x;
        x = -y;
        y = ox;
    }

    public void rotate270()
    {
        double ox = x;
        x = y;
        y = -ox;
    }

    public void subtract(final Point2D p)
    {
        x -= p.getX();
        y -= p.getY();
    }

    public void subtract(int x, int y)
    {
        this.x -= x;
        this.y -= y;
    }

    public void subtract(final double x, final double y)
    {
        this.x -= (int)x;
        this.y -= (int)y;
    }

    public void add(final Point2D p)
    {
        x += p.getX();
        y += p.getY();
    }

    public void add(int x, int y)
    {
        this.x += x;
        this.y += y;
    }

    public void add(final double x, final double y)
    {
        this.x += (int)x;
        this.y += (int)y;
    }

    public void transform(AffineTransform a)
    {
        setLocation(a.transform(this, null));
    }

    public Vec2D makeTransform(AffineTransform a)
    {
        return new Vec2D(a.transform(this, null));
    }

    /**
     * Finds the dot product of two vectors.
     * @param v
     * @return
     */
    public double dotProduct(final Point2D v)
    {
        return x * v.getX() + y * v.getY();
    }

    /**
     * Finds the absolute angle between two vectors using the Law of Cosines:<br>
     * <b>V . W = |V||W|cos(a)</b>
     */
    public double angleBetween(final Vec2D v)
    {
        double a = dotProduct(v);
        double magnitude = magnitude() * v.magnitude();
        if(magnitude == 0) {
            return 0;
        }
        a /= magnitude;

        // Check bounds, if you don't you WILL get NaNs from acos.
        // This is becuase of slight errors in the floating point arithmatic.
        if(a > 1) {
            a = 1;
        } else if(a < -1) {
            a = -1;
        }
        return Math.acos(a);
    }

    public double angleTo(Point2D a)
    {
        return Math.atan2(a.getY() - y, a.getX() - x);
    }

    public double angleFrom(Point2D a)
    {
        return Math.atan2(y - a.getY(), x - a.getX());
    }

    public void scale(float scale)
    {
        x *= scale;
        y *= scale;
    }

    public void addScale(float scale, Point2D v)
    {
        setLocation(x + v.getX() * scale, y + v.getY() * scale);
    }
}
