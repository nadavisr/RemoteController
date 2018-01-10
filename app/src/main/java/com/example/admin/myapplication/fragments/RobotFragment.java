/*
 * Created by Administrator on 27/12/2017
 * Last modified 09:54 27/12/17
 */

package com.example.admin.myapplication.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.admin.myapplication.listeners.RobotListener;
import com.example.admin.myapplication.listeners.TipsManagerListener;
import com.example.admin.myapplication.utils.RoboPadConstants;


/**
 * Base fragment for all the robot fragments
 *
 * @author Estefanía Sarasola Elvira
 *
 */

public abstract class RobotFragment extends Fragment {

    // Debugging
    private static final String LOG_TAG = "RobotFragment";

    protected boolean mIsClick;
    protected boolean mIsConnected = false;
    private boolean lastStateWasManualMode = true;

    protected RobotListener listener;

    protected RoboPadConstants.robotState state = RoboPadConstants.robotState.MANUAL_CONTROL;

    /**
     * Set the listeners to the UI views
     * @param containerLayout
     */
    protected abstract void setUiListeners (View containerLayout);


    /**
     * Send the message depending on the button pressed
     * @param viewId the id of the view pressed
     */
    protected abstract void controlButtonActionDown(int viewId);


    /**
     * The state of the robot changes. The state is the type of control the user has of the robot
     * such as manual control, or if the robot is in line follower mode
     * @param nextState next state the robot is going to have
     */
    protected abstract void stateChanged(RoboPadConstants.robotState nextState);


    /**
     * Callback method called from the activity when the Bluetooth change its status to connected
     */
    public void onBluetoothConnected() {}


    /**
     * Callback method called from the activity when the Bluetooth change its status to disconnected
     */
    public void onBluetoothDisconnected() {}


    /**
     * Set the fragmentActivity listener. Right now it is not necessary because the
     * fragment activity that contains the fragments is the one that implements the listener
     * so it is done in the onAttach of RobotFragment. But with this method can be another class
     * witch implements the listener not the container fragment activity.
     *
     * @param listener The RobotListener
     */
    public void setRobotListener(RobotListener listener) {
        this.listener = listener;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check the listener is the correct one: the fragment activity container
        // implements that listener
        if (activity instanceof RobotListener) {
            this.listener = (RobotListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement robotListener");
        }
    }


    /**
     * By default checks the preferences for the show tips. The onClickListener on mToolTipFrameLayout
     * is for show the tips until isLastTipToShow is set to true.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

/*        mToolTipFrameLayout = (ToolTipRelativeLayout) getActivity().findViewById(R.id.activity_main_tooltipframelayout);

        tipsManager = new TipsManager(getActivity(), mToolTipFrameLayout, this);
        tipsManager.initTips();*/

    }


    /**
     * Listener for the touch events. When action_down, the user is pressing the button
     * so we send the message to the arduino, and when action_up it is send a message to the arduino
     * in order to stop it.
     */
    protected OnTouchListener buttonOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final View view = v;

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                        stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                        lastStateWasManualMode = false;

                    } else {
                        lastStateWasManualMode = true;
                    }

                    if(listener != null && !listener.onCheckIsConnected()) {
                        mIsConnected = false;
                        break;
                    } else {
                        mIsConnected = true;
                    }

                    if(!lastStateWasManualMode) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initSendActionThread(view.getId());
                            }
                        }, 100);

                    } else {
                        initSendActionThread(view.getId());
                    }

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    if(!mIsConnected) {
                        break;
                    }

                    if(!lastStateWasManualMode) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsClick = true;
                                if (listener != null) {
                                    listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
                                }
                            }
                        }, 100);

                    } else {
                        mIsClick = true;
                        if (listener != null) {
                            listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
                        }
                    }

                    break;

            }

            return false;
        }

    };


    private void initSendActionThread(final int actionId) {
        mIsClick = false;
        Thread sendActionThread = createSendActionThread(actionId);
        sendActionThread.start();
    }


    /**
     * Thread to send the command but waits and send the stop command with a 130 delay
     * in case it was only a click and the arduino app didn't process the stop command
     * because of itself delays
     *
     * @param actionId the id of the view touched
     * @return Thread The thread that send the commands when pressed the corresponding buttons
     */
    private Thread createSendActionThread(final int actionId) {

        Thread sendActionThread = new Thread() {

            @Override
            public void run() {
                try {

                    if(!mIsClick) {
                        controlButtonActionDown(actionId);
                    }

                    sleep(RoboPadConstants.CLICK_SLEEP_TIME);

                    if(mIsClick && listener != null) {
                        Log.d(LOG_TAG, "stop command in thread send");
                        listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
                    }

                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "error in sendActionThread: )" + e);
                }

            }

        };

        return sendActionThread;
    }

}
