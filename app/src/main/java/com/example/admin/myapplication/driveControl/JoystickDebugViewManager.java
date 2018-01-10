/*
 * Created by admin on 12/11/2017
 * Last modified 10:54 12/11/17
 */

package com.example.admin.myapplication.driveControl;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.example.admin.myapplication.R;

import businessLogic.common.exceptions.NotInitializedException;
import businessLogic.common.interfaces.IDisposable;
import businessLogic.common.interfaces.IInitializable;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.</P>
 * <P></P>
 */


public class JoystickDebugViewManager implements IInitializable, IDisposable {

    //region Fields

    private final View m_containedView;

    private JoystickDebugView m_joystickDebugView;

    private ImageButton m_joystickDebugViewButton;

    private boolean m_visible;

    private boolean m_disposed;

    private boolean m_initialized;
    //endregion

    //region Constructors

    public JoystickDebugViewManager(View view) {
        m_containedView = view;
    }

    //endregion

    //region BaseBgOperation Implementation

    @Override
    public void dispose() {
        if (m_disposed) {
            return;
        }
        m_joystickDebugView = null;

        m_joystickDebugViewButton = null;

        m_initialized = false;

        m_disposed = true;
    }

    @Override
    public void initialize() {
        if (m_initialized) {
            return;
        }
        if (m_containedView == null) {
            throw new NullPointerException("View is null");
        }

//        m_joystickDebugViewButton = (ImageButton) m_containedView.findViewById(R.id.joystick_view_debug_button);
//
//        m_joystickDebugView = (JoystickDebugView) m_containedView.findViewById(R.id.joystick_view_debug);

        m_visible = m_joystickDebugView.getVisibility() == View.VISIBLE;

        if (m_visible) {
            m_joystickDebugViewButton.setImageResource(R.drawable.ic_close_holo_dark);
        } else {
            m_joystickDebugViewButton.setImageResource(R.drawable.ic_adb_holo_dark);
        }

        m_joystickDebugViewButton.setOnClickListener(v -> {
            if (m_visible) {
                m_joystickDebugView.setVisibility(View.INVISIBLE);
                m_joystickDebugViewButton.setImageResource(R.drawable.ic_adb_holo_dark);
                m_joystickDebugView.resetView();
                m_visible = false;

                Animation fadeOutAnimation = AnimationUtils.loadAnimation(m_joystickDebugView.getContext(), R.anim.right_to_left);
                m_joystickDebugView.startAnimation(fadeOutAnimation);
            } else {
                m_joystickDebugView.setVisibility(View.VISIBLE);
                m_joystickDebugViewButton.setImageResource(R.drawable.ic_close_holo_dark);
                m_visible = true;

                Animation fadeInAnimation = AnimationUtils.loadAnimation(m_joystickDebugView.getContext(), R.anim.left_to_right);
                m_joystickDebugView.startAnimation(fadeInAnimation);
            }
        });

        m_initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return !m_disposed && m_initialized;
    }

    public void setVelocity(int x, int y) throws NotInitializedException {
        if (!m_initialized) {
            throw new NotInitializedException(JoystickDebugViewManager.class);
        }
        if (m_visible) {
            m_joystickDebugView.setVelocity(x, y);
        }
    }

    public void stop() throws NotInitializedException {
        setVelocity(0, 0);
    }

    public void resetView() throws NotInitializedException {
        m_joystickDebugView.resetView();
    }
    //endregion
}
