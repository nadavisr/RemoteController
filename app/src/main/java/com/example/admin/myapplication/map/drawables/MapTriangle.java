
/*
 * Created by admin on 10/12/2017
 * Last modified 15:22 10/12/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>A class which describes triangular map element that can be drawn on {@link com.example.admin.myapplication.map.MapView}. </P>
 */
public class MapTriangle extends MapDrawable {

    //region Fields

    private PointF m_firstVertex;

    private PointF m_secondVertex;

    private PointF m_thirdVertex;

    protected PointF m_CenterVertex;

    private int m_rotateAngleInDegrees;

    private Matrix m_rotateMatrix;

    private Path m_path;

    //endregion

    //region Constructors

    /**
     * @param centerVertex         The centerVertex of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF centerVertex, float width, float height,
                       @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color,
                       @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, color, strokeWidth);
        init(centerVertex, width, height, rotateAngleInDegrees);

    }

    /**
     * Create MapTriangle instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param centerVertex         The centerVertex of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @param color                A color described by integer.
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF centerVertex, float width, float height,
                       @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color) throws NullPointerException {
        super(id, color);
        init(centerVertex, width, height, rotateAngleInDegrees);
    }

    /**
     * Create MapTriangle instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param centerVertex         The centerVertex of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF centerVertex, float width, float height,
                       @IntRange(from = 0, to = 360) int rotateAngleInDegrees) throws NullPointerException {
        super(id);
        init(centerVertex, width, height, rotateAngleInDegrees);
    }

    /**
     * @param firstVertex  The first vertex of the triangle in Cartesian axis.
     * @param secondVertex The second vertex of the triangle in Cartesian axis.
     * @param thirdVertex  The third vertex of the triangle in Cartesian axis.
     * @param color        A color described by integer.
     * @param strokeWidth  The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF firstVertex, PointF secondVertex, PointF thirdVertex, @ColorInt int color,
                       @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, color, strokeWidth);
        init(firstVertex, secondVertex, thirdVertex);
    }

    /**
     * Create MapTriangle instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param firstVertex  The first vertex of the triangle in Cartesian axis.
     * @param secondVertex The second vertex of the triangle in Cartesian axis.
     * @param thirdVertex  The third vertex of the triangle in Cartesian axis.
     * @param color        A color described by integer.
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF firstVertex, PointF secondVertex, PointF thirdVertex, @ColorInt int color)
            throws NullPointerException {
        super(id, color);
        init(firstVertex, secondVertex, thirdVertex);
    }

    /**
     * Create MapTriangle instance with default color (= {@value DEFAULT_COLOR}).
     *
     * @param firstVertex  The first vertex of the triangle in Cartesian axis.
     * @param secondVertex The second vertex of the triangle in Cartesian axis.
     * @param thirdVertex  The third vertex of the triangle in Cartesian axis.
     * @param strokeWidth  The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF firstVertex, PointF secondVertex, PointF thirdVertex,
                       @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(id, strokeWidth);
        init(firstVertex, secondVertex, thirdVertex);
    }

    /**
     * Create MapTriangle instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param firstVertex  The first vertex of the triangle in Cartesian axis.
     * @param secondVertex The second vertex of the triangle in Cartesian axis.
     * @param thirdVertex  The third vertex of the triangle in Cartesian axis.
     * @throws NullPointerException if one of the points are null.
     */
    public MapTriangle(long id, PointF firstVertex, PointF secondVertex, PointF thirdVertex) throws NullPointerException {
        super(id);
        init(firstVertex, secondVertex, thirdVertex);
    }

    /**
     * @param x1          The x value of the first vertex in Cartesian axis.
     * @param y1          The y value of the first vertex in Cartesian axis.
     * @param x2          The x value of the second vertex in Cartesian axis.
     * @param y2          The y value of the second vertex in Cartesian axis.
     * @param x3          The x value of the second vertex in Cartesian axis.
     * @param y3          The y value of the second vertex in Cartesian axis.
     * @param color       A color described by integer.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapTriangle(long id, float x1, float y1, float x2, float y2, float x3, float y3,
                       @ColorInt int color, @FloatRange(from = 0.1) float strokeWidth) {
        super(id, color, strokeWidth);
        init(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Create MapTriangle instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param x1    The x value of the first vertex in Cartesian axis.
     * @param y1    The y value of the first vertex in Cartesian axis.
     * @param x2    The x value of the second vertex in Cartesian axis.
     * @param y2    The y value of the second vertex in Cartesian axis.
     * @param x3    The x value of the second vertex in Cartesian axis.
     * @param y3    The y value of the second vertex in Cartesian axis.
     * @param color A color described by integer.
     */
    public MapTriangle(long id, float x1, float y1, float x2, float y2, float x3, float y3, @ColorInt int color) {
        super(id, color);
        init(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Create MapTriangle instance with default color (= {@value DEFAULT_COLOR}).
     *
     * @param x1          The x value of the first vertex in Cartesian axis.
     * @param y1          The y value of the first vertex in Cartesian axis.
     * @param x2          The x value of the second vertex in Cartesian axis.
     * @param y2          The y value of the second vertex in Cartesian axis.
     * @param x3          The x value of the second vertex in Cartesian axis.
     * @param y3          The y value of the second vertex in Cartesian axis.
     * @param strokeWidth The width of the stroke (greater then 0.1).
     */
    public MapTriangle(long id, float x1, float y1, float x2, float y2, float x3, float y3, @FloatRange(from = 0.1) float strokeWidth) {
        super(id, strokeWidth);
        init(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Create MapTriangle instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param x1 The x value of the first vertex in Cartesian axis.
     * @param y1 The y value of the first vertex in Cartesian axis.
     * @param x2 The x value of the second vertex in Cartesian axis.
     * @param y2 The y value of the second vertex in Cartesian axis.
     * @param x3 The x value of the second vertex in Cartesian axis.
     * @param y3 The y value of the second vertex in Cartesian axis.
     */
    public MapTriangle(long id, float x1, float y1, float x2, float y2, float x3, float y3) {
        super(id);
        init(x1, y1, x2, y2, x3, y3);
    }

    private void init(PointF firstPoint, PointF secondPoint, PointF thirdPoint) throws NullPointerException {
        if (firstPoint == null) {
            throw new NullPointerException("First vertex parameter is null.");
        }
        if (secondPoint == null) {
            throw new NullPointerException("Second vertex parameter is null.");
        }
        if (thirdPoint == null) {
            throw new NullPointerException("Third vertex parameter is null.");
        }

        m_firstVertex = firstPoint;
        m_secondVertex = secondPoint;
        m_thirdVertex = thirdPoint;
        m_rotateAngleInDegrees = 0;
        m_CenterVertex = null;
        m_path = new Path();
        m_rotateMatrix = new Matrix();
    }

    private void init(float x1, float y1, float x2, float y2, float x3, float y3) {
        m_firstVertex = new PointF(x1, y1);
        m_secondVertex = new PointF(x2, y2);
        m_thirdVertex = new PointF(x3, y3);
        m_rotateAngleInDegrees = 0;
        m_CenterVertex = null;
        m_path = new Path();
    }

    private void init(PointF center, float width, float height, int rotateAngleInDegrees) {
        if (center == null) {
            throw new NullPointerException("Center point is null.");
        }

        float halfHeight = height / 2, halfWidth = width / 2;
        init(center.x - halfWidth, center.y - halfHeight, center.x - halfWidth, center.y + halfHeight,
                center.x + halfWidth, center.y);
        m_CenterVertex = center;

        m_rotateAngleInDegrees = rotateAngleInDegrees % 360;
        m_rotateMatrix = new Matrix();
    }

    //endregion

    //region Getters

    /**
     * @return The first vertex of the triangle.
     */
    public PointF getFirstVertex() {
        return m_firstVertex;
    }

    /**
     * @return The second vertex of the triangle.
     */
    public PointF getSecondVertex() {
        return m_secondVertex;
    }

    /**
     * @return The third vertex of the triangle.
     */
    public PointF getThirdVertex() {
        return m_thirdVertex;
    }

    /**
     * @return The vertex of the center of triangle.
     */
    public int getRotateAngleInDegrees() {
        return m_rotateAngleInDegrees;
    }
    //endregion

    //region Setters

    /**
     * The method update the vertex of the of the triangle.
     *
     * @param centerX              The x point of the center vertex of the triangle in Cartesian axis.
     * @param centerY              The y point of the center vertex of the triangle in Cartesian axis.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     */
    protected void setVertexes(float centerX, float centerY, float width, float height,
                               @IntRange(from = 0, to = 360) int rotateAngleInDegrees) {

        float halfHeight = height / 2, halfWidth = width / 2;

        synchronized (MapTriangle.class) {
            m_firstVertex.set(centerX - halfWidth, centerY - halfHeight);
            m_secondVertex.set(centerX - halfWidth, centerY + halfHeight);
            m_thirdVertex.set(centerX + halfWidth, centerY);

            m_rotateAngleInDegrees = rotateAngleInDegrees % 360;
            m_CenterVertex.set(centerX, centerY);
        }
    }

    //endregion

    //region Public Methods

    public void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) {
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        float layoutWidth = maxX - minX;
        float layoutHeight = maxY - minY;

        float scale = s_BoundsCoefficient* Math.min(canvasHeight / layoutHeight, canvasWidth / layoutWidth);

        float firstVertexX, firstVertexY, secondVertexX, secondVertexY, thirdVertexX, thirdVertexY, centerX, centerY;
        int rotateAngle;

        synchronized (MapTriangle.class) {
            firstVertexX = m_firstVertex.x;
            firstVertexY = m_firstVertex.y;
            secondVertexX = m_secondVertex.x;
            secondVertexY = m_secondVertex.y;
            thirdVertexX = m_thirdVertex.x;
            thirdVertexY = m_thirdVertex.y;
            rotateAngle = m_rotateAngleInDegrees;
            if (m_CenterVertex != null) {
                centerX = m_CenterVertex.x;
                centerY = m_CenterVertex.y;
            } else {
                centerX = centerY = 0;
            }
        }


        firstVertexX = scale * (firstVertexX - minX);
        firstVertexY = scale * (firstVertexY - minY);
        secondVertexX = scale * (secondVertexX - minX);
        secondVertexY = scale * (secondVertexY - minY);
        thirdVertexX = scale * (thirdVertexX - minX);
        thirdVertexY = scale * (thirdVertexY - minY);
        centerX = scale * (centerX - minX);
        centerY = scale * (centerY - minY);


        //for landscape:
        firstVertexY = canvasHeight - firstVertexY;
        secondVertexY = canvasHeight - secondVertexY;
        thirdVertexY = canvasHeight - thirdVertexY;
        centerY = canvasHeight - centerY;

        m_path.setFillType(Path.FillType.EVEN_ODD);
        m_path.moveTo(firstVertexX, firstVertexY);
        m_path.lineTo(secondVertexX, secondVertexY);
        m_path.lineTo(thirdVertexX, thirdVertexY);
        m_path.close();

        if (rotateAngle != 0) {
            m_rotateMatrix.setRotate(-rotateAngle, centerX, centerY);
            m_path.transform(m_rotateMatrix);
            m_rotateMatrix.reset();
        }
        canvas.drawPath(m_path, m_Paint);

        m_path.rewind();
    }

    //endregion
}
