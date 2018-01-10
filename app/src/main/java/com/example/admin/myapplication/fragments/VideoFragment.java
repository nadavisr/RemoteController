/*
 * Created by admin on  27/09/2017
 * Last modified 10:17 27/09/17
 */

package com.example.admin.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.common.Enums.Fragments;
import com.example.admin.myapplication.driveControl.DrivingManagerJoystickView;
import com.example.admin.myapplication.map.MapGraph;
import com.example.admin.myapplication.utils.RoboPadConstants;
import com.example.admin.myapplication.video.VideoChannel;

import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import services.common.RunnableSynchronizedExecutor;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.fragments.</P>
 * <P></P>
 */

public class VideoFragment extends RobotFragment {

    //region Fields

    private ILog m_logger;

    private DrivingManagerJoystickView m_drivingManagerJoystickView;

    private VideoChannel m_videoChannel;

    private MapGraph m_mapGraph;

    private FloatingActionButton m_floatingActionButtonSetting;

    private RunnableSynchronizedExecutor m_videoChannelExecutor;

    private RunnableSynchronizedExecutor m_drivingManagerExecutor;

    private RunnableSynchronizedExecutor m_mapManagerExecutor;

    private ImageButton m_mapViewButton;

    private boolean m_visible;

    private RelativeLayout m_mapLayout;

    //endregion

    //region Constructors


    //endregion

    //region Methods

    //region Fragment Lifecycle Methods

    @Override
    public void onAttach(Context context) {
        m_logger = LogManager.getLogger();
        m_logger.verbose("in onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        m_logger.verbose("in onCreate");


        final IHandler<Exception> exceptionIHandler = new ExceptionHandler();

        m_videoChannelExecutor = new RunnableSynchronizedExecutor();
        m_videoChannelExecutor.initialize();
        m_videoChannelExecutor.setExceptionHandler(exceptionIHandler);
        m_videoChannelExecutor.start();

        m_drivingManagerExecutor = new RunnableSynchronizedExecutor();
        m_drivingManagerExecutor.initialize();
        m_drivingManagerExecutor.setExceptionHandler(exceptionIHandler);
        m_drivingManagerExecutor.start();

        m_mapManagerExecutor = new RunnableSynchronizedExecutor();
        m_mapManagerExecutor.initialize();
        m_mapManagerExecutor.setExceptionHandler(exceptionIHandler);
        m_mapManagerExecutor.start();

        Context context = getActivity();

        m_videoChannel = new VideoChannel(context);

        m_videoChannelExecutor.setInput(() -> m_videoChannel.initialize());

        m_mapGraph = new MapGraph();

        m_mapManagerExecutor.setInput(() -> m_mapGraph.initialize());

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_logger.verbose("in onCreateView");

        final View view = inflater.inflate(R.layout.fragment_video, container, false);

        initializeView(view);

        m_videoChannelExecutor.setInput(() -> {
            m_videoChannel.initializeViewFilters(view);
            m_videoChannel.start();
        });


        m_drivingManagerExecutor.setInput(() -> {
            m_drivingManagerJoystickView = new DrivingManagerJoystickView(view, listener);
            m_drivingManagerJoystickView.initialize();
            m_drivingManagerJoystickView.start();
        });

        m_mapManagerExecutor.setInput(() -> {
            m_mapGraph.initializeViewFilters(view);
            m_mapGraph.start();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        m_logger.verbose("in onDestroyView");

        m_drivingManagerExecutor.setInput(() -> m_drivingManagerJoystickView.stop());

        m_videoChannelExecutor.setInput(() -> {
            m_videoChannel.stop();
            m_videoChannel.removeViewFilter();
        });

        m_mapManagerExecutor.setInput(() -> {
            m_mapGraph.stop();
            m_mapGraph.removeViewFilter();
        });

        removeOnClickListener();

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        m_logger.verbose("in onDestroy");
        m_videoChannelExecutor.synchronizedStop(5000);
        m_videoChannelExecutor.dispose();

        m_videoChannel.dispose();

        m_drivingManagerExecutor.synchronizedStop(5000);
        m_drivingManagerExecutor.dispose();

        m_drivingManagerJoystickView.dispose();

        m_mapManagerExecutor.synchronizedStop(5000);
        m_mapManagerExecutor.dispose();

        m_mapGraph.dispose();

        super.onDestroy();
    }

    //endregion

    //region Private Methods


    private void initializeView(View view) {

        m_floatingActionButtonSetting = (FloatingActionButton) getActivity().findViewById(R.id.fab_main);
        initializeOnClickListener();

        m_mapViewButton = (ImageButton) view.findViewById(R.id.map_view_button);

        m_mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);

        setMapView(m_mapLayout);

    }

    private void setMapView(RelativeLayout mapLayout) {
        m_visible = mapLayout.getVisibility() == View.VISIBLE;

        if (m_visible) {
            m_mapViewButton.setImageResource(R.drawable.ic_close_holo_dark);
        } else {
            m_mapViewButton.setImageResource(R.drawable.ic_map_holo_dark);
        }

        m_mapViewButton.setOnClickListener(clickedView -> {
            if (m_visible) {
                m_mapGraph.stopRendering();
                m_mapLayout.setVisibility(View.INVISIBLE);
                m_mapViewButton.setImageResource(R.drawable.ic_map_holo_dark);
                m_visible = false;
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(mapLayout.getContext(), R.anim.right_to_left);
                m_mapLayout.startAnimation(fadeOutAnimation);

            } else {
                m_mapLayout.setVisibility(View.VISIBLE);
                m_mapViewButton.setImageResource(R.drawable.ic_close_holo_dark);
                m_visible = true;
                Animation fadeInAnimation = AnimationUtils.loadAnimation(mapLayout.getContext(), R.anim.left_to_right);
                m_mapLayout.startAnimation(fadeInAnimation);
                m_mapGraph.startRendering();

            }
        });
    }

    private void initializeOnClickListener() {
        if (m_floatingActionButtonSetting == null) {
            m_logger.warning("The methods initializeOnClickListener called when FloatingActionButton is null!");
            return;
        }

        removeOnClickListener();

        m_floatingActionButtonSetting.setImageResource(R.drawable.ic_settings);

        m_floatingActionButtonSetting.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            activity.setFragment(Fragments.Settings, true);
        });
    }

    private void removeOnClickListener() {
        if (m_floatingActionButtonSetting == null) {
            m_logger.warning("The methods removeOnClickListener called when FloatingActionButton is null!");
            return;
        }
        if (m_floatingActionButtonSetting.hasOnClickListeners()) {
            m_floatingActionButtonSetting.setOnClickListener(null);
        }

        m_floatingActionButtonSetting.setImageResource(0);

    }

    @Override
    protected void setUiListeners(View containerLayout) {
        listener.onSendMessage(RoboPadConstants.UP_COMMAND);
    }

    @Override
    protected void controlButtonActionDown(int viewId) {

    }

    @Override
    protected void stateChanged(RoboPadConstants.robotState nextState) {

    }

    //endregion

    //endregion

    private class ExceptionHandler implements IHandler<Exception> {

        @Override
        public void setInput(Exception e) {
            m_logger.debug("Exception received from background thread!", e);
        }
    }
}
