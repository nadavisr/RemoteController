

package com.example.admin.myapplication.map.executors;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.map.DrawableMap;
import com.example.admin.myapplication.map.MapView;
import com.example.admin.myapplication.map.drawables.IMapDrawable;
import com.example.admin.myapplication.map.drawables.MapCursor;

import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.IRendering;
import businessLogic.graph.executors.interfaces.IRenderExecutor;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map</P>
 * <P>A map graph render, that receive map data and push it to the map viewer.</P>
 */

public class MapViewExecutor extends ServicesBaseBgOperation implements IRenderExecutor<MapData>, IRendering,
        SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    //region Fields

    private final View m_containedView;

    private short m_renderingFrequency;

    private MapView m_mapView;

    private SeekBar m_zoomSeekBar;

    private boolean m_originShown;

    private ImageButton m_originButton;

    private ImageButton m_cursorTrackingButton;

    private DrawableMap m_drawableMap;

    private IHandler<Exception> m_exceptionHandler;

    float m_lastAlphaSeekBar = 1.0f;

    //endregion

    //region Constructors

    public MapViewExecutor(View view, short renderingFrequencyInMillis) {
        m_containedView = view;
        m_renderingFrequency = renderingFrequencyInMillis;
    }

    //endregion

    //region Methods

    //region BaseBgOperation Implementation

    @Override
    protected void internalInitialize() throws Exception {
        if (m_containedView == null) {
            throw new NullPointerException("View is null");
        }
        if (m_renderingFrequency <= 0) {
            throw new NullPointerException("Rendering Frequency is negative or zero.");
        }

        m_mapView = (MapView) m_containedView.findViewById(R.id.map_view);

        m_zoomSeekBar = (SeekBar) m_containedView.findViewById(R.id.map_seek_bar);

        m_zoomSeekBar.setOnSeekBarChangeListener(this);

        m_originShown = false;

        m_originButton = (ImageButton) m_containedView.findViewById(R.id.btn_display_origin);

        m_cursorTrackingButton = (ImageButton) m_containedView.findViewById(R.id.btn_tracking_cursor);
        m_originButton.setOnClickListener(this);
        m_cursorTrackingButton.setOnClickListener(this);
    }

    @Override
    protected void internalStart() throws Exception {

        m_drawableMap = m_mapView.getDrawableMap();

        m_mapView.startRenderingMap(m_renderingFrequency);
    }

    @Override
    protected void internalStop() throws Exception {
        m_mapView.stopRenderingMap();
    }

    @Override
    protected void innerDispose() throws Exception {
        m_zoomSeekBar.setOnSeekBarChangeListener(null);
        m_drawableMap.purgeMap();
        m_drawableMap = null;
        m_mapView = null;
    }

    //endregion

    //region IRenderExecutor<IMapDrawable> Implementation

    @Override
    public void setInput(MapData mapData) {
        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        MapCursor mapCursor = mapData.getMapCursor();
        if (mapCursor != null) {
            PointF center = mapCursor.getCenterVertex();
            m_drawableMap.setCursor(center.x, center.y, mapCursor.getRotateAngleInDegrees());
        }

        IMapDrawable mapDrawable = mapData.getMapDrawable();
        if (mapDrawable == null) {
            return;
        }
        switch (mapData.getMapOperation()) {
            case DOP_DELETE:
                m_drawableMap.removeDrawable(mapDrawable);
                break;
            case DOP_ADD:
            case DOP_UPDATE:
                m_drawableMap.addDrawable(mapDrawable);
                break;
            default:
                break;
        }
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    //endregion

    //region SeekBar.OnSeekBarChangeListener Implementation

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        m_zoomSeekBar.setAlpha(m_lastAlphaSeekBar);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        m_lastAlphaSeekBar = seekBar.getAlpha();
        m_zoomSeekBar.setAlpha(1.0f);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (m_mapView == null) {
            return;
        }
        float progressPercent = 1.0f + progress * 0.2f;
        m_mapView.setZoom(progressPercent);

        if (m_mapView.getZoom() != MapView.DEFAULT_ZOOM) {
            m_cursorTrackingButton.setVisibility(View.VISIBLE);
        } else {
            m_cursorTrackingButton.setVisibility(View.INVISIBLE);
            m_cursorTrackingButton.setImageResource(R.drawable.ic_visibility_holo_dark);
        }

    }

    //endregion

    //region  View.OnClick Implementation

    @Override
    public void onClick(View v) {
        if (v == m_originButton) {
            if (m_drawableMap == null) {
                return;
            }
            if (m_originShown) {
                m_drawableMap.removeOrigin();
                m_originButton.setImageResource(R.drawable.ic_my_location_holo_dark);
                m_originShown = false;
            } else {
                m_drawableMap.addOrigin(Color.RED);
                m_originButton.setImageResource(R.drawable.ic_my_location_red);
                m_originShown = true;
            }
            m_mapView.setZoom(MapView.DEFAULT_ZOOM);
            m_zoomSeekBar.setProgress(0, true);
            m_cursorTrackingButton.setVisibility(View.INVISIBLE);
            m_cursorTrackingButton.setImageResource(R.drawable.ic_visibility_holo_dark);

        } else {
            if (m_mapView == null) {
                return;
            }
            if (m_mapView.isCursorTracing()) {
                m_mapView.setCursorTracing(false);
                m_cursorTrackingButton.setImageResource(R.drawable.ic_visibility_holo_dark);
            } else {
                m_mapView.setCursorTracing(true);
                m_cursorTrackingButton.setImageResource(R.drawable.ic_visibility_red);
            }
        }
    }

    //endregion

    //region IRendering Implementation

    @Override
    public boolean startRendering() {
        if (m_mapView == null) {
            return false;
        }
        if (m_mapView.startRenderingMap(m_renderingFrequency)) {
            m_logger.info("Map start rendering every " + m_renderingFrequency + " milliseconds.");
            return true;
        }
        m_logger.warning("Map failed on try to start rendering.");
        return false;
    }

    @Override
    public void stopRendering() {
        if (m_mapView != null) {
            m_mapView.stopRenderingMap();
            m_logger.info("Map rendering stopped.");
        }
    }

    //endregion

    //endregion
}
