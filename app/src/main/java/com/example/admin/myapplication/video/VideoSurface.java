/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package com.example.admin.myapplication.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.nio.ByteBuffer;

import businessLogic.common.interfaces.ILog;
import businessLogic.droneVideoProvider.FFmpegService;
import businessLogic.droneVideoProvider.ImageProvider;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

public class VideoSurface extends SurfaceView implements SurfaceHolder.Callback {

    //region Fields

    private final ILog m_logger;

    private final Rect m_rectangle;

    private SurfaceHolder m_surfaceHolder;

    private Bitmap m_bitmap;

    //endregion

    //region Constructors

    public VideoSurface(Context context) {
        this(context, null);

    }

    public VideoSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_logger = LogManager.getLogger();

        m_bitmap = null;

        m_rectangle = new Rect();

        m_surfaceHolder = getHolder();

        m_surfaceHolder.addCallback(this);
    }
    //endregion

    //region Methods

    @UiThread
    private void drawBitmap() {

        Canvas canvas = null;
        Bitmap localBitmap = m_bitmap;
        if(m_surfaceHolder==null){
            return;
        }
        try {
            canvas = m_surfaceHolder.lockCanvas(null);

            if (canvas != null) {
                canvas.drawBitmap(localBitmap, null, m_rectangle, null);
            }

        } catch (Exception e) {
            m_logger.warning("Exception while trying draw new canvas ", e);
        } finally {
            if (canvas != null) {
                m_surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public synchronized void setBitmap(@NonNull Bitmap bitmap) {
        m_bitmap = bitmap;

        post(this::drawBitmap);
    }

    public void setDroneBitmap(@NonNull Bitmap bitmap) {
        m_bitmap = bitmap;
    }

    public Bitmap getCurrentBitmap() {
        return m_bitmap;
    }

    //endregion

    //region SurfaceHolder.Callback Implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_logger.verbose("in surfaceCreated.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        m_logger.verbose("in surfaceChanged.");

        if (holder != null) {
            m_surfaceHolder = holder;
        }

        m_rectangle.set(0, 0, width, height);
        m_rectangle.sort();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        m_logger.verbose("in surfaceDestroyed.");
        m_surfaceHolder = null;
    }

    //endregion
}

