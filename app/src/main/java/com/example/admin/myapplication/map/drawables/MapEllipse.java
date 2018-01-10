/*
 * Created by admin on 26/11/2017
 * Last modified 16:09 26/11/17
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

public class MapEllipse extends MapArc {

    /**
     * Create an Arc instance which is described by ellipse that blocked by a rectangle.
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     */
    public MapEllipse(long id,float left, float top, float right, float bottom, int rotateAngleInDegrees,
                      @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) {
        super(id,left, top, right, bottom, 0, 360, rotateAngleInDegrees, color, strokeWidth);
    }

    /**
     * Create an Arc instance which is described by ellipse that blocked by a rectangle.
     * The arc with default color (= {@value DEFAULT_COLOR}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     */
    public MapEllipse(long id,float left, float top, float right, float bottom, int rotateAngleInDegrees, @FloatRange(from = 0.1) float strokeWidth) {
        super(id,left, top, right, bottom, 0, 360, rotateAngleInDegrees, strokeWidth);
    }

    /**
     * Create an Arc instance which is described by ellipse that blocked by a rectangle.
     * The arc with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     */
    public MapEllipse(long id,float left, float top, float right, float bottom, int rotateAngleInDegrees, @ColorInt int color) {
        super(id,left, top, right, bottom, 0, 360, rotateAngleInDegrees, color);
    }

    /**
     * Create an Arc instance which is described by ellipse that blocked by a rectangle.
     * The arc with default color (= {@value DEFAULT_COLOR}) and
     * default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     */
    public MapEllipse(long id,float left, float top, float right, float bottom, int rotateAngleInDegrees) {
        super(id,left, top, right, bottom, 0, 360, rotateAngleInDegrees);
    }

    /**
     * Create an ellipse instance which is described by a center and two radius.
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapEllipse(long id,@NonNull PointF centerPoint, float x_radius, float y_radius, int rotateAngleInDegrees,
                      @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id,centerPoint, x_radius, y_radius, 0, 360, rotateAngleInDegrees, color,strokeWidth);
    }

    /**
     * Create an ellipse instance which is described by a center and two radius.
     * The arc with default color (= {@value DEFAULT_COLOR}).
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @throws NullPointerException if the center point is null.
     */
    public MapEllipse(long id,@NonNull PointF centerPoint, float x_radius, float y_radius, int rotateAngleInDegrees,
                      @ColorInt int color) throws NullPointerException {
        super(id,centerPoint, x_radius, y_radius, 0, 360, rotateAngleInDegrees,color);
    }

    /**
     * Create an ellipse instance which is described by a center and two radius.
     * The arc with default  stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapEllipse(long id,@NonNull PointF centerPoint, float x_radius, float y_radius, int rotateAngleInDegrees,
                      @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id,centerPoint, x_radius, y_radius, 0, 360, rotateAngleInDegrees,strokeWidth);
    }


    /**
     * Create an ellipse instance which is described by a center and two radius.
     * The arc with default color (= {@value DEFAULT_COLOR}) and
     * default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @throws NullPointerException if the center point is null.
     */
    public MapEllipse(long id,@NonNull PointF centerPoint, float x_radius, float y_radius, int rotateAngleInDegrees)
            throws NullPointerException {
        super(id,centerPoint, x_radius, y_radius, 0, 360, rotateAngleInDegrees);
    }
}
