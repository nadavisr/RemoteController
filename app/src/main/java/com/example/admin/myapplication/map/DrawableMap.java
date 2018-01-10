/*
 * Created by admin on 19/11/2017
 * Last modified 15:24 19/11/17
 */

package com.example.admin.myapplication.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.ColorInt;

import com.example.admin.myapplication.map.drawables.IMapDrawable;
import com.example.admin.myapplication.map.drawables.MapArc;
import com.example.admin.myapplication.map.drawables.MapCircle;
import com.example.admin.myapplication.map.drawables.MapCursor;
import com.example.admin.myapplication.map.drawables.MapLine;
import com.example.admin.myapplication.map.drawables.MapTriangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.</P>
 * <P>DrawableMap elements that collect and manage {@link IMapDrawable}, and can draw them on canvas.</P>
 */
public class DrawableMap {

    //region Constants

    private final static int CURSOR_LENGTH = 10;

    private final static int CURSOR_WIDTH = 30;

    //endregion

    //region Fields

    private ILog m_logger;

    private List<IMapDrawable> m_drawables;

    private MapCursor m_cursor;

    private Lock m_drawablesLock;

    private volatile float m_minX, m_minY, m_maxX, m_maxY;

    private ExecutorService m_executorService;

    private volatile boolean m_changedFromLastDraw;

    //endregion

    //region Getters & Setters

    /**
     * @return true if the mao changed from the last draw.
     */
    public boolean isChangedFromLastDraw() {
        return m_changedFromLastDraw;
    }

    /**
     * @return The center of the cursor if it exist, and <code>null</code> if does not.
     */
    public PointF getCursorLocation() {
        if (m_cursor == null) {
            return null;
        }
        PointF centerVertex = m_cursor.getCenterVertex();
        return new PointF(centerVertex.x, centerVertex.y);
    }

    void setDrawableMapChanged() {
        m_changedFromLastDraw = true;
    }
    //endregion

    //region Constructors

    public DrawableMap() {
        m_minX = m_minY = m_maxX = m_maxY = Float.NaN;

        m_drawables = new ArrayList<>();

        m_drawablesLock = new ReentrantLock();

        m_logger = LogManager.getLogger();

        m_executorService = Executors.newSingleThreadExecutor();

        m_changedFromLastDraw = false;

    }

    /**
     * @param drawables Collection of {@link IMapDrawable}.
     * @throws NullPointerException if the collection is null or empty.
     */
    public DrawableMap(Collection<IMapDrawable> drawables) throws NullPointerException {
        this();

        if (drawables == null || drawables.isEmpty()) {
            throw new NullPointerException("IMapDrawable collection is null or empty!");
        }

        m_drawables.addAll(drawables);

        calculateMinMax();
    }

    //endregion

    //region Private Methods

    private void updateMapLimitsByPoint(PointF pointF) {
        if (Float.isNaN(m_minX) || Float.isNaN(m_maxX)) {
            m_minX = m_maxX = pointF.x;
        } else if (pointF.x <= m_minX) {
            m_minX = pointF.x;
        } else if (pointF.x >= m_maxX) {
            m_maxX = pointF.x;
        }

        if (Float.isNaN(m_minY) || Float.isNaN(m_maxY)) {
            m_minY = m_maxY = pointF.y;
        } else if (pointF.y <= m_minY) {
            m_minY = pointF.y;
        } else if (pointF.y >= m_maxY) {
            m_maxY = pointF.y;
        }
    }

    private void updateMapLimitsByLine(MapLine mapLine) {

        PointF first = mapLine.getFirstPoint();
        updateMapLimitsByPoint(first);

        PointF second = mapLine.getSecondPoint();
        updateMapLimitsByPoint(second);
    }

    private void updateMapLimitsByArc(MapArc mapArc) {

        float a = Math.abs((mapArc.getRight() - mapArc.getLeft()) / 2.0f);
        float b = Math.abs((mapArc.getTop() - mapArc.getBottom()) / 2.0f);

        float x0 = mapArc.getRight() - a;
        float y0 = mapArc.getTop() - b;


        PointF point1 = new PointF(x0, y0 + b);
        PointF point2 = new PointF(x0, y0 - b);
        PointF point3 = new PointF(x0 + a, y0);
        PointF point4 = new PointF(x0 - a, y0);

        if (mapArc.getRotateAngle() != 0) {
            double rotateAngleInRadians = Math.toRadians(mapArc.getRotateAngle());

            double cos = Math.cos(rotateAngleInRadians);
            double sin = Math.sin(rotateAngleInRadians);

            double xRotation = cos - sin;
            double yRotation = sin + cos;

            point1.x *= xRotation;
            point1.y *= yRotation;

            point2.x *= xRotation;
            point2.y *= yRotation;

            point3.x *= xRotation;
            point3.y *= yRotation;

            point4.x *= xRotation;
            point4.y *= yRotation;
        }

        updateMapLimitsByPoint(point1);
        updateMapLimitsByPoint(point2);
        updateMapLimitsByPoint(point3);
        updateMapLimitsByPoint(point4);
    }

    private void updateMapLimitsByTriangle(MapTriangle mapArc) {
        PointF firstVertex = mapArc.getFirstVertex();
        updateMapLimitsByPoint(firstVertex);

        PointF secondVertex = mapArc.getSecondVertex();
        updateMapLimitsByPoint(secondVertex);

        PointF thirdVertex = mapArc.getThirdVertex();
        updateMapLimitsByPoint(thirdVertex);
    }

    private void calculateMinMax() {
        m_executorService.execute(() ->
        {
            m_minX = m_minY = m_maxX = m_maxY = Float.NaN;
            m_drawablesLock.lock();
            try {
                for (IMapDrawable drawable : m_drawables) {
                    if (drawable instanceof MapArc) {
                        MapArc mapArc = (MapArc) drawable;
                        updateMapLimitsByArc(mapArc);
                    } else if (drawable instanceof MapLine) {
                        MapLine mapLine = (MapLine) drawable;
                        updateMapLimitsByLine(mapLine);
                    } else if (drawable instanceof MapTriangle) {
                        MapTriangle mapTriangle = (MapTriangle) drawable;
                        updateMapLimitsByTriangle(mapTriangle);
                    } else {
                        m_logger.warning("Unsupported type: " + drawable.getClass().getName());
                    }
                }
                if (m_cursor != null) {
                    updateMapLimitsByTriangle(m_cursor);
                }
            } catch (Exception ex) {
                m_logger.warning("Attempt to update the states of the MapLine failed!", ex);
            } finally {
                m_drawablesLock.unlock();
                m_changedFromLastDraw = true;
            }
        });
    }

    //endregion

    //region Public Methods

    /**
     * Add collection of {@link IMapDrawable} to the map.
     *
     * @param mapDrawables The collection of {@link IMapDrawable}.
     * @return A collection of {@link IMapDrawable} that does not added.
     * @throws NullPointerException if the collection is null.
     */
    public Collection<IMapDrawable> addDrawables(Collection<IMapDrawable> mapDrawables) throws NullPointerException {
        if (mapDrawables == null) {
            throw new NullPointerException("IMapDrawable collection is null!");
        }

        Collection<IMapDrawable> notAddedCollection = new ArrayList<>();
        for (IMapDrawable mapDrawable : mapDrawables) {
            if (!this.addDrawable(mapDrawable)) {
                notAddedCollection.add(mapDrawable);
            }
        }
        return notAddedCollection.isEmpty() ? null : notAddedCollection;
    }

    /**
     * Add a {@link IMapDrawable} to the map.
     *
     * @param mapDrawable an instance of {@link IMapDrawable}.
     * @return true if the element added and false if did not.
     * @throws NullPointerException if the instance is null.
     */
    public boolean addDrawable(IMapDrawable mapDrawable) throws NullPointerException {

        if (mapDrawable == null) {
            throw new NullPointerException("IMapDrawable is null!");
        }

        if (mapDrawable instanceof MapCursor) {
            MapCursor mapCursor = (MapCursor) mapDrawable;
            PointF centerVertex = mapCursor.getCenterVertex();
            setCursor(centerVertex.x, centerVertex.y, mapCursor.getRotateAngleInDegrees());
            return true;
        }

        if (!(mapDrawable instanceof MapArc)
                && !(mapDrawable instanceof MapLine)
                && !(mapDrawable instanceof MapTriangle)) {
            m_logger.warning("Unsupported type: " + mapDrawable.getClass().getName());
            return false;
        }


        m_drawablesLock.lock();
        boolean succeed;
        try {
            int existDrawableIndex = Integer.MIN_VALUE;
            for (int i = 0; i < m_drawables.size(); i++) {
                IMapDrawable drawable = m_drawables.get(i);
                if (drawable.getId() == mapDrawable.getId()) {
                    existDrawableIndex = i;
                    break;
                }
            }

            if (existDrawableIndex != Integer.MIN_VALUE) {
                m_drawables.remove(existDrawableIndex);
            }
            succeed = m_drawables.add(mapDrawable);

        } catch (Exception ex) {
            m_logger.warning("Attempt to add MapLine to DrawableMap failed!", ex);
            succeed = false;
        } finally {
            m_drawablesLock.unlock();
        }

        m_changedFromLastDraw |= succeed;

        if (mapDrawable instanceof MapArc) {
            MapArc mapArc = (MapArc) mapDrawable;
            updateMapLimitsByArc(mapArc);

        } else if (mapDrawable instanceof MapLine) {
            MapLine mapLine = (MapLine) mapDrawable;
            updateMapLimitsByLine(mapLine);
        } else {
            MapTriangle mapTriangle = (MapTriangle) mapDrawable;
            updateMapLimitsByTriangle(mapTriangle);
        }
        return succeed;
    }

    /**
     * Remove a {@link IMapDrawable} from the map.
     *
     * @param mapDrawable the instance to remove
     * @return true if removed and false if did not, return false also if the instance did not contained in the map.
     * @throws NullPointerException if the instance is null.
     */
    public boolean removeDrawable(IMapDrawable mapDrawable) throws NullPointerException {
        if (mapDrawable == null) {
            throw new NullPointerException("IMapDrawable is null!");
        }

        m_drawablesLock.lock();
        boolean succeed = false;
        try {
            if (m_drawables.contains(mapDrawable)) {
                succeed = m_drawables.remove(mapDrawable);
            }
        } catch (Exception ex) {
            m_logger.warning("Attempt to add MapLine to DrawableMap failed!", ex);
            succeed = false;
        } finally {
            m_drawablesLock.unlock();
        }

        calculateMinMax();

        m_changedFromLastDraw |= succeed;

        return succeed;

    }

    /**
     * Remove a {@link IMapDrawable} from the map by {@link IMapDrawable} ID.
     *
     * @param mapDrawableId the ID of the {@link IMapDrawable}.
     * @return true if removed and false if did not, return false also if the id did not contained in the map.
     */
    public IMapDrawable removeDrawable(long mapDrawableId) {

        IMapDrawable mapDrawable = null;

        m_drawablesLock.lock();

        try {
            for (IMapDrawable drawable : m_drawables) {
                if (drawable.getId() == mapDrawableId) {
                    mapDrawable = drawable;
                }
            }
        } catch (Exception ex) {
            m_logger.warning("Attempt to add MapLine to DrawableMap failed!", ex);
        } finally {
            m_drawablesLock.unlock();
        }

        if (mapDrawable != null) {
            mapDrawable = this.removeDrawable(mapDrawable) ? mapDrawable : null;
            calculateMinMax();
        }

        return mapDrawable;
    }

    /**
     * Clean the map from the {@link IMapDrawable}.
     *
     * @return A collection of removed elements.
     */
    public Collection<IMapDrawable> purgeMap() {
        m_minX = m_minY = m_maxX = m_maxY = Float.NaN;

        List<IMapDrawable> mapDrawables = new ArrayList<>(m_drawables.size());
        m_drawablesLock.lock();

        try {
            mapDrawables.addAll(m_drawables);
            m_drawables.clear();
        } catch (Exception ex) {
            m_logger.warning("Attempt to add MapLine to DrawableMap failed!", ex);
        } finally {
            m_drawablesLock.unlock();
        }

        m_changedFromLastDraw |= !mapDrawables.isEmpty();

        return mapDrawables;
    }

    /**
     * Draw all the contained {@link IMapDrawable} on canvas.
     *
     * @param canvas The canvas to draw on it.
     * @throws NullPointerException if the received canvas is null.
     */
    public void drawMap(Canvas canvas) throws NullPointerException {
        if (canvas == null) {
            throw new NullPointerException("Canvas is null!");
        }

        m_drawablesLock.lock();

        try {
            for (int i = m_drawables.size() - 1; i >= 0; i--) {
                IMapDrawable drawable = m_drawables.get(i);
                drawable.drawOnCanvas(canvas, m_minX, m_minY, m_maxX, m_maxY);
            }
            m_changedFromLastDraw = false;
        } catch (Exception ex) {
            m_logger.warning("Attempt to add MapLine to DrawableMap failed!", ex);
        } finally {
            m_drawablesLock.unlock();
        }

        if (m_cursor != null) {
            m_cursor.drawOnCanvas(canvas, m_minX, m_minY, m_maxX, m_maxY);
        }
    }

    /**
     * Set map cursor (triangle shape) location.
     *
     * @param x              x value in Cartesian axis.
     * @param y              y value in Cartesian axis.
     * @param angleInDegrees the direction of the cursor in degrees.
     */
    public void setCursor(float x, float y, int angleInDegrees) {
        if (m_cursor == null) {
            m_cursor = new MapCursor(new PointF(x, y), CURSOR_WIDTH, CURSOR_LENGTH, angleInDegrees, Color.BLUE);
        } else {
            m_cursor.setVertexes(x, y, CURSOR_WIDTH, CURSOR_LENGTH, angleInDegrees);
        }
        updateMapLimitsByTriangle(m_cursor);
        m_changedFromLastDraw = true;

    }

    /**
     * Clear the cursor from the map.
     */
    public void clearCursor() {
        m_cursor = null;
        calculateMinMax();
    }

    /**
     * The method add an origin vertex.
     *
     * @param color The color of the origin.
     */
    public void addOrigin(@ColorInt int color) {
        addDrawable(new MapCircle(-3, new PointF(0, 0), 5, color));
        addDrawable(new MapCircle(-2, new PointF(0, 0), 10, color));

    }

    /**
     * The method remove the origin vertex.
     */
    public void removeOrigin() {
        removeDrawable(-2);
        removeDrawable(-3);
    }

    /**
     * The method can transform x,y coordinates from map axis to canvas axis,
     * for one coordinate transformation possible to use the methods:
     * {@link DrawableMap#transformXCoordinate(float, float)} or {@link DrawableMap#transformYCoordinate(float, float, boolean)}
     *
     * @param canvasWidth  Canvas width
     * @param canvasHeight Canvas height
     * @param x            X coordinate of the point for transformation
     * @param y            Y coordinate of the point for transformation
     * @param portrait     True if the screen in portrait mode and false for landscape
     * @return The point in canvas axis.
     */
    protected PointF transformPoint(float canvasWidth, float canvasHeight, float x, float y, boolean portrait) {

        float transformedX = transformXCoordinate(canvasWidth, x);
        float transformedY = transformYCoordinate(canvasHeight, y, portrait);

        return new PointF(transformedX, transformedY);
    }

    /**
     * The method can transform Y coordinate from map axis to canvas axis.
     *
     * @param canvasHeight Canvas height
     * @param y            Y coordinate of the point for transformation
     * @param portrait     True if the screen in portrait mode and false for landscape
     * @return The point in canvas axis.
     */
    protected float transformYCoordinate(float canvasHeight, float y, boolean portrait) {
        float y1 = (canvasHeight / (m_maxY - m_minY)) * (y - m_minY);

        if (!portrait) {
            y1 = canvasHeight - y1;
        }

        return y1;
    }

    /**
     * The method can transform X coordinate from map axis to canvas axis.
     *
     * @param canvasWidth Canvas width
     * @param x           X coordinate of the point for transformation
     * @return The point in canvas axis.
     */
    protected float transformXCoordinate(float canvasWidth, float x) {
        return (canvasWidth / (m_maxX - m_minX)) * (x - m_minX);
    }
    //endregion
}