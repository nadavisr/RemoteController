/*
 * Created by admin on 26/11/2017
 * Last modified 16:10 26/11/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P></P>
 */

public class MapCircle extends MapEllipse {

    /**
     * Create an circle instance which is described by a center and radius.
     *
     * @param centerPoint The center of the ellipse in Cartesian axis.
     * @param radius      The size of x radius of the ellipse.
     * @param color       A color described by integer.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapCircle(long id,@NonNull PointF centerPoint, @FloatRange(from = 0.1) float radius,
                     @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth)
            throws NullPointerException {
        super(id,centerPoint, radius, radius, 0, color, strokeWidth);
    }

    /**
     * Create an circle instance which is described by a center and radius.
     * The arc with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerPoint The center of the ellipse in Cartesian axis.
     * @param radius      The size of x radius of the ellipse.
     * @param color       A color described by integer.
     * @throws NullPointerException if the center point is null.
     */
    public MapCircle(long id,@NonNull PointF centerPoint, float radius, @ColorInt int color) throws NullPointerException {
        super(id,centerPoint, radius, radius, 0, color);
    }

    /**
     * Create an circle instance which is described by a center and radius.
     * The arc with default color (= {@value DEFAULT_COLOR}).
     *
     * @param centerPoint The center of the ellipse in Cartesian axis.
     * @param radius      The size of x radius of the ellipse.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapCircle(long id,@NonNull PointF centerPoint, float radius,
                     @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id,centerPoint, radius, radius, 0, strokeWidth);
    }

    /**
     * Create an circle instance which is described by a center and radius.
     * The arc with default color (= {@value DEFAULT_COLOR}) and
     * default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerPoint The center of the ellipse in Cartesian axis.
     * @param radius      The size of x radius of the ellipse.
     * @throws NullPointerException if the center point is null.
     */
    public MapCircle(long id,@NonNull PointF centerPoint, @FloatRange(from = 0.1) float radius)
            throws NullPointerException {
        super(id,centerPoint, radius, radius, 0);
    }
}
