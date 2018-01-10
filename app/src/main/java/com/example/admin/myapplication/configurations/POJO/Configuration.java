/*
 * Created by admin on 28/09/2017
 * Last modified 09:10 28/09/17
 */

package com.example.admin.myapplication.configurations.POJO;

import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.google.gson.annotations.SerializedName;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.POJO.</P>
 * <P>POJO of JSON configuration file, singleton instance created by {@link ConfigurationManager}.</P>
 */

public class Configuration {

    @SerializedName("video")
    private VideoConfiguration m_videoConfiguration;

    @SerializedName("connectivity")
    private ConnectivityConfiguration m_connectivityConfiguration;

    @SerializedName("bits")
    private BitsConfiguration m_bitsConfiguration;

    @SerializedName("driving")
    private DrivingConfiguration m_drivingConfiguration;

    @SerializedName("map")
    private MapConfiguration m_mapConfiguration;

    @SerializedName("ble")
    private BleConfiguration m_bleConfiguration;

    public Configuration(VideoConfiguration videoConfiguration, ConnectivityConfiguration connectivity,
                         BitsConfiguration bitsConfiguration, DrivingConfiguration drivingConfiguration,
                         MapConfiguration mapConfiguration, BleConfiguration bleConfiguration) {
        m_videoConfiguration = videoConfiguration;
        m_connectivityConfiguration = connectivity;
        m_bitsConfiguration = bitsConfiguration;
        m_drivingConfiguration = drivingConfiguration;
        m_mapConfiguration = mapConfiguration;
        m_bleConfiguration = bleConfiguration;
    }

    public VideoConfiguration getVideo() {
        return m_videoConfiguration;
    }

    public ConnectivityConfiguration getConnectivity() {
        return m_connectivityConfiguration;
    }

    public BitsConfiguration getBits() {
        return m_bitsConfiguration;
    }

    public DrivingConfiguration getDriving() {
        return m_drivingConfiguration;
    }

    public MapConfiguration getMap() {
        return m_mapConfiguration;
    }

    public BleConfiguration getBle() {
        return m_bleConfiguration;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                m_videoConfiguration +
                ", " + m_connectivityConfiguration +
                ", " + m_bitsConfiguration +
                ", " + m_drivingConfiguration +
                ", " + m_mapConfiguration +
                ", " + m_bleConfiguration +
                '}';
    }


}