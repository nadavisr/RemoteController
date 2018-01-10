/*
 * Created by admin on 24/12/2017
 * Last modified 16:24 24/12/17
 */

package com.example.admin.myapplication.driveControl.joysticks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.admin.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

import static com.example.admin.myapplication.driveControl.joysticks.JoystickView.DEATH_ZONE_RATIO;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.joysticks.</P>
 * <P></P>
 */

public class DoubleJoystickView extends View implements IJoystickView {

    //region Private Fields

    private ILog m_logger;
    private boolean m_enabled;
    private boolean m_angleAndPower;
    private Timer m_timer;

    private Paint m_joystickButtonPaint;
    private float m_joystickButtonRadius;

    private Drawable m_verticalJoystick;
    private float m_verticalJoystickCenterX;
    volatile private float m_verticalJoystickButtonLocation;
    volatile private float m_verticalPointerLastPlace;
    volatile private int m_verticalPointerId;
    private Rect m_verticalJoystickBounds;

    private Drawable m_horizontalJoystick;
    private float m_horizontalJoystickCenterY;
    volatile private float m_horizontalJoystickButtonLocation;
    volatile private float m_horizontalPointerLastPlace;
    volatile private int m_horizontalPointerId;
    private Rect m_horizontalJoystickBounds;
    private TimerTask m_eventTimerTask;

    private double m_joystickRadius;

    volatile private IOnMoveListener m_onMoveListener;

    private int m_eventFrequencyInMilliseconds;

    private double m_joystickHypotenuse;

    //endregion

    //region Constructors

    public DoubleJoystickView(Context context) {
        super(context);
        init();
    }

    public DoubleJoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoubleJoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DoubleJoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        m_logger = LogManager.getLogger();

        m_verticalJoystick = ResourcesCompat.getDrawable(getResources(), R.drawable.shape_cylinder2d_vertical, null);
        m_horizontalJoystick = ResourcesCompat.getDrawable(getResources(), R.drawable.shape_cylinder2d_horizontal, null);

        m_joystickButtonPaint = new Paint();
        m_joystickButtonPaint.setAntiAlias(true);

        m_joystickButtonPaint.setColor(Color.parseColor("#800000"));
        m_joystickButtonPaint.setAlpha(150);
        m_joystickButtonPaint.setStyle(Paint.Style.FILL);

        m_horizontalPointerId = m_verticalPointerId = -1;

        m_timer = new Timer();

        m_enabled = true;

        m_joystickRadius = m_joystickButtonRadius = m_verticalJoystickCenterX =
                m_verticalJoystickButtonLocation = m_verticalPointerLastPlace =
                        m_horizontalJoystickCenterY = m_horizontalJoystickButtonLocation =
                                m_horizontalPointerLastPlace = 0.f;

        m_eventFrequencyInMilliseconds = DEFAULT_EVENT_FREQUENCY_IN_MILLISECONDS;

        m_eventTimerTask = null;

    }

    //endregion

    //region Getters & Setters

    /**
     * Return the state of the joystick. False when the button don't move.
     *
     * @return the state of the joystick
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Enable or disable the joystick.
     *
     * @param enabled False mean the button won't move and onMove won't be called
     */
    public void setJoystickEnabled(boolean enabled) {
        m_enabled = enabled;
        if (!m_enabled) {
            m_verticalPointerId = m_horizontalPointerId = -1;
            m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.centerX();
            m_verticalJoystickButtonLocation = m_verticalJoystickBounds.centerY();
        }
    }
    //endregion

    //region IJoystickView Implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMoveListener(IOnMoveListener onMoveListener) {
        this.setOnMoveListener(onMoveListener, DEFAULT_EVENT_FREQUENCY_IN_MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMoveListener(IOnMoveListener onMoveListener,
                                  @IntRange(from = 1) int eventFrequencyInMilliseconds) {
        m_onMoveListener = onMoveListener;
        m_angleAndPower = onMoveListener instanceof IOnMoveListenerGetAnglePower;
        m_eventFrequencyInMilliseconds = eventFrequencyInMilliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] checkDeadZones(int x, int y) {
        double xInPercent = x / m_joystickRadius;

        if (Math.abs(xInPercent) < DEATH_ZONE_RATIO) {
            x = 0;
        }

        double yInPercent = y / m_joystickRadius;
        if (Math.abs(yInPercent) < DEATH_ZONE_RATIO) {
            y = 0;
        }

        return new int[]{x, y};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculatePower(int x, int y) {
        double size = 100 * Math.sqrt(y * y + x * x);
        if (x == 0.0f || y == 0.0f) {
            return (int) (size / m_joystickRadius);
        }
        return (int) (size / m_joystickHypotenuse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateAngleInDegrees(int x, int y) {
        int angle = (int) Math.toDegrees(Math.atan2(y, x));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateAngleInRadians(int x, int y) {
        int degree = this.calculateAngleInDegrees(x, y);
        double radians = Math.toRadians(degree);
        return (int) Math.round(radians);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (m_eventTimerTask != null) {
            m_eventTimerTask.cancel();
            m_eventTimerTask = null;
        }
        if (m_timer != null) {
            m_timer.cancel();
            m_timer = null;
        }
    }

    //endregion

    //region View Methods Override

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // if disabled we don't move the
//        if (!m_enabled) {
//            return true;
//        }
//        if (m_eventTimerTask == null) {
//            m_eventTimerTask = new EventTimerTask();
//            m_timer.schedule(m_eventTimerTask, 0, m_eventFrequencyInMilliseconds);
//        }
//
//        OnTouchAsyncTask asyncTask = new OnTouchAsyncTask();
//        asyncTask.execute(event);
//
//        this.performClick();
//
//        return true;
//    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!m_enabled) {
            return true;
        }
        if (m_eventTimerTask == null) {
            m_eventTimerTask = new EventTimerTask();
            m_timer.schedule(m_eventTimerTask, 0, m_eventFrequencyInMilliseconds);
        }

        boolean toInvalidate = true;
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:

                if (m_verticalPointerId != -1 && m_horizontalPointerId != -1) {
                    toInvalidate = false;
                    break;
                }
                PointF downPoint = new PointF();
                downPoint.x = event.getX(pointerIndex);
                downPoint.y = event.getY(pointerIndex);

                if (m_horizontalPointerId == -1 && m_horizontalJoystickBounds.contains(Math.round(downPoint.x), Math.round(downPoint.y))) {
                    m_horizontalPointerId = pointerId;
                    m_horizontalPointerLastPlace = downPoint.x;
                } else if (m_verticalPointerId == -1 && m_verticalJoystickBounds.contains(Math.round(downPoint.x), Math.round(downPoint.y))) {
                    m_verticalPointerId = pointerId;
                    m_verticalPointerLastPlace = downPoint.y;
                } else {
                    toInvalidate = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (m_horizontalPointerId != -1) {
                    float movedX;
                    try {
                        movedX = event.getX(m_horizontalPointerId);
                    } catch (Exception ignored){
                        toInvalidate = false;
                        break;
                    }
//                    float movedX = event.getX(m_horizontalPointerId);

                    m_horizontalJoystickButtonLocation += (movedX - m_horizontalPointerLastPlace);
                    m_horizontalPointerLastPlace = movedX;

                    if (m_horizontalJoystickButtonLocation > m_horizontalJoystickBounds.right - m_joystickButtonRadius) {
                        m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.right - m_joystickButtonRadius;
                    } else if (m_horizontalJoystickButtonLocation < m_horizontalJoystickBounds.left + m_joystickButtonRadius) {
                        m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.left + m_joystickButtonRadius;
                    }
                }
                if (m_verticalPointerId != -1) {
                    float movedY;
                    try {
                        movedY = event.getY(m_verticalPointerId);
                    } catch (Exception ignored){
                        toInvalidate = false;
                        break;
                    }
//                    float movedY = event.getY(m_verticalPointerId);

                    m_verticalJoystickButtonLocation += (movedY - m_verticalPointerLastPlace);
                    m_verticalPointerLastPlace = movedY;

                    if (m_verticalJoystickButtonLocation < m_verticalJoystickBounds.top + m_joystickButtonRadius) {
                        m_verticalJoystickButtonLocation = m_verticalJoystickBounds.top + m_joystickButtonRadius;
                    } else if (m_verticalJoystickButtonLocation > m_verticalJoystickBounds.bottom - m_joystickButtonRadius) {
                        m_verticalJoystickButtonLocation = m_verticalJoystickBounds.bottom - m_joystickButtonRadius;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if (pointerId == m_horizontalPointerId || pointerId == m_verticalPointerId) {
                    m_verticalPointerId = m_horizontalPointerId = -1;
                    m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.centerX();
                    m_verticalJoystickButtonLocation = m_verticalJoystickBounds.centerY();
                } else {
                    toInvalidate = false;
                }

//                if (m_horizontalPointerId == pointerId) {
//                    m_horizontalPointerId = -1;
//                    m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.centerX();
//                } else if (m_verticalPointerId == pointerId) {
//                    m_verticalPointerId = -1;
//                    m_verticalJoystickButtonLocation = m_verticalJoystickBounds.centerY();
//                }

                break;
            default:
                toInvalidate = false;
                break;
        }

        if (toInvalidate) {
            invalidate();
        }
        return true;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        m_horizontalJoystick.draw(canvas);

        m_verticalJoystick.draw(canvas);

        canvas.drawCircle(
                m_verticalJoystickCenterX,
                m_verticalJoystickButtonLocation,
                m_joystickButtonRadius,
                m_joystickButtonPaint
        );
        canvas.drawCircle(
                m_horizontalJoystickButtonLocation,
                m_horizontalJoystickCenterY,
                m_joystickButtonRadius,
                m_joystickButtonPaint
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        int width = getWidth() - (getPaddingStart() + getPaddingEnd());
        int height = getHeight() - (getPaddingTop() - getPaddingBottom());

        m_joystickButtonRadius = m_verticalJoystick.getMinimumWidth() / 2.0f;

        m_verticalJoystick.setBounds(width - m_verticalJoystick.getMinimumWidth(), height - m_verticalJoystick.getMinimumHeight(), width, height);
        m_verticalJoystickBounds = m_verticalJoystick.getBounds();
        m_verticalJoystickBounds.sort();
        m_verticalJoystickCenterX = m_verticalJoystickBounds.centerX();
        m_verticalJoystickButtonLocation = m_verticalJoystickBounds.centerY();


        m_horizontalJoystick.setBounds(0, height - m_horizontalJoystick.getMinimumHeight(), m_horizontalJoystick.getMinimumWidth(), height);
        m_horizontalJoystickBounds = m_horizontalJoystick.getBounds();
        m_horizontalJoystickBounds.sort();

        m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.centerX();
        m_horizontalJoystickCenterY = m_horizontalJoystickBounds.centerY();

        m_joystickRadius = Math.max(
                Math.abs(m_horizontalJoystickBounds.right - m_horizontalJoystickBounds.left),
                Math.abs(m_verticalJoystickBounds.top - m_verticalJoystickBounds.bottom)
        ) - 2 * m_joystickButtonRadius;

        m_joystickRadius /= 2.0;

        m_joystickHypotenuse = Math.sqrt(2) * m_joystickRadius;
    }

    //endregion

    //region Private Methods

    /**
     * Process the angle following the 360° counter-clock protractor rules.
     *
     * @return the angle of the button
     */

    private int getButtonAngle() {
        int buttonY = getButtonY();
        int buttonX = getButtonX();
        int angle = (int) Math.toDegrees(Math.atan2(buttonY, buttonX));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }

    private int getButtonX() {
//        return m_posX - m_centerX;
        return Math.round(m_horizontalJoystickButtonLocation - m_horizontalJoystickBounds.centerX());
    }


    private int getButtonY() {
//        return m_posX - m_centerX;

        return Math.round(m_verticalJoystickBounds.centerY() - m_verticalJoystickButtonLocation);
    }

    /**
     * Process the strength as a percentage of the distance between the center and the border.
     *
     * @return the strength of the button
     */
    private int getButtonPower() {

        int x = getButtonX();
        int y = getButtonY();

        double normalized = Math.sqrt(x * x + y * y) / m_joystickRadius;
        return (int) (100 * normalized);
    }

//endregion

//region Nested classes

    private class EventTimerTask extends TimerTask {
        @Override
        public void run() {
            if (m_onMoveListener == null) {
                return;
            }

            int firstArgument, secondArgument;
            if (m_angleAndPower) {
                secondArgument = getButtonPower();
                if (secondArgument > 100) {
                    secondArgument = 100;
                }
                if (secondArgument != 0) {
                    firstArgument = getButtonAngle();
                } else {
                    firstArgument = 0;
                }

            } else {
                firstArgument = getButtonX();
                secondArgument = getButtonY();
            }
            m_onMoveListener.onMove(firstArgument, secondArgument);

        }
    }

    //TODO : check for better option.
    private class OnTouchAsyncTask extends AsyncTask<MotionEvent, Void, Boolean> {

        protected Boolean doInBackground(MotionEvent... motionEvents) {
            if (motionEvents == null || motionEvents.length == 0) {
                return false;
            }

            boolean returnedValue = true;
            MotionEvent event = motionEvents[0];
            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();

            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);

            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:

                    if (m_verticalPointerId != -1 && m_horizontalPointerId != -1) {
                        break;
                    }
                    PointF downPoint = new PointF();
                    downPoint.x = event.getX(pointerIndex);
                    downPoint.y = event.getY(pointerIndex);

                    if (m_horizontalPointerId == -1 && m_horizontalJoystickBounds.contains(Math.round(downPoint.x), Math.round(downPoint.y))) {
                        m_horizontalPointerId = pointerId;
                        m_horizontalPointerLastPlace = downPoint.x;
                    } else if (m_verticalPointerId == -1 && m_verticalJoystickBounds.contains(Math.round(downPoint.x), Math.round(downPoint.y))) {
                        m_verticalPointerId = pointerId;
                        m_verticalPointerLastPlace = downPoint.y;
                    } else {
                        returnedValue = false;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (m_horizontalPointerId != -1) {
                        float movedX = event.getX(m_horizontalPointerId);

                        m_horizontalJoystickButtonLocation += (movedX - m_horizontalPointerLastPlace);
                        m_horizontalPointerLastPlace = movedX;

                        if (m_horizontalJoystickButtonLocation > m_horizontalJoystickBounds.right - m_joystickButtonRadius) {
                            m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.right - m_joystickButtonRadius;
                        } else if (m_horizontalJoystickButtonLocation < m_horizontalJoystickBounds.left + m_joystickButtonRadius) {
                            m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.left + m_joystickButtonRadius;
                        }
                    }
                    if (m_verticalPointerId != -1) {
                        float movedY = event.getY(m_verticalPointerId);

                        m_verticalJoystickButtonLocation += (movedY - m_verticalPointerLastPlace);
                        m_verticalPointerLastPlace = movedY;

                        if (m_verticalJoystickButtonLocation < m_verticalJoystickBounds.top + m_joystickButtonRadius) {
                            m_verticalJoystickButtonLocation = m_verticalJoystickBounds.top + m_joystickButtonRadius;
                        } else if (m_verticalJoystickButtonLocation > m_verticalJoystickBounds.bottom - m_joystickButtonRadius) {
                            m_verticalJoystickButtonLocation = m_verticalJoystickBounds.bottom - m_joystickButtonRadius;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (pointerId == m_horizontalPointerId || pointerId == m_verticalPointerId) {
                        m_verticalPointerId = m_horizontalPointerId = -1;
                        m_horizontalJoystickButtonLocation = m_horizontalJoystickBounds.centerX();
                        m_verticalJoystickButtonLocation = m_verticalJoystickBounds.centerY();
                    } else {
                        returnedValue = false;
                    }
                    break;

                default:
                    returnedValue = false;
            }
            return returnedValue;
        }

        @Override
        protected void onPostExecute(Boolean toRender) {
            if (toRender) {
                DoubleJoystickView.this.invalidate();
            }
        }
    }
//endregion
}
