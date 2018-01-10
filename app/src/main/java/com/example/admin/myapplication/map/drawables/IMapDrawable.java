/*
 * Created by admin on 27/11/2017
 * Last modified 10:54 27/11/17
 */

package com.example.admin.myapplication.map.drawables;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.drawables.</P>
 * <P>An interface which describes map elements that can be drawn on {@link com.example.admin.myapplication.map.MapView}. </P></P>
 */

public interface IMapDrawable {

    /**
     * @return An unique id of the drawable.
     */
    long getId();

    /**
     * A method that draw the drawable map element on canvas.
     *
     * @param canvas The canvas whose element is painted
     */
    void drawOnCanvas(@NonNull Canvas canvas) ;

    /**
     * A method that draw the drawable map element on canvas, and can adjust the size of the element
     * to a rectangle of a different size than the canvas.
     *
     * @param canvas The canvas whose element is painted
     * @param minX The X of the bottom side of the rectangle.
     * @param minY The Y of the left side of the rectangle.
     * @param maxX The X of the top side of the rectangle.
     * @param maxY The Y of the right side of the rectangle.
     */
    void drawOnCanvas(@NonNull Canvas canvas, float minX, float minY, float maxX, float maxY);
}
