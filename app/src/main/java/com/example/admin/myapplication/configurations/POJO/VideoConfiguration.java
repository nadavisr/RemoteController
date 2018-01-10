/*
 * Created by admin on 28/09/2017
 * Last modified 09:54 28/09/17
 */

package com.example.admin.myapplication.configurations.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.POJO.</P>
 * <P>POJO of JSON configuration file, contained in {@link Configuration} POJO.</P>
 */
public class VideoConfiguration {

    @SerializedName("rendering_frequency_millisecond")
    private short m_renderFrequency;

    @SerializedName("port")
    private short m_port;

    public VideoConfiguration(short port ,short renderingFrequencyInMillis) {
        m_port=port;
        m_renderFrequency = renderingFrequencyInMillis;
    }

    public short getRenderingFrequencyInMillis() {
        return m_renderFrequency;
    }

    public short getPort() {
        return m_port;
    }

    @Override
    public String toString() {
        return "VideoConfiguration{" +
                "renderFrequency=" + m_renderFrequency +
                ", port=" + m_port +
                '}';
    }
}
