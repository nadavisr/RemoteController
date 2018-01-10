/*
 * Created by admin on 03/12/2017
 * Last modified 15:13 03/12/17
 */

package com.example.admin.myapplication.map.executors;

import android.graphics.PointF;

import com.example.admin.myapplication.common.Messages.MapMessage;
import com.example.admin.myapplication.map.drawables.MapCursor;
import com.example.admin.myapplication.map.drawables.MapLine;

import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.ITransformExecutor;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.</P>
 * <P> A class that decode from {@link MapMessage} to {@link MapData}.</P>
 */

public class MapDecodeExecutor extends ServicesBaseBgOperation implements ITransformExecutor<MapMessage, MapData> {

    //region Fields

    private IHandler<Exception> m_exceptionHandler;

    private IHandler<MapData> m_sourceHandler;

    //endregion

    //region Methods

    //region ITransformExecutor<MapMessage, MapData> Implementation

    @Override
    public void setInput(MapMessage mapMessage) {

        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (m_sourceHandler == null) {
            m_logger.warning("Received input but the handler is null.");
            return;
        }

        try {
            calculateMapData(mapMessage);
        } catch (Exception ex) {
            String errMsg = "An exception received on try to calculate drawable from Map Message: " + mapMessage;
            m_logger.warning(errMsg, ex);
            m_exceptionHandler.setInput(new Exception(errMsg, ex));
        }
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {

        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<MapData> sourceHandler) {

        m_sourceHandler = sourceHandler;
    }

    //endregion

    //region ServicesBaseBgOperation Implementation

    @Override
    protected void internalInitialize() throws Exception {
        //No internal logic.
    }

    @Override
    protected void internalStart() throws Exception {
        //No internal logic.
    }

    @Override
    protected void internalStop() throws Exception {
        //No internal logic.
    }

    @Override
    protected void innerDispose() throws Exception {
        m_exceptionHandler = null;
        m_sourceHandler = null;
    }

    //endregion

    //region Private Methods

    @SuppressWarnings("SuspiciousNameCombination")
    private void calculateMapData(MapMessage mapMessage) {

        MapData mapData = new MapData();
        if (mapMessage.LocationX != 0 && mapMessage.LocationY != 0) {
            if (mapMessage.Angle < 0.0f) {
                mapMessage.Angle += 2 * Math.PI;
            }
            double degrees = Math.toDegrees(mapMessage.Angle);
            int angleInDegrees = (int) Math.round(degrees);
            MapCursor mapCursor = new MapCursor(new PointF(mapMessage.LocationX, mapMessage.LocationY),
                    30, 10, angleInDegrees);
            mapData.setMapCursor(mapCursor);
        }

        if (!(mapMessage.LandmarkSegmentStartX == 0 && mapMessage.LandmarkSegmentEndX == 0 &&
                mapMessage.LandmarkSegmentStartY == 0 && mapMessage.LandmarkSegmentEndY == 0)) {
            MapLine mapLine = new MapLine(mapMessage.LandmarkId, mapMessage.LandmarkSegmentStartX, mapMessage.LandmarkSegmentStartY, mapMessage.LandmarkSegmentEndX, mapMessage.LandmarkSegmentEndY);
            mapData.setMapDrawable(mapLine, mapMessage.OperationOnLandmark, mapMessage.LandmarkClass);
        } else {
            //TODO set ellipse
        }
        if (!mapData.isEmpty()) {
            m_sourceHandler.setInput(mapData);
        }
    }

    //endregion

    //endregion
}
