/*
 * Created by admin on 19/11/2017
 * Last modified 17:28 19/11/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>An abstract class which describes map elements that can be drawn on {@link com.example.admin.myapplication.map.MapView}. </P>
 */

public abstract class MapDrawable implements IMapDrawable {

    //region Constants

    protected final static float s_BoundsCoefficient = 0.98f;

    public final static float DEFAULT_STROKE_WIDTH = 6.0f;

    public final static int DEFAULT_COLOR = Color.BLACK;

    //endregion

    //region Fields

    private final long m_id;

    /**
     * The paint of the drawable.
     */
    protected final Paint m_Paint;

    //endregion

    //region Constructors

    /**
     * Create MapDrawable instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     */
    public MapDrawable(long id) {
        this(id, DEFAULT_COLOR, DEFAULT_STROKE_WIDTH);
    }

    /**
     * Create MapDrawable instance with default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapDrawable(long id, @FloatRange(from = 0.1) float strokeWidth) {
        this(id, DEFAULT_COLOR, strokeWidth);
    }

    /**
     * Create MapDrawable instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param color A color described by integer.
     */
    public MapDrawable(long id, @ColorInt int color) {
        this(id, color, DEFAULT_STROKE_WIDTH);
    }


    /**
     * @param color       A color described by integer.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapDrawable(long id, @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) {
        m_id = id;
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeWidth(strokeWidth);
        m_Paint.setColor(color);
    }

    //endregion

    //region Getters

    /**
     * @return The color of the drawable.
     */
    public @ColorInt
    int getColor() {
        return m_Paint.getColor();
    }

    /**
     * @return An unique id of the drawable.
     */
    public long getId() {
        return m_id;
    }

    /**
     * @return The stroke width of the drawable.
     */
    public float getStrokeWidth() {
        return m_Paint.getStrokeWidth();
    }

    //endregion

    //region Public Methods

    /**
     * A method that draw the drawable map element on canvas.
     *
     * @param canvas The canvas whose element is painted
     * @throws NullPointerException if the canvas is null.
     */
    public void drawOnCanvas(@NonNull Canvas canvas) throws NullPointerException {
        this.drawOnCanvas(canvas, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    //endregion

    //region Abstract Methods

    /**
     * A method that draw the drawable map element on canvas, and can adjust the size of the element
     * to a rectangle of a different size than the canvas.
     *
     * @param canvas The canvas whose element is painted
     * @param minX   The X of the bottom side of the rectangle.
     * @param minY   The Y of the left side of the rectangle.
     * @param maxX   The X of the top side of the rectangle.
     * @param maxY   The Y of the right side of the rectangle.
     * @throws NullPointerException if the canvas is null.
     */
    public abstract void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) throws NullPointerException;

    //endregion

    //region Object Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapDrawable)) return false;

        MapDrawable that = (MapDrawable) o;

        return m_id == that.m_id;
    }

    @Override
    public int hashCode() {
        return (int) (m_id ^ (m_id >>> 32));
    }

    @Override
    public String toString() {
        return "MapDrawable{ID=" + m_id + '}';
    }

    //endregion
}
