/*
 * Created by admin on 04/12/2017
 * Last modified 11:33 04/12/17
 */

package com.example.admin.myapplication.map.executors;

import com.example.admin.myapplication.common.Enums;
import com.example.admin.myapplication.map.drawables.IMapDrawable;
import com.example.admin.myapplication.map.drawables.MapCursor;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.map.executors.</P>
 * <P>A type that transferred in the map graph.</P>
 */

public class MapData {

    private MapCursor m_mapCursor;
    private IMapDrawable m_mapDrawable;
    private Enums.MapOperation m_mapOperation;
    private Enums.MapType m_mapType;

    public MapData(MapCursor mapCursor, IMapDrawable mapDrawable, Enums.MapOperation mapOperation, Enums.MapType mapType) {
        m_mapCursor = mapCursor;
        m_mapDrawable = mapDrawable;
        m_mapOperation = mapOperation;
        m_mapType = mapType;
    }

    public MapData() {
        m_mapCursor = null;
        m_mapDrawable = null;
        m_mapOperation = Enums.MapOperation.DOP_NONE;
        m_mapType = Enums.MapType.DCL_NONE;
    }

    public IMapDrawable getMapDrawable() {
        return m_mapDrawable;
    }

    public Enums.MapOperation getMapOperation() {
        return m_mapOperation;
    }

    public Enums.MapType getMapType() {
        return m_mapType;
    }

    public MapCursor getMapCursor() {
        return m_mapCursor;
    }

    public void setMapCursor(MapCursor mapCursor) {
        m_mapCursor = mapCursor;
    }

    public void setMapDrawable(IMapDrawable mapDrawable,Enums.MapOperation mapOperation,Enums.MapType mapType){
        m_mapDrawable = mapDrawable;
        m_mapOperation = mapOperation;
        m_mapType = mapType;
    }

    public boolean isEmpty() {
        return m_mapCursor == null && m_mapDrawable == null;
    }
}
