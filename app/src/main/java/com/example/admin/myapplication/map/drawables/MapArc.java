/*
 * Created by admin on 19/11/2017
 * Last modified 16:37 19/11/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>A class which describes archer map element that can be drawn on {@link com.example.admin.myapplication.map.MapView}. </P>
 */
public class MapArc extends MapDrawable {

    //region Fields

    private float m_left;
    private float m_top;
    private float m_right;
    private float m_bottom;
    private int m_startAngle;
    private int m_sweepAngle;
    private int m_rotateAngle;
    private RectF m_rectF;

    //endregion

    //region Constructors


    /**
     * Create an Arc instance which is described by a part of ellipse that blocked by a rectangle.
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     */
    public MapArc(long id, float left, float top, float right, float bottom,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color,
                  @FloatRange(from = 0.1) float strokeWidth) {
        super(id, color, strokeWidth);

        init(left, top, right, bottom, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a part of ellipse that blocked by a rectangle.
     * The arc with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     */
    public MapArc(long id, float left, float top, float right, float bottom,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color) {
        super(id, color);

        init(left, top, right, bottom, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a part of ellipse that blocked by a rectangle.
     * The arc with default color (= {@value DEFAULT_COLOR}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     */
    public MapArc(long id, float left, float top, float right, float bottom,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees,
                  @FloatRange(from = 0.1) float strokeWidth) {
        super(id, strokeWidth);

        init(left, top, right, bottom, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a part of ellipse that blocked by a rectangle.
     * The arc with default color (= {@value DEFAULT_COLOR}) and
     * default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param left                 The left side of the rectangle that blocks the ellipse.
     * @param top                  The top side of the rectangle that blocks the ellipse.
     * @param right                The right side of the rectangle that blocks the ellipse.
     * @param bottom               The bottom side of the rectangle that blocks the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     */
    public MapArc(long id, float left, float top, float right, float bottom,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees) {
        this(id, left, top, right, bottom, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees, DEFAULT_COLOR);
    }

    /**
     * Create an Arc instance which is described by a center and two radius.
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapArc(long id, @NonNull PointF centerPoint, @FloatRange(from = 0.1) float x_radius, @FloatRange(from = 0.1) float y_radius,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color,
                  @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, color, strokeWidth);
        init(centerPoint.x - x_radius, centerPoint.y + y_radius, centerPoint.x + x_radius,
                centerPoint.y - y_radius, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a center and two radius.
     * The arc with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     * *
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param color                A color described by integer.
     * @throws NullPointerException if the center point is null.
     */
    public MapArc(long id, @NonNull PointF centerPoint, @FloatRange(from = 0.1) float x_radius, @FloatRange(from = 0.1) float y_radius,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color) throws NullPointerException {
        super(id, color);
        init(centerPoint.x - x_radius, centerPoint.y + y_radius, centerPoint.x + x_radius,
                centerPoint.y - y_radius, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a center and two radius.
     * The arc with default color (= {@value DEFAULT_COLOR}).
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if the center point is null.
     */
    public MapArc(long id, @NonNull PointF centerPoint, @FloatRange(from = 0.1) float x_radius, @FloatRange(from = 0.1) float y_radius,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees,
                  @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, strokeWidth);
        init(centerPoint.x - x_radius, centerPoint.y + y_radius, centerPoint.x + x_radius,
                centerPoint.y - y_radius, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees);
    }

    /**
     * Create an Arc instance which is described by a center and two radius.
     * The arc with default color (= {@value DEFAULT_COLOR})
     * and default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerPoint          The center of the ellipse in Cartesian axis.
     * @param x_radius             The size of x radius of the ellipse.
     * @param y_radius             The size of y radius of the ellipse.
     * @param startAngleInDegrees  The angle relative to origins which the arc begins on the ellipse archer.
     * @param endAngleInDegrees    The angle relative to origins which the arc ends on the ellipse archer.
     * @param rotateAngleInDegrees The rotate angle of the ellipse that relative to origins.
     * @throws NullPointerException if the center point is null.
     */
    public MapArc(long id, @NonNull PointF centerPoint, @FloatRange(from = 0.1) float x_radius, @FloatRange(from = 0.1) float y_radius,
                  @IntRange(from = 0, to = 360) int startAngleInDegrees,
                  @IntRange(from = 0, to = 360) int endAngleInDegrees,
                  @IntRange(from = 0, to = 360) int rotateAngleInDegrees) {
        this(id, centerPoint.x - x_radius, centerPoint.y + y_radius, centerPoint.x + x_radius,
                centerPoint.y - y_radius, startAngleInDegrees, endAngleInDegrees, rotateAngleInDegrees, DEFAULT_COLOR);
    }

    private void init(float left, float top, float right, float bottom, int startAngleInDegrees, int endAngleInDegrees, int rotateAngleInDegrees) {
        m_left = left;
        m_top = top;
        m_right = right;
        m_bottom = bottom;

        if (startAngleInDegrees > 360) {
            startAngleInDegrees -= (startAngleInDegrees / 360) * 360;
        }

        if (endAngleInDegrees > 360) {
            endAngleInDegrees -= (endAngleInDegrees / 360) * 360;
        }

        m_startAngle = 360 - endAngleInDegrees;
        m_sweepAngle = endAngleInDegrees - startAngleInDegrees;

        m_rotateAngle = rotateAngleInDegrees;

        m_rectF = new RectF();
    }
    //endregion

    //region Getters

    /**
     * @return The left side of the rectangle that blocks the ellipse.
     */
    public float getLeft() {
        return m_left;
    }

    /**
     * @return The top side of the rectangle that blocks the ellipse.
     */
    public float getTop() {
        return m_top;
    }

    /**
     * @return The right side of the rectangle that blocks the ellipse.
     */
    public float getRight() {
        return m_right;
    }

    /**
     * @return The bottom side of the rectangle that blocks the ellipse.
     */
    public float getBottom() {
        return m_bottom;
    }

    /**
     * @return The angle relative to origins which the arc begins on the ellipse archer.
     */
    public int getStartAngle() {
        return m_startAngle;
    }

    /**
     * @return The angle relative to origins which the arc ends on the ellipse archer.
     */
    public int getSweepAngle() {
        return m_sweepAngle;
    }

    /**
     * @return The rotate angle of the ellipse that relative to origins.
     */
    public int getRotateAngle() {
        return m_rotateAngle;
    }

    //endregion

    //region Public Methods

    public void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) {

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        float layoutWidth = maxX - minX;
        float layoutHeight = maxY - minY;

        float scale = s_BoundsCoefficient* Math.min(canvasHeight / layoutHeight, canvasWidth / layoutWidth);

        float left = scale * (m_left - minX);
        float bottom = scale * (m_bottom - minY);
        float right = scale * (m_right - minX);
        float top = scale * (m_top - minY);

        // for  landscape region :
        bottom = canvasHeight - bottom;
        top = canvasHeight - top;

        if (bottom > top) {
            float temp = bottom;
            bottom = top;
            top = temp;
        }
        //end for landscape region

        m_rectF.set(left, bottom, right, top);

        if (m_rotateAngle != 0) {
            canvas.save();
            canvas.rotate(m_rotateAngle, left + ((right - left) / 2.0f), bottom + ((top - bottom) / 2.0f));
        }

        canvas.drawArc(m_rectF, m_startAngle, m_sweepAngle, false, m_Paint);

        if (m_rotateAngle != 0) {
            canvas.restore();
        }

    }

    //endregion
}
