/*
 * Created by admin on 01/10/2017
 * Last modified 16:28 01/10/17
 */

package com.example.admin.myapplication.configurations.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.POJO.</P>
 * <P>POJO of JSON configuration file, contained in {@link Configuration} POJO.</P>
 */

public class BitsConfiguration {

    @SerializedName("running_frequency_millisecond")
    private short m_runningFrequency;

    public BitsConfiguration(short runningFrequencyInMillis) {
        m_runningFrequency = runningFrequencyInMillis;
    }

    public short getRunningFrequencyInMillis() {
        return m_runningFrequency;
    }

    @Override
    public String toString() {
        return "Bit Configuration: rendering frequency= " + m_runningFrequency + " millisecond.";
    }
}

