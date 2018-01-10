/*
 * Created by admin on 25/10/2017
 * Last modified 12:53 25/10/17
 */

package com.example.admin.myapplication.driveControl.joysticks;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.admin.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.</P>
 * <P>A joystick class, that know also calculate the requested speed from the user presses.</P>
 */
public class JoystickView extends View implements IJoystickView {

    //region Fields

    //region Constants

    private static final int DEFAULT_SIZE = 200;

    public static final float BACKGROUND_SIZE_RATIO = 0.75f;

    public static final float DEATH_ZONE_RATIO = 0.05f;

    //endregion

    //region Default Values

    private static final int DEFAULT_COLOR_BUTTON = Color.LTGRAY;

    private static final int DEFAULT_COLOR_BORDER = Color.BLACK;

    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    private static final int DEFAULT_WIDTH_BORDER = 3;

    private static final boolean DEFAULT_FIXED_CENTER = true;

    private static final boolean DEFAULT_AUTO_RECENTER_BUTTON = true;

    private static final int DEFAULT_ALPHA = 255;

    public static final int DEFAULT_BUTTON_DIRECTION = 0; // both

    public static final float DEFAULT_BUTTON_SIZE_RATIO = 0.2f;

    //endregion

    //region Private Fields

    private final ILog m_logger;

    private Paint m_buttonFill;

    private Paint m_backgroundBorder;

    private Paint m_backgroundFill;

    private Bitmap m_buttonBitmap;

    private float m_buttonSizeRatio;

    volatile private int m_posX;

    volatile private int m_posY;

    volatile private int m_centerX;

    volatile private int m_centerY;

    volatile private int m_fixedCenterX;

    volatile private int m_fixedCenterY;

    volatile private int m_buttonRadius;

    volatile private float m_backgroundRadius;

    volatile private boolean m_fixedCenter;

    private boolean m_autoReCenterButton;

    private boolean m_enabled;

    private boolean m_angleAndPower;

    private IOnMoveListener m_callback;

    private long m_eventFrequencyInMilliseconds;

    private int m_buttonDirection;

    private boolean m_hasBackground;

    volatile private Timer m_timer;

    private EventTimerTask m_eventTimerTask;

    // This executor service here with OnTouchTask to be alternative to OnTouchAsyncTask
    //private ExecutorService m_executorService;

    //endregion

    //endregion

    //region Constructors

    /**
     * Simple constructor to use when creating a JoystickView from code.
     * Call another constructor passing null to Attribute.
     *
     * @param context The Context the JoystickView is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public JoystickView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a JoystickView from XML. This is called
     * when a JoystickView is being constructed from an XML file, supplying attributes
     * that were specified in the XML file.
     *
     * @param context The Context the JoystickView is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the JoystickView.
     */
    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_logger = LogManager.getLogger();

        initializeValuesFromXml(context, attrs);

        m_posX = m_posY = m_centerX = m_centerY = m_fixedCenterX = m_fixedCenterY = 0;

        m_timer = new Timer();

        //m_executorService = Executors.newSingleThreadExecutor();

        m_logger.info("JoystickView successfully created.");
    }


    //endregion

    //region Methods

    //region Getters & Setters

    //region Getters

    /**
     * Return the current direction allowed for the button to move
     *
     * @return Actually return an integer corresponding to the direction:
     * - A negative value is horizontal axe,
     * - A positive value is vertical axe,
     * - Zero means both axes
     */
    public int getButtonDirection() {
        return m_buttonDirection;
    }

    /**
     * Return the state of the joystick. False when the button don't move.
     *
     * @return the state of the joystick
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    //endregion

    //region Setters

    /**
     * Enable or disable the joystick.
     *
     * @param enabled False mean the button won't move and onMove won't be called
     */
    public void setJoystickEnabled(boolean enabled) {
        m_enabled = enabled;
    }


    /**
     * Set the current authorized direction for the button to move
     *
     * @param direction the value will define the authorized direction:
     *                  - any negative value (such as -1) for horizontal axe
     *                  - any positive value (such as 1) for vertical axe
     *                  - zero (0) for the full direction (both axes)
     */
    public void setButtonDirection(int direction) {
        m_buttonDirection = direction;
    }

    //endregion

    //endregion

    //region View Override

    @Override
    protected void onDraw(Canvas canvas) {

        if (!m_hasBackground) {
            if (m_backgroundFill != null) {
                canvas.drawCircle(m_fixedCenterX, m_fixedCenterY, m_backgroundRadius, m_backgroundFill);
            }
            if (m_backgroundBorder != null) {
                // Draw the circle border
                canvas.drawCircle(m_fixedCenterX, m_fixedCenterY, m_backgroundRadius, m_backgroundBorder);
            }
        }

        // Draw the button from image
        if (m_buttonBitmap != null) {
            canvas.drawBitmap(
                    m_buttonBitmap,
                    m_posX + m_fixedCenterX - m_centerX - m_buttonRadius,
                    m_posY + m_fixedCenterY - m_centerY - m_buttonRadius,
                    m_buttonFill
            );
        } else {
            // Draw the button as simple circle
            canvas.drawCircle(
                    m_posX + m_fixedCenterX - m_centerX,
                    m_posY + m_fixedCenterY - m_centerY,
                    m_buttonRadius,
                    m_buttonFill
            );
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        // get the center of view to position circle
        m_fixedCenterY = m_centerY = m_posY = m_fixedCenterX = m_centerX = m_posX = getWidth() / 2;

        // radius based on smallest size : height OR width
        int d = Math.min(w, h);
        if (m_hasBackground) {
            m_backgroundRadius = d / 2;
        } else {
            m_backgroundRadius = (int) (d / 2.0 * BACKGROUND_SIZE_RATIO);
        }
        m_buttonRadius = (int) (d / 2.0 * m_buttonSizeRatio);

        if (m_buttonBitmap != null) {
            m_buttonBitmap = Bitmap.createScaledBitmap(m_buttonBitmap, m_buttonRadius * 2, m_buttonRadius * 2, false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        // setting the measured values to resize the view to a certain width and height
        int d = Math.min(width, height);
        setMeasuredDimension(d, d);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if disabled we don't move the
        if (!m_enabled) {
            return true;
        }

//        OnTouchTask onTouchTask = new OnTouchTask(event);
//        m_executorService.execute(onTouchTask);

        OnTouchAsyncTask asyncTask = new OnTouchAsyncTask();
        asyncTask.execute(event);

        this.performClick();
        return true;
    }



    @Override
    @SuppressWarnings("EmptyMethod")
    public boolean performClick() {
        return super.performClick();
    }

    //endregion

    //region Private Methods

    private int measure(int measureSpec) {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            // if no bounds are specified return a default size (200)
            return DEFAULT_SIZE;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            return MeasureSpec.getSize(measureSpec);
        }
    }


    /**
     * Process the angle following the 360° counter-clock protractor rules.
     *
     * @return the angle of the button
     */
    private int getButtonAngle() {
        int angle = (int) Math.toDegrees(Math.atan2(m_centerY - m_posY, m_posX - m_centerX));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }


    /**
     * Process the strength as a percentage of the distance between the center and the border.
     *
     * @return the strength of the button
     */
    private int getButtonPower() {

        int x = m_posX - m_centerX;
        int y = m_posY - m_centerY;

        return (int) (100 * Math.sqrt(x * x + y * y) / m_backgroundRadius);
    }


    private int getButtonX() {
        return m_posX - m_centerX;
    }

    private int getButtonY() {
        return m_centerY - m_posY;
    }


    private void initializeValuesFromXml(Context context, AttributeSet attrs) {
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.JoystickView,
                0, 0
        );

        int buttonColor;
        int borderColor;
        int backgroundColor;
        int borderWidth;
        int backgroundAlpha;
        int buttonAlpha;
        Drawable buttonDrawable;
        try {
            buttonColor = styledAttributes.getColor(R.styleable.JoystickView_button_color, DEFAULT_COLOR_BUTTON);
            borderColor = styledAttributes.getColor(R.styleable.JoystickView_background_border_color, DEFAULT_COLOR_BORDER);
            buttonAlpha = styledAttributes.getInt(R.styleable.JoystickView_button_alpha, DEFAULT_ALPHA);
            backgroundColor = styledAttributes.getColor(R.styleable.JoystickView_background_color, DEFAULT_BACKGROUND_COLOR);
            backgroundAlpha = styledAttributes.getInt(R.styleable.JoystickView_background_alpha, DEFAULT_ALPHA);
            borderWidth = styledAttributes.getDimensionPixelSize(R.styleable.JoystickView_background_border_width, DEFAULT_WIDTH_BORDER);
            m_fixedCenter = styledAttributes.getBoolean(R.styleable.JoystickView_fixed_center, DEFAULT_FIXED_CENTER);
            m_autoReCenterButton = styledAttributes.getBoolean(R.styleable.JoystickView_auto_recenter_button, DEFAULT_AUTO_RECENTER_BUTTON);
            buttonDrawable = styledAttributes.getDrawable(R.styleable.JoystickView_button_image);
            m_enabled = styledAttributes.getBoolean(R.styleable.JoystickView_enabled, true);
            m_buttonSizeRatio = styledAttributes.getFraction(R.styleable.JoystickView_button_size_ratio, 1, 1, DEFAULT_BUTTON_SIZE_RATIO);
            m_buttonDirection = styledAttributes.getInteger(R.styleable.JoystickView_button_direction, DEFAULT_BUTTON_DIRECTION);
        } finally {
            styledAttributes.recycle();
        }

        // Initialize the drawing according to attributes

        m_buttonFill = new Paint();

        if (buttonDrawable != null &&
                buttonDrawable instanceof BitmapDrawable) {
            m_buttonBitmap = ((BitmapDrawable) buttonDrawable).getBitmap();
        } else {
            m_buttonFill = new Paint();
            m_buttonFill.setAntiAlias(true);
            m_buttonFill.setColor(buttonColor);
            m_buttonFill.setStyle(Paint.Style.FILL);
            m_buttonBitmap = null;
        }
        m_buttonFill.setAlpha(buttonAlpha);

        Drawable background = getBackground();
        m_hasBackground = background != null;

        if (m_hasBackground) {
            background.setAlpha(backgroundAlpha);
        } else if (backgroundColor != Color.TRANSPARENT) {
            m_backgroundFill = new Paint();
            m_backgroundFill.setAntiAlias(true);
            m_backgroundFill.setColor(backgroundColor);
            m_backgroundFill.setStyle(Paint.Style.FILL);
        } else {
            m_backgroundFill = null;
        }


        if (borderColor != Color.TRANSPARENT && borderWidth > 0) {
            m_backgroundBorder = new Paint();
            m_backgroundBorder.setAntiAlias(true);
            m_backgroundBorder.setColor(borderColor);
            m_backgroundBorder.setStyle(Paint.Style.STROKE);
            m_backgroundBorder.setStrokeWidth(borderWidth);
        } else {
            m_backgroundBorder = null;
        }

    }


    //endregion

    //region IJoystickView Implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMoveListener(IOnMoveListener onMoveListener) {
        setOnMoveListener(onMoveListener, DEFAULT_EVENT_FREQUENCY_IN_MILLISECONDS);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMoveListener(IOnMoveListener onMoveListener, @IntRange(from = 1) int eventFrequencyInMilliseconds) {
        m_angleAndPower = onMoveListener instanceof IOnMoveListenerGetAnglePower;
        m_callback = onMoveListener;
        m_eventFrequencyInMilliseconds = eventFrequencyInMilliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] checkDeadZones(int x, int y) {
        double xInPercent = x / m_backgroundRadius;
        if (Math.abs(xInPercent) < DEATH_ZONE_RATIO) {
            x = 0;
        }

        double yInPercent = y / m_backgroundRadius;
        if (Math.abs(yInPercent) < DEATH_ZONE_RATIO) {
            y = 0;
        }

        int[] returnedArray = new int[2];
        returnedArray[0] = x;
        returnedArray[1] = y;
        return returnedArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculatePower(int x, int y) {
        return (int) (100 * Math.sqrt(x * x + y * y) / m_backgroundRadius);
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

    //endregion

    //region IDisposable Implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        m_logger.info("Staring to dispose JoystickView..");
        boolean disposeSucceed = true;
        try {
            if (m_eventTimerTask != null) {
                m_eventTimerTask.cancel();
                m_eventTimerTask = null;
            }
            if (m_timer != null) {
                m_timer.cancel();
                m_timer = null;
            }

//            if (m_executorService != null) {
//                m_executorService.shutdownNow();
//            }

        } catch (Exception ex) {
            m_logger.warning("JoystickView disposing failed!", ex);
            disposeSucceed = false;
        }
        if (disposeSucceed) {
            m_logger.info("JoystickView disposing finished successfully.");
        }
    }

    //endregion

    //endregion

    //region Nested Classes

    private class EventTimerTask extends TimerTask {

        private final IOnMoveListener m_listener;

        public EventTimerTask(IOnMoveListener listener) {
            m_listener = listener;
        }

        @Override
        public void run() {
            if (m_listener == null) {
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
            m_listener.onMove(firstArgument, secondArgument);

        }
    }

    private class OnTouchTask implements Runnable {

        private final MotionEvent m_motionEvent;
        private final Handler m_handler;

        public OnTouchTask(MotionEvent motionEvent) {
            m_motionEvent = motionEvent;
            m_handler = new Handler();
        }

        protected Boolean doInBackground() {

            int action = m_motionEvent.getAction();

            if (action == MotionEvent.ACTION_UP) {
                if (m_autoReCenterButton) {
                    m_posX = m_centerX;
                    m_posY = m_centerY;
                }

                if (m_eventTimerTask != null) {

                    boolean cancel = m_eventTimerTask.cancel();
                    if (!cancel) {
                        m_timer.cancel();
                        m_timer = new Timer();
                    } else {
                        m_timer.purge();
                    }
                    m_eventTimerTask = null;

                }
                if (m_callback != null) {
                    m_callback.onMove(0, 0);
                }
                return true;
            }

            // to move the button according to the finger coordinate
            // (or limited to one axe according to direction option
            m_posY = m_buttonDirection < 0 ? m_centerY : (int) m_motionEvent.getY(); // direction negative is horizontal axe
            m_posX = m_buttonDirection > 0 ? m_centerX : (int) m_motionEvent.getX(); // direction positive is vertical axe


            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (!m_fixedCenter) {
                        m_centerX = m_posX;
                        m_centerY = m_posY;
                    }

                    if (m_callback != null) {
                        m_eventTimerTask = new EventTimerTask(m_callback);
                        m_timer.schedule(m_eventTimerTask, 0, m_eventFrequencyInMilliseconds);
                    } else {
                        m_eventTimerTask = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = m_posX - m_centerX;
                    int y = m_posY - m_centerY;
                    double abs = Math.sqrt(x * x + y * y);

                    if (abs > m_backgroundRadius) {
                        m_posX = (int) (x * m_backgroundRadius / abs + m_centerX);
                        m_posY = (int) (y * m_backgroundRadius / abs + m_centerY);
                    }
                    break;
            }
            return true;
        }


        @Override
        public void run() {
            Boolean toDraw = doInBackground();
            if (toDraw) {
                m_handler.post(JoystickView.this::invalidate);
            }
        }
    }

    //TODO : check for better option.
    private class OnTouchAsyncTask extends AsyncTask<MotionEvent, Void, Boolean> {

        protected Boolean doInBackground(MotionEvent... motionEvents) {
            if (motionEvents.length < 1) {
                return false;
            }
            MotionEvent motionEvent = motionEvents[0];
            int action = motionEvent.getAction();

            if (action == MotionEvent.ACTION_UP) {
                if (m_autoReCenterButton) {
                    m_posX = m_centerX;
                    m_posY = m_centerY;
                }

                m_eventTimerTask = new EventTimerTask(m_callback);
                //m_timer.schedule(m_eventTimerTask, 0, m_eventFrequencyInMilliseconds);
                m_eventTimerTask.run();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (m_eventTimerTask != null) {
                    boolean cancel = m_eventTimerTask.cancel();
                    if (!cancel) {
                        m_timer.cancel();
                        m_timer = new Timer();
                    } else {
                        m_timer.purge();
                    }
                    m_eventTimerTask = null;

                }
                if (m_callback != null) {
                    m_callback.onMove(0, 0);
                }
                return true;
            }

            // to move the button according to the finger coordinate
            // (or limited to one axe according to direction option
            m_posY = m_buttonDirection < 0 ? m_centerY : (int) motionEvent.getY(); // direction negative is horizontal axe
            m_posX = m_buttonDirection > 0 ? m_centerX : (int) motionEvent.getX(); // direction positive is vertical axe


            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (!m_fixedCenter) {
                        m_centerX = m_posX;
                        m_centerY = m_posY;
                    }

                    if (m_callback != null) {
                        m_eventTimerTask = new EventTimerTask(m_callback);
                        m_timer.schedule(m_eventTimerTask, 0, m_eventFrequencyInMilliseconds);
                    } else {
                        m_eventTimerTask = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = m_posX - m_centerX;
                    int y = m_posY - m_centerY;
                    double abs = Math.sqrt(x * x + y * y);

                    if (abs > m_backgroundRadius) {
                        m_posX = (int) (x * m_backgroundRadius / abs + m_centerX);
                        m_posY = (int) (y * m_backgroundRadius / abs + m_centerY);
                    }
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean toRender) {
            if (toRender) {
                JoystickView.this.invalidate();
            }
        }
    }
    //endregion
}
