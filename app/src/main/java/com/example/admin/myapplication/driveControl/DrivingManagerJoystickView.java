/*
 * Created by admin on 23/10/2017
 * Last modified 16:07 23/10/17
 */

package com.example.admin.myapplication.driveControl;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.common.Messages.MotionMessage;
import com.example.admin.myapplication.common.PointsCollector;
import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.DrivingConfiguration;
import com.example.admin.myapplication.driveControl.joysticks.IJoystickView;
import com.example.admin.myapplication.driveControl.joysticks.JoystickView;
import com.example.admin.myapplication.fragments.VideoFragment;
import com.example.admin.myapplication.listeners.RobotListener;
import com.example.admin.myapplication.utils.RoboPadConstants;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import businessLogic.communication.UdpPublisher;
import businessLogic.communication.interfaces.IPublisher;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.</P>
 * <P></P>
 */
public class DrivingManagerJoystickView extends ServicesBaseBgOperation {

    //region Fields

    private final View m_containedView;

    private IJoystickView m_joystick;

    private IPublisher<MotionMessage> m_drivingCommandPublisher;

    private DrivingCommandTimerTask m_drivingCommandTimerTask;

    private Timer m_drivingCommandTimer;

    private short m_publishFrequency;

    private RobotListener m_joystickListener;

    //endregion

    //region Constructors

    public DrivingManagerJoystickView(View view, RobotListener listener) {
        m_containedView = view;
        m_joystickListener = listener;
    }

    //endregion

    //region BaseBgOperation Implementation
    @Override
    protected void internalInitialize() throws Exception {
        if (m_containedView == null) {
            throw new NullPointerException("View is null");
        }

        m_joystick = (IJoystickView) m_containedView.findViewById(R.id.joystick_view);

        m_drivingCommandPublisher = new DrivingCommandPublisherMock();

//        m_drivingCommandPublisher = new DrivingCommandPublisherMock();

        m_drivingCommandPublisher = new UdpPublisher<>(m_logger, new DrivingCommandSerializer(), "172.16.10.1", (short) 8895, "DrivingCommandPublisher");
        m_drivingCommandPublisher.initialize();

        DrivingConfiguration drivingConfiguration = ConfigurationManager.getDrivingConfiguration();
        if (drivingConfiguration == null) {
            throw new Exception("DrivingConfiguration not initialized in ConfigurationManager");
        }
        m_publishFrequency = drivingConfiguration.getCommandFrequencyInMillis();
    }

    @Override
    protected void internalStart() throws Exception {

        m_drivingCommandPublisher.start();

        m_drivingCommandTimerTask = new DrivingCommandTimerTask(m_drivingCommandPublisher);

        m_drivingCommandTimer = new Timer();

        m_joystick.setOnMoveListener((JoystickView.IOnMoveListenerGetXY) (x, y) ->
                m_drivingCommandTimerTask.setData(x, y)
        );

        m_drivingCommandTimer.schedule(m_drivingCommandTimerTask, 0, m_publishFrequency);

    }

    @Override
    protected void internalStop() throws Exception {
        m_joystick.setOnMoveListener(null);

        m_drivingCommandTimerTask.cancel();

        m_drivingCommandTimer.cancel();

        m_drivingCommandTimer.purge();

        m_drivingCommandPublisher.stop();
    }

    @Override
    protected void innerDispose() throws Exception {

        m_drivingCommandPublisher.dispose();

        m_joystick.dispose();

        m_joystick = null;

    }

    //endregion

    //region Nested Classes

    /**
     * <P>Project: RemoteController.</P>
     * <P>Package: com.example.admin.myapplication.driveControl.</P>
     * <P>{@link TimerTask} that calculate the driving speed and publish that.</P>
     */
    private class DrivingCommandTimerTask extends TimerTask {

        //region Fields

        private final IPublisher<MotionMessage> m_publisher;

        private final PointsCollector m_pointsCollector;

        private final MotionMessage m_stopMotionMessage;
        //endregion

        //region Constructors

        DrivingCommandTimerTask(IPublisher<MotionMessage> publisher) {
            m_publisher = publisher;
            m_pointsCollector = new PointsCollector();
            m_stopMotionMessage = new MotionMessage();
            m_stopMotionMessage.Angle = 0;
            m_stopMotionMessage.Power = 0;
        }

        //endregion

        //region TimerTask Implementations

        @Override
        public void run() {
            int[] averagePoint = m_pointsCollector.getAverage();
            if (averagePoint == null) {
                sendMessage(m_stopMotionMessage);
                return;
            }

            int[] checkedDeadZones = m_joystick.checkDeadZones(averagePoint[0], averagePoint[1]);

            int angle = m_joystick.calculateAngleInDegrees(checkedDeadZones[0], checkedDeadZones[1]);
            int power = m_joystick.calculatePower(checkedDeadZones[0], checkedDeadZones[1]);

            if (power != 0) {
                MotionMessage motionMessage = new MotionMessage();
                motionMessage.Angle = angle;
                motionMessage.Power = power;

                if (angle < 110 && angle > 70)
                {
                    m_joystickListener.onSendMessage(RoboPadConstants.UP_COMMAND);
                }
                else if (angle < 290 && angle > 250)
                {
                    m_joystickListener.onSendMessage(RoboPadConstants.DOWN_COMMAND);
                }
                else if (angle <= 20 && angle >= 0 || angle >= 340 && angle <= 360)
                {
                    m_joystickListener.onSendMessage(RoboPadConstants.RIGHT_COMMAND);
                }
                else if (angle <= 200 && angle >= 160)
                {
                    m_joystickListener.onSendMessage(RoboPadConstants.LEFT_COMMAND);
                }
                else
                    m_joystickListener.onSendMessage(RoboPadConstants.STOP_COMMAND);

                Log.w("Joystick123", "angle:" +angle+ "power:" +power);

                //sendMessage(motionMessage);
                //Toast.makeText(m_containedView.getContext(),angle + ", " + power, Toast.LENGTH_SHORT).show();


            } else {
                //sendMessage(m_stopMotionMessage);
                //Toast.makeText(m_containedView.getContext(),"Stop", Toast.LENGTH_SHORT).show();
                Log.w("Joystick123", "angle:" +angle+ "power:" +power);

                m_joystickListener.onSendMessage(RoboPadConstants.STOP_COMMAND);
            }

        }

        public void setData(int x, int y) {
            m_pointsCollector.addPoint(x, y);
        }

        private void sendMessage(MotionMessage message) {
/*            try {
                m_publisher.publish(message);
            } catch (IOException e) {
                m_logger.warning("Publish of driving command failed.", e);
            }*/
        }
        //endregion
    }

    //endregion
}

