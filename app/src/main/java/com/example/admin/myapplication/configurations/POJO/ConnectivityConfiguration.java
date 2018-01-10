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
public class ConnectivityConfiguration {

    @SerializedName("ip")
    private String m_ip;

    public ConnectivityConfiguration(String ip) {
        m_ip = ip;
    }

    public String getIp() {
        return m_ip;
    }

    @Override
    public String toString() {
        return "ConnectivityConfiguration{" +
                "ip='" + m_ip + '\'' +
                '}';
    }
}
