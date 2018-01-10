/*
 * Created by admin on 03/10/2017
 * Last modified 12:58 03/10/17
 */

package com.example.admin.myapplication.driveControl;

import com.example.admin.myapplication.common.Messages;

import java.io.IOException;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.ILog;
import businessLogic.communication.interfaces.IPublisher;
import services.logging.LogManager;

class DrivingCommandPublisherMock implements IPublisher<Messages.MotionMessage> {

    private final ILog m_log = LogManager.getLogger();

    @Override
    public void dispose() {
        m_log.debug("in dispose");
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public BgOperationState getState() {
        return null;
    }

    @Override
    public ILog getLogger() {
        return null;
    }

    @Override
    public void initialize() {
        m_log.debug("in initialize");
    }

    @Override
    public void start() {
        m_log.debug("in start");
    }

    @Override
    public void stop() {
        m_log.debug("in stop");
    }

    @Override
    public void publish(Messages.MotionMessage motionMessage) throws IOException {
       // m_log.debug("Power = " + motionMessage.Power + ", Angle = " + motionMessage.Angle);
    }
}
