/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.map;

import android.view.View;

import com.example.admin.myapplication.common.Messages;
import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.MapConfiguration;
import com.example.admin.myapplication.map.executors.MapData;
import com.example.admin.myapplication.map.executors.MapDecodeExecutor;
import com.example.admin.myapplication.map.executors.MapSimulationSourceExecutor;
import com.example.admin.myapplication.map.executors.MapSourceExecutor;
import com.example.admin.myapplication.map.executors.MapViewExecutor;

import java.util.ArrayList;
import java.util.List;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.IRendering;
import businessLogic.graph.executors.ProducerConsumerExecutor;
import businessLogic.graph.executors.interfaces.ISourceExecutor;
import businessLogic.graph.executors.interfaces.ITransformExecutor;
import businessLogic.graph.filters.FilterException;
import businessLogic.graph.filters.RenderFilter;
import businessLogic.graph.filters.SourceFilter;
import businessLogic.graph.filters.TransformFilter;
import businessLogic.graph.filters.interfaces.IFilter;
import businessLogic.graph.filters.interfaces.IRenderFilter;
import businessLogic.graph.filters.interfaces.ISourceFilter;
import services.common.ServicesBaseBgOperation;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map</P>
 * <P></P>
 */
public class MapGraph extends ServicesBaseBgOperation implements IRendering {

    //region Constants

    private final static int GRAPH_SIZE = 5;

    //endregion

    //region Fields

    private List<IFilter> m_graph;

    private ExceptionHandler m_exceptionHandler;

    private MapConfiguration m_mapConfiguration;

    private IRendering m_render;

    //endregion

    //region Constructors

    public MapGraph(String id) {
        super(id);
    }

    public MapGraph() {
        super();
    }

    //endregion

    //region Methods

    //region IBackgroundOperation Implementation

    @SuppressWarnings("unchecked")
    @Override
    protected void internalInitialize() throws Exception {
        m_render = null;
        m_exceptionHandler = new ExceptionHandler();

        m_graph = new ArrayList<>(5);

        //Source filter  -

        m_mapConfiguration = ConfigurationManager.getMapConfiguration();
        if (m_mapConfiguration == null) {
            throw new Exception("Map configuration did not initialized well.");
        }


//        ISourceExecutor<Messages.MapMessage> mapSourceExecutor = new MapSourceExecutor();

        /*Simulation source executor*/
        ISourceExecutor<Messages.MapMessage> mapSourceExecutor = new MapSimulationSourceExecutor();

        SourceFilter<Messages.MapMessage> mapMessageSourceFilter = new SourceFilter(mapSourceExecutor, m_logger);
        m_graph.add(mapMessageSourceFilter);

        //Transform filter between source to decoder  -
        ProducerConsumerExecutor<Messages.MapMessage> mapMessageProducerConsumerExecutor = new ProducerConsumerExecutor<>(m_logger);
        TransformFilter<Messages.MapMessage, Messages.MapMessage> mapMessageMapMessageTransformFilter =
                new TransformFilter<>(mapMessageProducerConsumerExecutor, m_logger);
        m_graph.add(mapMessageMapMessageTransformFilter);

        // DecoderExecutor  -
        ITransformExecutor<Messages.MapMessage, MapData> drawableExecutor = new MapDecodeExecutor();
        TransformFilter<Messages.MapMessage, MapData> drawableTransformFilter =
                new TransformFilter<>(drawableExecutor, m_logger);
        m_graph.add(drawableTransformFilter);

        //Transform filter between decoder to setInput -
        ProducerConsumerExecutor<MapData> mapDrawableProducerConsumerExecutor = new ProducerConsumerExecutor<>(m_logger);
        TransformFilter<MapData, MapData> mapDrawableIMapDrawableTransformFilter =
                new TransformFilter<>(mapDrawableProducerConsumerExecutor, m_logger);
        m_graph.add(mapDrawableIMapDrawableTransformFilter);


        for (IFilter filter : m_graph) {
            filter.initialize();
            if (filter.getState() == BgOperationState.Error) {
                String msg = "Map Graph failed in initialization of his graph, failed filter: " +
                        filter.getId() + ", For more information go to the log.";
                m_logger.error(msg);
                throw new Exception(msg);
            }
            filter.setExceptionHandler(m_exceptionHandler);
        }

        // Set filters relationship
        mapMessageSourceFilter.setFollowerFilter(mapMessageMapMessageTransformFilter);
        mapMessageMapMessageTransformFilter.setFollowerFilter(drawableTransformFilter);
        drawableTransformFilter.setFollowerFilter(mapDrawableIMapDrawableTransformFilter);
    }

    @SuppressWarnings("unchecked")
    public void initializeViewFilters(View view) {
        int lastIndex = m_graph.size() - 1;



        //Render
        MapViewExecutor mapViewExecutor = new MapViewExecutor(view, m_mapConfiguration.getRenderingFrequencyInMillis());
        IRenderFilter<MapData> mapDrawableRenderFilter = new RenderFilter<>(mapViewExecutor, m_logger);
        m_graph.add(mapDrawableRenderFilter);

        if (mapViewExecutor instanceof IRendering) {
            m_render = (IRendering) mapViewExecutor;
        }

        mapDrawableRenderFilter.initialize();
        mapDrawableRenderFilter.setExceptionHandler(m_exceptionHandler);

        IFilter filter = m_graph.get(lastIndex);

        ISourceFilter<MapData> mapDrawableISourceFilter;
        try {
            mapDrawableISourceFilter = (ISourceFilter<MapData>) filter;
        } catch (Exception ex) {
            String msg = "The last filter in the graph does not match to TransformFilter";
            m_logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        mapDrawableISourceFilter.setFollowerFilter(mapDrawableRenderFilter);

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

        m_render = null;
    }

    @Override
    protected void internalStart() throws Exception {
        if (m_graph.size() != GRAPH_SIZE) {
            String msg = "Some of the graph are missing, required " + GRAPH_SIZE + " components and exist only " + m_graph.size() + '.';
            m_logger.error(msg);
            throw new RuntimeException(msg);
        }

        startGraph();
    }

    @Override
    protected void internalStop() throws Exception {
        stopGraph();
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
        m_render = null;

    }


    //endregion

    //region Private Methods

    private void startGraph() throws Exception {
        synchronized (this) {
            for (int i = m_graph.size() - 1; i >= 0; i--) {
                IFilter filter = m_graph.get(i);
                filter.start();
                if (filter.getState() == BgOperationState.Error) {
                    String msg = "Map graph failed in starting, failed filter: " +
                            filter.getId() + ", For more information go to the log.";
                    m_logger.error(msg);
                    throw new Exception(msg);
                }
            }
            startRendering();
        }
    }

    private void stopGraph() throws Exception {
        synchronized (this) {
            stopRendering();
            for (IFilter filter : m_graph) {
                filter.stop();
                if (filter.getState() == BgOperationState.Error) {
                    String msg = "Map graph failed in stopping, failed filter: "
                            + filter.getId() + ", For more information go to the log.";
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
        }

        m_logger.info("the graph: " + getId() + ", restarted successfully.");
    }


    //endregion

    //region IRendering implementation

    @Override
    public boolean startRendering() {
        return m_render != null && m_render.startRendering();
    }

    @Override
    public void stopRendering() {
        if (m_render != null) {
            m_render.stopRendering();
        }
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
                    m_logger.info("Stopping the graph: " + getId() + ", Map Graph state updated to Error.");
                    try {
                        stopGraph();
                    } catch (Exception innerException) {
                        msg = "Failed to stop the graph: " + getId();
                        m_logger.error(msg, innerException);
                    }
                    m_bgOperationState = BgOperationState.Error;
                    break;
            }
        }
    }

    //endregion
}
