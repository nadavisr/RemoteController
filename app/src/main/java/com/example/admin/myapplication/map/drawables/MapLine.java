/*
 * Created by admin on 19/11/2017
 * Last modified 16:37 19/11/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>A class which describes line map element that can be drawn on {@link com.example.admin.myapplication.map.MapView}. </P>
 */
public class MapLine extends MapDrawable {

    //region Fields

    private PointF m_firstPoint;

    private PointF m_secondPoint;

    //endregion

    //region Constructors

    /**
     * @param firstPoint  The first point of the line in Cartesian axis.
     * @param secondPoint The second point of the line in Cartesian axis.
     * @param color       A color described by integer.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapLine(long id, PointF firstPoint, PointF secondPoint, @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, color, strokeWidth);
        init(firstPoint, secondPoint);
    }

    /**
     * Create MapLine instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param firstPoint  The first point of the line in Cartesian axis.
     * @param secondPoint The second point of the line in Cartesian axis.
     * @param color       A color described by integer.
     * @throws NullPointerException if one of the points are null.
     */
    public MapLine(long id, PointF firstPoint, PointF secondPoint, @ColorInt int color) throws NullPointerException {
        super(id, color);
        init(firstPoint, secondPoint);
    }

    /**
     * Create MapLine instance with default color (= {@value DEFAULT_COLOR}).
     *
     * @param firstPoint  The first point of the line in Cartesian axis.
     * @param secondPoint The second point of the line in Cartesian axis.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapLine(long id, PointF firstPoint, PointF secondPoint, @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, strokeWidth);
        init(firstPoint, secondPoint);
    }

    /**
     * Create MapLine instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param firstPoint  The first point of the line in Cartesian axis.
     * @param secondPoint The second point of the line in Cartesian axis.
     * @throws NullPointerException if one of the points are null.
     */
    public MapLine(long id, PointF firstPoint, PointF secondPoint) throws NullPointerException {
        super(id);
        init(firstPoint, secondPoint);
    }

    /**
     * @param x1          The x value of the first point in Cartesian axis.
     * @param y1          The y value of the first point in Cartesian axis.
     * @param x2          The x value of the second point in Cartesian axis.
     * @param y2          The y value of the second point in Cartesian axis.
     * @param color       A color described by integer.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapLine(long id, float x1, float y1, float x2, float y2, @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) {
        super(id, color, strokeWidth);
        init(x1, y1, x2, y2);
    }

    /**
     * Create MapLine instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param x1    The x value of the first point in Cartesian axis.
     * @param y1    The y value of the first point in Cartesian axis.
     * @param x2    The x value of the second point in Cartesian axis.
     * @param y2    The y value of the second point in Cartesian axis.
     * @param color A color described by integer.
     */
    public MapLine(long id, float x1, float y1, float x2, float y2, @ColorInt int color) {
        super(id, color);
        init(x1, y1, x2, y2);
    }

    /**
     * Create MapLine instance with default color (= {@value DEFAULT_COLOR}).
     *
     * @param x1          The x value of the first point in Cartesian axis.
     * @param y1          The y value of the first point in Cartesian axis.
     * @param x2          The x value of the second point in Cartesian axis.
     * @param y2          The y value of the second point in Cartesian axis.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapLine(long id, float x1, float y1, float x2, float y2, @FloatRange(from = 0.1) float strokeWidth) {
        super(id, strokeWidth);
        init(x1, y1, x2, y2);
    }

    /**
     * Create MapLine instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param x1 The x value of the first point in Cartesian axis.
     * @param y1 The y value of the first point in Cartesian axis.
     * @param x2 The x value of the second point in Cartesian axis.
     * @param y2 The y value of the second point in Cartesian axis.
     */
    public MapLine(long id, float x1, float y1, float x2, float y2) {
        super(id);
        init(x1, y1, x2, y2);
    }

    private void init(PointF firstPoint, PointF secondPoint) throws NullPointerException {
        if (firstPoint == null) {
            throw new NullPointerException("firstPoint parameter is null.");
        }
        if (secondPoint == null) {
            throw new NullPointerException("secondPoint parameter is null.");
        }

        m_firstPoint = firstPoint;
        m_secondPoint = secondPoint;
    }

    private void init(float x1, float y1, float x2, float y2) {
        m_firstPoint = new PointF(x1, y1);
        m_secondPoint = new PointF(x2, y2);
    }

    //endregion

    //region Getters

    /**
     * @return The first point in the line.
     */
    public PointF getFirstPoint() {
        return m_firstPoint;
    }

    /**
     * @return The second point in the line.
     */
    public PointF getSecondPoint() {
        return m_secondPoint;
    }

    //endregion

    //region Public Methods


    public void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) {
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        float layoutWidth = maxX - minX;
        float layoutHeight = maxY - minY;

        float scale = s_BoundsCoefficient* Math.min(canvasHeight / layoutHeight, canvasWidth / layoutWidth);

        float x1 = scale * (m_firstPoint.x - minX);
        float y1 = scale * (m_firstPoint.y - minY);
        float x2 = scale * (m_secondPoint.x - minX);
        float y2 = scale * (m_secondPoint.y - minY);

        //for portrait:
        //canvas.drawLine(x1, y1, x2, y2, m_Paint);

        //for landscape:
        y1 = canvasHeight - y1;
        y2 = canvasHeight - y2;
        canvas.drawLine(x1, y1, x2, y2, m_Paint);
    }


    //endregion
}
