/*
 * Created by admin on 03/10/2017
 * Last modified 13:11 03/10/17
 */

package com.example.admin.myapplication.configurations.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.POJO.</P>
 * <P>POJO of JSON configuration file, contained in {@link Configuration} POJO.</P>
 */
public class DrivingConfiguration {

    @SerializedName("command_frequency_millisecond")
    private short m_commandFrequency;

    public DrivingConfiguration(short commandFrequencyInMillis) {
        m_commandFrequency = commandFrequencyInMillis;
    }

    public short getCommandFrequencyInMillis() {
        return m_commandFrequency;
    }

    @Override
    public String toString() {
        return "Driving Configuration: command frequency= " + m_commandFrequency + " millisecond.";
    }
}
