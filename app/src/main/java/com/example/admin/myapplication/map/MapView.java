/*
 * Created by admin on 04/12/2017
 * Last modified 18:58 04/12/17
 */

package com.example.admin.myapplication.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.admin.myapplication.notification.NotificationManager;

import java.util.Timer;
import java.util.TimerTask;

import businessLogic.common.interfaces.IDisposable;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map</P>
 * <P>A map view that use {@link DrawableMap} instance to collect map elements and rendering them.</P>
 */
public class MapView extends View implements IDisposable {

    //region Constants

    public static final float DEFAULT_ZOOM = 1.0f;

    public static final int BACKGROUND_COLOR = Color.DKGRAY;

    public static final int BORDER_COLOR = Color.WHITE;

    public static final int BORDER_STROKE_WIDTH = 5;

    //endregion

    //region Fields

    private Paint m_borderPaint, m_backgroundPaint;

    private int m_width, m_height;

    private long m_currentTimerPeriod;

    private Timer m_timer;

    private TimerTask m_timerTask;

    private volatile DrawableMap m_drawableMap;

    private volatile float m_zoom;

    private float m_touchX, m_touchY, m_translateX, m_translateY;

    private boolean m_cursorTracing;

    //endregion

    //region Getters & Setters

    /**
     * @return The current rendering period, returns -1 if the rendering stopped.
     */
    public long getCurrentRenderingPeriod() {
        return m_currentTimerPeriod;
    }

    /**
     * @return instance of a {@link DrawableMap} type the related to current {@link MapView}.
     */
    public DrawableMap getDrawableMap() {
        return m_drawableMap;
    }

    /**
     * @return The current zoom scale (1.0 is regular scale).
     */
    public float getZoom() {
        return m_zoom;
    }

    /**
     * Setter to the zoom of the map.
     *
     * @param zoom Float range from 0.1
     */
    public void setZoom(@FloatRange(from = 0.1) float zoom) {
        if (zoom == m_zoom) {
            return;
        }
        if (zoom < 0.1f) {
            zoom = 0.1f;
        }

        m_zoom = zoom;

        if (m_zoom == DEFAULT_ZOOM) {
            m_translateX = m_translateY = Float.NaN;
            m_cursorTracing = false;
        } else if (!(Float.isNaN(m_translateX) || Float.isNaN(m_translateY))) {
            float diffZoom = Math.abs(m_zoom - zoom);
            m_translateX -= m_translateX * diffZoom;
            m_translateY -= m_translateY * diffZoom;
        }
        postInvalidate();
    }

    public boolean isCursorTracing() {
        return m_cursorTracing;
    }

    public void setCursorTracing(boolean cursorTracing) {
        m_cursorTracing = cursorTracing;
        if (m_cursorTracing) {
            m_drawableMap.setDrawableMapChanged();
        }
    }
    //endregion

    //region Constructors

    public MapView(Context context) {
        super(context);

        initialize();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    private void initialize() {
        m_drawableMap = new DrawableMap();

        m_timer = new Timer();
        m_currentTimerPeriod = -1;
        m_zoom = DEFAULT_ZOOM;
        m_translateX = m_translateY = Float.NaN;
        m_cursorTracing = false;
        //Initialize view elements:

        m_borderPaint = new Paint();
        m_borderPaint.setAntiAlias(true);
        m_borderPaint.setColor(BORDER_COLOR);
        m_borderPaint.setStyle(Paint.Style.STROKE);
        m_borderPaint.setStrokeWidth(BORDER_STROKE_WIDTH);

        //noinspection ConstantConditions
        if (BACKGROUND_COLOR != Color.TRANSPARENT) {
            m_backgroundPaint = new Paint();
            m_backgroundPaint.setAntiAlias(true);
            m_backgroundPaint.setColor(BACKGROUND_COLOR);
            m_backgroundPaint.setStyle(Paint.Style.FILL);
        } else {
            m_backgroundPaint = null;
        }
    }

    //endregion

    //region Methods

    //region Override View Methods

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        m_width = width;
        m_height = height;
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        if (m_backgroundPaint != null) {
            canvas.drawRect(0, 0, m_width, m_height, m_backgroundPaint);
        }

        if (m_zoom == DEFAULT_ZOOM) {
            m_drawableMap.drawMap(canvas);
        } else {
            canvas.save();
            if (m_cursorTracing) {
                PointF cursorLocation = m_drawableMap.getCursorLocation();
                if (cursorLocation != null) {

                    float maxTranslateX = m_width * (m_zoom - 1);
                    float pointX = m_drawableMap.transformXCoordinate(m_width, cursorLocation.x);
                    if (pointX > maxTranslateX) {
                        m_translateX = maxTranslateX;
                    } else if (m_translateX < 0) {
                        m_translateX = 0;
                    } else {
                        m_translateX = pointX;
                    }

                    float maxTranslateY = m_height * (m_zoom - 1);
                    float pointY = m_drawableMap.transformYCoordinate(m_height, cursorLocation.y, true);
                    if (pointY > maxTranslateY) {
                        m_translateY = maxTranslateY;
                    } else if (m_translateY < 0) {
                        m_translateY = 0;
                    } else {
                        m_translateY = pointY;
                    }
                }
            }
            if (!(Float.isNaN(m_translateX) || Float.isNaN(m_translateY))) {
                canvas.translate(-m_translateX, -m_translateY);
            }
            canvas.scale(m_zoom, m_zoom);
            m_drawableMap.drawMap(canvas);
            canvas.restore();
        }

        //draw border
        canvas.drawRect(0, 0, m_width, m_height, m_borderPaint);
        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (m_zoom == DEFAULT_ZOOM) {
            return true;
        }
        if (m_cursorTracing) {
            NotificationManager.showShortToast(getContext(), "Disable cursor tracing to panning..");
            return true;
        }
        float currentTouchX = ev.getX();
        float currentTouchY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (Float.isNaN(m_translateX) && Float.isNaN(m_translateY)) {
                    m_translateX = 0;
                    m_translateY = 0;
                }
                m_touchX = currentTouchX;
                m_touchY = currentTouchY;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                m_translateX -= ((currentTouchX - m_touchX) / m_zoom);
                m_translateY -= ((currentTouchY - m_touchY) / m_zoom);

                float maxTranslateX = m_width * m_zoom - m_width;
                if (m_translateX > maxTranslateX) {
                    m_translateX = maxTranslateX;
                } else if (m_translateX < 0) {
                    m_translateX = 0;
                }

                float maxTranslateY = m_height * m_zoom - m_height;

                if (m_translateY > maxTranslateY) {
                    m_translateY = maxTranslateY;
                } else if (m_translateY < 0) {
                    m_translateY = 0;
                }

                m_touchX = currentTouchX;
                m_touchY = currentTouchY;

                invalidate();
                break;
            }
        }
        return true;
    }

    //endregion

    //region Public Methods

    /**
     * Start the {@link MapView} rendering in fixed period.
     *
     * @param periodInMilliseconds The rendering period.
     * @return true if the rendering started, and false if not.
     */

    public boolean startRenderingMap(@IntRange(from = 1) long periodInMilliseconds) {
        if (periodInMilliseconds < 1) {
            return false;
        }

        if (m_timerTask != null) {
            stopRenderingMap();
        }

        m_currentTimerPeriod = periodInMilliseconds;
        m_timerTask = new MapViewRenderingTask();
        m_timer.schedule(m_timerTask, 0, m_currentTimerPeriod);
        return true;
    }

    /**
     * Stop the {@link MapView} rendering, if the rendering already stopped nothing happened.
     */
    public void stopRenderingMap() {
        if (m_timerTask == null) {
            return;
        }
        m_timerTask.cancel();
        m_timer.purge();
        m_currentTimerPeriod = -1;
    }

    //endregion

    //region IDisposable Implementation

    @Override
    public void dispose() {
        stopRenderingMap();
        m_timer.cancel();

        m_drawableMap.purgeMap();
        m_drawableMap = null;

        m_borderPaint = m_backgroundPaint = null;

    }

//endregion

//endregion

//region Nested Classes

    private class MapViewRenderingTask extends TimerTask {

        @Override
        public void run() {
            if (m_drawableMap != null && m_drawableMap.isChangedFromLastDraw()) {
                postInvalidate();
            }
        }
    }
//endregion
}
