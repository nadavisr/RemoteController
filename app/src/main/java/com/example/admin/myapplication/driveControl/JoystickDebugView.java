/*
 * Created by admin on 26/10/2017
 * Last modified 10:55 26/10/17
 */

/*
 * Created by admin on 25/10/2017
 * Last modified 23:59 24/09/15
 */

package com.example.admin.myapplication.driveControl;

import android.animation.TimeAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * A simple toy view for demo purposes.
 * Displays a circle and a trail that can be controlled via {@link #setVelocity(float, float)}.
 */
public class JoystickDebugView extends View implements TimeAnimator.TimeListener {

    private static final int BUG_RADIUS = 8;
    private static final float BUG_TRAIL_PERCENT = 0.2f;
    private static final int BACKGROUND_COLOR = Color.DKGRAY;
    private static final int CURSOR_COLOR = Color.WHITE;
    private static final int BORDER_COLOR = Color.WHITE;
    private static final int BORDER_STROKE_WIDTH = 5;
    private static final int MARGIN = BORDER_STROKE_WIDTH + BUG_RADIUS;

    private Paint m_cursorPaint;

    private TimeAnimator m_animator;

    private float m_density, m_trail;

    private int m_width, m_height;

    private PointF m_positionPoint;

    private PointF m_velocity;

    private Path m_path;

    private PathMeasure m_pathMeasure;

    private Paint m_borderPaint;

    private Paint m_backgroundPaint;

    public JoystickDebugView(Context context) {
        super(context);
        init();
    }

    public JoystickDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JoystickDebugView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        m_animator = new TimeAnimator();
        m_animator.setTimeListener(this);

        m_cursorPaint = new Paint();
        m_cursorPaint.setColor(CURSOR_COLOR);

        m_density = getResources().getDisplayMetrics().density;

        m_path = new Path();
        m_pathMeasure = new PathMeasure();
        m_positionPoint = new PointF();
        m_velocity = new PointF();

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

    /**
     * Start applying m_velocity as soon as view is on-screen.
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        m_animator.start();
    }

    /**
     * Stop animations when the view hierarchy is torn down.
     */
    @Override
    public void onDetachedFromWindow() {
        m_animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        m_width = width;
        m_height = height;
        m_trail = Math.min(m_width, m_height) * BUG_TRAIL_PERCENT;
        m_positionPoint.set(m_width / 2, m_height / 2);
        m_path.rewind();
        m_path.moveTo(m_positionPoint.x, m_positionPoint.y);
    }

    /**
     * Set bug m_velocity in dips.
     */
    public void setVelocity(float velocityXDps, float velocityYDps) {
        m_velocity.set(velocityXDps * m_density, -velocityYDps * m_density);
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        final float dt = deltaTime / 1000.0f; // seconds

        m_positionPoint.x += m_velocity.x * dt;
        m_positionPoint.y += m_velocity.y * dt;

        bound();

        m_path.lineTo(m_positionPoint.x, m_positionPoint.y);

        invalidate();
    }


    /**
     * Bound m_positionPoint.
     */
    private void bound() {
        if (m_positionPoint.x > m_width - MARGIN) {
            m_positionPoint.x = m_width - MARGIN;
        } else if (m_positionPoint.x < 3 * MARGIN) {
            m_positionPoint.x = 3 * MARGIN;
        }
        if (m_positionPoint.y > m_height - MARGIN) {
            m_positionPoint.y = m_height - MARGIN;
        } else if (m_positionPoint.y < 3 * MARGIN) {
            m_positionPoint.y = 3 * MARGIN;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (m_backgroundPaint != null) {
            canvas.drawRect(0, 0, m_width , m_height, m_backgroundPaint);
        }

        canvas.drawRect(0, 0, m_width , m_height , m_borderPaint);

        m_pathMeasure.setPath(m_path, false);
        float length = m_pathMeasure.getLength();

        if (length > m_trail * m_density) {
            // Note - this is likely a poor way to accomplish the result. Just for demo purposes.
            @SuppressLint("DrawAllocation")
            PathEffect effect = new DashPathEffect(new float[]{length, length}, -length + m_trail * m_density);
            m_cursorPaint.setPathEffect(effect);
        }

        m_cursorPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(m_path, m_cursorPaint);

        m_cursorPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(m_positionPoint.x, m_positionPoint.y, BUG_RADIUS * m_density, m_cursorPaint);
    }

    public void resetView() {
        m_trail = Math.min(m_width, m_height) * BUG_TRAIL_PERCENT;
        m_positionPoint.set(m_width / 2, m_height / 2);
        m_path.rewind();
        m_path.moveTo(m_positionPoint.x, m_positionPoint.y);
        invalidate();
    }
}
