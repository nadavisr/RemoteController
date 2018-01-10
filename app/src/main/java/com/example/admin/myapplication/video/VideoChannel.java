/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.admin.myapplication.common.Messages.ImageMessage;

import java.util.ArrayList;
import java.util.List;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.ProducerConsumerExecutor;
import businessLogic.graph.executors.interfaces.IRenderExecutor;
import businessLogic.graph.executors.interfaces.ITransformExecutor;
import businessLogic.graph.filters.FilterException;
import businessLogic.graph.filters.RenderFilter;
import businessLogic.graph.filters.SourceFilter;
import businessLogic.graph.filters.TransformFilter;
import businessLogic.graph.filters.interfaces.IFilter;
import businessLogic.graph.filters.interfaces.IRenderFilter;
import businessLogic.graph.filters.interfaces.ISourceFilter;
import services.common.ServicesBaseBgOperation;

import static com.example.admin.myapplication.common.Intents.EXTRA_KEY_EXCEPTION_MESSAGE;
import static com.example.admin.myapplication.common.Intents.KEY_VIDEO_CHANNEL_STARTED;
import static com.example.admin.myapplication.common.Intents.KEY_VIDEO_CHANNEL_STOPPED;
import static com.example.admin.myapplication.common.Intents.KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

public class VideoChannel extends ServicesBaseBgOperation {

    private final static int GRAPH_SIZE = 5;

    //region Fields

    private final Context m_context;

    private List<IFilter> m_graph;

    private ExceptionHandler m_exceptionHandler;

    //endregion

    //region Constructors

    public VideoChannel(Context context, String id) {
        super(id);
        m_context = context;
    }

    public VideoChannel(Context context) {
        this(context, "");
    }

    //endregion

    //region IBackgroundOperation Implementation

    @SuppressWarnings("unchecked")
    @Override
    protected void internalInitialize() throws Exception {
        m_exceptionHandler = new ExceptionHandler();

        m_graph = new ArrayList<>(5);

        //Source filter (ImageMessage) -
//        VideoServiceExecutor videoSourceExecutorExecutor = new VideoServiceExecutor(m_context);

        VideoSourceExecutor videoSourceExecutorExecutor = new VideoSourceExecutor();
        SourceFilter<ImageMessage> ImageMessageSourceFilter = new SourceFilter<>(videoSourceExecutorExecutor, m_logger);
        m_graph.add(ImageMessageSourceFilter);

        //Transform filter between source to decoder (Producer Consumer BaseFilter) -
        ProducerConsumerExecutor<ImageMessage> ImageMessage_ProdConsExecutor = new ProducerConsumerExecutor<>(m_logger);
        TransformFilter<ImageMessage, ImageMessage> ImageMessageTransformFilter =
                new TransformFilter<>(ImageMessage_ProdConsExecutor, m_logger);
        m_graph.add(ImageMessageTransformFilter);

        // DecoderExecutor (To Bitmap) -
        ITransformExecutor<ImageMessage, Bitmap> decoder = new DecoderExecutor();
        TransformFilter<ImageMessage, Bitmap> transformer_ImageMessageToBitmap =
                new TransformFilter<>(decoder, m_logger);
        m_graph.add(transformer_ImageMessageToBitmap);

        //Transform filter between decoder to setInput(Producer Consumer BaseFilter) -
        ProducerConsumerExecutor<Bitmap> Bitmap_ProdConsExecutor = new ProducerConsumerExecutor<>(m_logger);
        TransformFilter<Bitmap, Bitmap> BitmapTransformFilter =
                new TransformFilter<>(Bitmap_ProdConsExecutor, m_logger);
        m_graph.add(BitmapTransformFilter);


        for (IFilter filter : m_graph) {
            filter.initialize();
            if (filter.getState() == BgOperationState.Error) {
                String msg = "Video Channel failed in initialization of his graph, failed filter: " +
                        filter.getId() + ", For more information go to the log.";
                m_logger.error(msg);
                throw new Exception(msg);
            }
            filter.setExceptionHandler(m_exceptionHandler);
        }

        // Set filters relationship
        ImageMessageSourceFilter.setFollowerFilter(ImageMessageTransformFilter);
        ImageMessageTransformFilter.setFollowerFilter(transformer_ImageMessageToBitmap);
        transformer_ImageMessageToBitmap.setFollowerFilter(BitmapTransformFilter);
    }

    @SuppressWarnings("unchecked")
    public void initializeViewFilters(View view) {
        int lastIndex = m_graph.size() - 1;

        //Render (Bitmap)
        IRenderExecutor<Bitmap> videoRenderExecutor = new VideoRenderExecutor(view);
        IRenderFilter<Bitmap> bitmapRenderFilter = new RenderFilter<>(videoRenderExecutor, m_logger);
        m_graph.add(bitmapRenderFilter);

        bitmapRenderFilter.initialize();
        bitmapRenderFilter.setExceptionHandler(m_exceptionHandler);

        IFilter filter = m_graph.get(lastIndex);

        ISourceFilter<Bitmap> BitmapTransformFilter;
        try {
            BitmapTransformFilter = (ISourceFilter<Bitmap>) filter;
        } catch (Exception ex) {
            String msg = "The last filter in the graph does not match to TransformFilter";
            m_logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        BitmapTransformFilter.setFollowerFilter(bitmapRenderFilter);

    }

    public void removeViewFilter() {
        if (m_bgOperationState == BgOperationState.Running) {
            String msg = "Could not remove view filters while the graph is running.";
            m_logger.error(msg);
            throw new RuntimeException(msg);
        }

        if (m_graph == null || m_graph.isEmpty()) {
            return;
        }

        int lastIndex = m_graph.size() - 1;
        IFilter filter = m_graph.remove(lastIndex);
        filter.stop();
        filter.dispose();

    }

    @Override
    protected void internalStart() throws Exception {

        if (m_graph.size() != GRAPH_SIZE) {
            String msg = "The graph is not the correct size";
            m_logger.error(msg);
            throw new RuntimeException(msg);
        }

        startGraph();

        final Intent intent = new Intent(KEY_VIDEO_CHANNEL_STARTED);
        m_context.sendBroadcast(intent);
        m_logger.info("Sent a broadcast that the video was started.");
    }

    @Override
    protected void internalStop() throws Exception {
        stopGraph();

        final Intent intent = new Intent(KEY_VIDEO_CHANNEL_STOPPED);
        m_context.sendBroadcast(intent);
        m_logger.info("Sent a broadcast that the video was stopped.");
    }

    @Override
    protected void innerDispose() {
        if (m_graph == null) {
            return;
        }

        for (IFilter filter : m_graph) {
            filter.dispose();
        }

        m_graph.clear();
        m_graph = null;

    }


    //endregion

    //region Methods

    private void startGraph() throws Exception {
        synchronized (this) {
            for (int i = m_graph.size() - 1; i >= 0; i--) {
                IFilter filter = m_graph.get(i);
                filter.start();
                if (filter.getState() == BgOperationState.Error) {
                    String msg = "Video Channel failed in starting of his graph, failed filter: " +
                            filter.getId() + ", For more information go to the log.";
                    m_logger.error(msg);
                    throw new Exception(msg);
                }
            }
        }
    }

    private void stopGraph() throws Exception {
        synchronized (this) {
            for (IFilter filter : m_graph) {
                filter.stop();
                if (filter.getState() == BgOperationState.Error) {
                    String msg = "Video Channel failed in stopping of his graph, failed" +
                            " filter: " + filter.getId() + ", For more information go to the log.";
                    m_logger.error(msg);
                    throw new Exception(msg);
                }
            }
        }
    }

    private void restart() {

        m_logger.info("Trying to restart the graph: " + getId());

        try {
            stopGraph();
            startGraph();
        } catch (Exception e) {
            m_bgOperationState = BgOperationState.Error;
            String msg = "Failed to restart the graph: " + getId() + ". VideoChannel state updated to Error.";
            m_logger.error(msg, e);
            intentStopOnException(e);
        }

        m_logger.info("the graph: " + getId() + ", restarted successfully.");
    }

    private void intentStopOnException(@Nullable Exception ex) {
        Intent intent = new Intent(KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION);
        if (ex != null) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_KEY_EXCEPTION_MESSAGE, ex.getMessage());
            intent.putExtras(bundle);
        }
        m_context.sendBroadcast(intent);
        m_logger.info("Sent a broadcast that the video was stopped due to an exception.");
    }

    //endregion

    //region Nested Classes

    private class ExceptionHandler implements IHandler<FilterException> {

        @Override
        public void setInput(FilterException filterException) {
            IFilter filter = filterException.getFilter();
            Exception exception = filterException.getException();

            String msg = "An error has been received from the IFilter: " + filter.getId()
                    + ", the IFilter state:" + filter.getState() + ".";
            switch (filter.getState()) {
                case Running:
                    m_logger.warning(msg, exception);
                    break;
                case Ready:
                    m_logger.warning(msg, exception);
                    restart();
                    break;
                case Error:
                    m_logger.error(msg, exception);
                    m_logger.info("Stopping the graph: " + getId() + ", VideoChannel state updated to Error.");
                    try {
                        stopGraph();
                    } catch (Exception innerException) {
                        msg = "Failed to stop the graph: " + getId();
                        m_logger.error(msg, innerException);
                    }
                    m_bgOperationState = BgOperationState.Error;
                    intentStopOnException(exception);
                    break;
            }
        }
    }

    //endregion
}
