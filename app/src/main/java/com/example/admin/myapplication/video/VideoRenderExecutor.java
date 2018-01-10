/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.VideoConfiguration;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import businessLogic.common.interfaces.IHandler;
import businessLogic.droneVideoProvider.FFmpegService;
import businessLogic.droneVideoProvider.ImageProvider;
import businessLogic.graph.executors.interfaces.IRenderExecutor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

public class VideoRenderExecutor extends ServicesBaseBgOperation implements IRenderExecutor<Bitmap> {

    //region Fields

    private static final int DEFAULT_DRAWABLE_ID = R.drawable.ic_video_off;

    private final View m_containView;

    @BindView(R.id.video_surface)
    VideoSurface m_videoSurface;

    @BindView(R.id.textView)
    TextView m_textView;

    private Unbinder m_bind;

    private IHandler<Exception> m_exceptionHandler;

    private Bitmap m_defaultBitmap;

    private Timer m_renderingTimer;

    private TimerTask m_renderTask;

    private short m_renderTime;

    //endregion

    //region Constructors

    public VideoRenderExecutor(View view, String id) {
        super(id);
        m_containView = view;
    }

    public VideoRenderExecutor(View view) {
        this(view, "");
    }

    //endregion

    //region IRenderExecutor<Bitmap> Implementation

    @Override
    public void setInput(Bitmap bitmap) {
        m_videoSurface.setBitmap(bitmap);
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    //endregion

    //region IBackgroundOperation Implementation

    @Override
    protected void internalInitialize() throws Exception {
        if (m_containView == null) {
            String msg = "VideoSurface is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        m_bind = ButterKnife.bind(this, m_containView);

        m_defaultBitmap = BitmapFactory.decodeResource(m_containView.getResources(), DEFAULT_DRAWABLE_ID);

        m_videoSurface.setBitmap(m_defaultBitmap);

        VideoConfiguration videoConfiguration = ConfigurationManager.getVideoConfiguration();

        if (videoConfiguration == null) {
            throw new Exception("VideoConfiguration not initialize in ConfigurationManager");
        }

        m_renderTime = videoConfiguration.getRenderingFrequencyInMillis();
    }

    @Override
    protected void internalStart() throws Exception {
        if (m_exceptionHandler == null) {
            String msg = "IHandler<Exception> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        m_renderTask = new RenderingTimerTask(m_videoSurface, m_defaultBitmap, m_containView.getContext(), m_textView);

        m_renderingTimer = new Timer();

        m_renderingTimer.schedule(m_renderTask, 0, m_renderTime);

    }


    @Override
    protected void internalStop() throws Exception {
        m_renderTask.cancel();

        m_renderingTimer.cancel();

        m_renderingTimer.purge();

    }

    @Override
    protected void innerDispose() {
        if (m_bind != null) {
            m_bind.unbind();
            m_bind = null;
        }
    }

    //endregion

    //region Nested Classes

    //endregion
}
