/*
 * Created by admin on 04/12/2017
 * Last modified 09:57 04/12/17
 */

package com.example.admin.myapplication.map.executors;

import com.example.admin.myapplication.MainApplication;
import com.example.admin.myapplication.common.Enums;
import com.example.admin.myapplication.common.Messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.ISourceExecutor;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.executors.</P>
 * <P>A simulator executor that can replace the original SourceExecutor.</P>
 */

public class MapSimulationSourceExecutor extends ServicesBaseBgOperation implements ISourceExecutor<Messages.MapMessage> {

    private IHandler<Messages.MapMessage> m_sourceHandler;

    private File m_simulationFile;

    private boolean m_started;

    private List<Messages.MapMessage> m_mapMessages;

    private ExecutorService m_executorService;

    private IHandler<Exception> m_exceptionHandler;

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {

        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<Messages.MapMessage> sourceHandler) {

        m_sourceHandler = sourceHandler;
    }

    @Override
    protected void internalInitialize() throws Exception {
        m_simulationFile = new File(MainApplication.getApplicationExternalFolderPath(), "mapOutputData.txt");
        if (!m_simulationFile.exists()) {
            throw new FileNotFoundException("The file does not exist: " + m_simulationFile.getAbsolutePath());
        }
        m_mapMessages = new ArrayList<>();
        m_executorService = Executors.newSingleThreadExecutor();
        m_started = false;
    }

    @Override
    protected void internalStart() throws Exception {
        m_started = true;

        m_executorService.execute(this::readSimulationFile);
        m_executorService.execute(this::transferData);
    }

    @Override
    protected void internalStop() throws Exception {
        m_started = false;
        m_executorService.shutdownNow();
    }

    @Override
    protected void innerDispose() throws Exception {
        m_simulationFile = null;
        m_sourceHandler = null;
        m_executorService = null;
    }

    private void transferData() {
        for (int i = 0; i < m_mapMessages.size() && m_started; i++) {
            try {
                m_sourceHandler.setInput(m_mapMessages.get(i));
                Thread.sleep(10);
            } catch (InterruptedException e) {
                m_logger.debug("Transfer data thread interrupted, background operation changed to ERROR!", e);
                m_bgOperationState = BgOperationState.Error;
                m_exceptionHandler.setInput(e);
                return;
            }
        }
    }

    private void readSimulationFile() {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(m_simulationFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            return;
        }
        int count = 0;
        String[] split = sb.toString().split(",");
        for (int i = 0; i < split.length / 21 && m_started; i++) {
            try {
                Messages.MapMessage mapMessage = new Messages.MapMessage();
                mapMessage.LocationX = Float.parseFloat(split[i * 21].trim());
                mapMessage.LocationY = Float.parseFloat(split[i * 21 + 1].trim());
                mapMessage.Angle = Float.parseFloat(split[i * 21 + 2].trim());
                mapMessage.ReceptionIntensity = Float.parseFloat(split[i * 21 + 3].trim());
                mapMessage.ExposedFlag = Integer.parseInt(split[i * 21 + 4].trim()) == 1;
                mapMessage.AmbientLightIntensity = Float.parseFloat(split[i * 21 + 5].trim());
                mapMessage.SlopeAngle = Float.parseFloat(split[i * 21 + 6].trim());
                mapMessage.LandmarkId = Math.round(Float.parseFloat(split[i * 21 + 7].trim()));
                mapMessage.OperationOnLandmark = Enum.valueOf(Enums.MapOperation.class, split[i * 21 + 8].trim());
                mapMessage.LandmarkSegmentStartX = Float.parseFloat(split[i * 21 + 9].trim());
                mapMessage.LandmarkSegmentStartY = Float.parseFloat(split[i * 21 + 10].trim());
                mapMessage.LandmarkSegmentEndX = Float.parseFloat(split[i * 21 + 11].trim());
                mapMessage.LandmarkSegmentEndY = Float.parseFloat(split[i * 21 + 12].trim());
                mapMessage.LandmarkSegmentEllipseA = Float.parseFloat(split[i * 21 + 13].trim());
                mapMessage.LandmarkSegmentEllipseB = Float.parseFloat(split[i * 21 + 14].trim());
                mapMessage.LandmarkSegmentEllipseCenterX = Float.parseFloat(split[i * 21 + 15].trim());
                mapMessage.LandmarkSegmentEllipseCenterY = Float.parseFloat(split[i * 21 + 16].trim());
                mapMessage.LandmarkSegmentEllipseTheta = Float.parseFloat(split[i * 21 + 17].trim());
                mapMessage.LandmarkSegmentEllipseMinT = Float.parseFloat(split[i * 21 + 18].trim());
                mapMessage.LandmarkSegmentEllipseMaxT = Float.parseFloat(split[i * 21 + 19].trim());
                mapMessage.LandmarkClass = Enum.valueOf(Enums.MapType.class, split[i * 21 + 20].trim());
                m_mapMessages.add(mapMessage);
            } catch (Exception ex) {
                count++;
            }
        }
        m_logger.debug("Failed to generate objects " + count + " times.");
    }

}
