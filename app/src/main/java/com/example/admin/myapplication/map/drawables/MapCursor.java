/*
 * Created by admin on 04/12/2017
 * Last modified 09:27 04/12/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>A class which describes triangular map element that can be drawn on {@link com.example.admin.myapplication.map.MapView},
 * and can be updated.</P>
 */
public class MapCursor extends MapTriangle {

    //region Fields

    private static int CURSOR_ID = -1;

    private List<PointF> m_cursorPath;

    private boolean m_drawPath;

    private Path m_path;

    private Paint m_paint;

    //endregion

    //region Constructors

    /**
     * @param center               The center of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @param color                A color described by integer.
     * @param strokeWidth          The width of the stroke (greater then 0.1).
     * @throws NullPointerException if one of the points are null.
     */
    public MapCursor(PointF center, float width, float height,
                     @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color,
                     @FloatRange(from = 0.1) float strokeWidth) throws NullPointerException {
        super(CURSOR_ID, center, width, height, rotateAngleInDegrees, color, strokeWidth);
        init();
    }

    /**
     * Create MapTriangle instance with default stroke width (= {@value DEFAULT_STROKE_WIDTH}).
     *
     * @param center               The center of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @param color                A color described by integer.
     * @throws NullPointerException if one of the points are null.
     */
    public MapCursor(PointF center, float width, float height,
                     @IntRange(from = 0, to = 360) int rotateAngleInDegrees, @ColorInt int color) throws NullPointerException {
        super(CURSOR_ID, center, width, height, rotateAngleInDegrees, color);
        init();
    }

    /**
     * Create MapTriangle instance with default stroke width ( = {@value DEFAULT_STROKE_WIDTH} )
     * and default color ( = {@value DEFAULT_COLOR} ).
     *
     * @param center               The center of the triangle.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     * @throws NullPointerException if one of the points are null.
     */
    public MapCursor(PointF center, float width, float height,
                     @IntRange(from = 0, to = 360) int rotateAngleInDegrees) throws NullPointerException {
        super(CURSOR_ID, center, width, height, rotateAngleInDegrees);
        init();
    }


    private void init() {
        m_cursorPath = new ArrayList<>();
        m_drawPath = true;
        m_path = new Path();
        m_paint = new Paint();
        m_paint.setStrokeWidth(3);
        m_paint.setPathEffect(null);
        m_paint.setColor(Color.YELLOW);
        m_paint.setStyle(Paint.Style.STROKE);
    }

    //endregion

    //region Getters & Setters

    /**
     * @return The center point of the cursor.
     */
    public PointF getCenterVertex() {
        return m_CenterVertex;
    }

    /**
     * @param drawPath true to draw the path of the cursor.
     */
    public void setDrawPath(boolean drawPath) {
        m_drawPath = drawPath;
    }

    //endregion

    //region Methods

    /**
     * @param centerX              The x point of the center vertex of the triangle in Cartesian axis.
     * @param centerY              The y point of the center vertex of the triangle in Cartesian axis.
     * @param width                The width of the triangle
     * @param height               The height of the triangle
     * @param rotateAngleInDegrees The rotate angle of the triangle that relative to origins.
     */
    public void setVertexes(float centerX, float centerY, float width, float height,
                            @IntRange(from = 0, to = 360) int rotateAngleInDegrees) {
        m_cursorPath.add(new PointF(centerX, centerY));
        super.setVertexes(centerX, centerY, width, height, rotateAngleInDegrees);
    }


    @Override
    public void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) {
        if (m_drawPath) {
            drawPath(canvas, minX, minY, maxX, maxY);
        }
        super.drawOnCanvas(canvas, minX, minY, maxX, maxY);
    }

    private void drawPath(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY) {
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        float layoutWidth = maxX - minX;
        float layoutHeight = maxY - minY;

        float scale = s_BoundsCoefficient * Math.min(canvasHeight / layoutHeight, canvasWidth / layoutWidth);

        float pointX, pointY;

        for (int i = 0; i < m_cursorPath.size(); i = i + 5) {
            PointF pointF = m_cursorPath.get(i);

            pointX = scale * (pointF.x - minX);
            pointY = scale * (pointF.y - minY);

            //for landscape:
            pointY = canvasHeight - pointY;

            if (i % 2 == 0) {
                m_path.moveTo(pointX, pointY);
            } else {
                m_path.lineTo(pointX, pointY);
            }
        }

        canvas.drawPath(m_path, m_paint);

        m_path.rewind();
    }

    //endregion
}
