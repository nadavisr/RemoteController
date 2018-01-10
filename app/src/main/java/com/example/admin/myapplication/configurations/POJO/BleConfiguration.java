/*
 * Created by admin on 27/11/2017
 * Last modified 18:04 12/11/17
 */

/*
 * Created by admin on 28/09/2017
 * Last modified 09:54 28/09/17
 */

package com.example.admin.myapplication.configurations.POJO;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.POJO.</P>
 * <P>POJO of JSON configuration file, contained in {@link Configuration} POJO.</P>
 */
public class BleConfiguration {

    @SerializedName("service_string_uuid")
    private String m_serviceStringUuid;

    private UUID m_serviceUuid;

    @SerializedName("characteristic_echo_string_uuid")
    private String m_characteristicEchoStringUuid;

    private UUID m_characteristicEchoUuid;

    @SerializedName("scan_period_millisecond")
    private int m_scanPeriodInMillisecond;


    public BleConfiguration(String serviceStringUuid, String characteristicEchoStringUuid, int scanPeriodInMillisecond) {
        m_serviceStringUuid = serviceStringUuid;

        m_serviceUuid = m_serviceStringUuid != null ? UUID.fromString(m_serviceStringUuid) : null;

        m_characteristicEchoStringUuid = characteristicEchoStringUuid;

        m_characteristicEchoUuid = m_characteristicEchoStringUuid != null ? UUID.fromString(m_characteristicEchoStringUuid) : null;

        m_scanPeriodInMillisecond = scanPeriodInMillisecond;
    }

    public String getCharacteristicEchoStringUuid() {
        return m_characteristicEchoStringUuid;
    }

    public UUID getCharacteristicEchoUuid() {
        if (m_characteristicEchoUuid == null && m_characteristicEchoStringUuid != null && !m_characteristicEchoStringUuid.isEmpty()) {
            m_characteristicEchoUuid = UUID.fromString(m_characteristicEchoStringUuid);
        }
        return m_serviceUuid;
    }

    public String getServiceStringUuid() {
        return m_serviceStringUuid;
    }

    public UUID getServiceUuid() {
        if (m_serviceUuid == null && m_serviceStringUuid != null && !m_serviceStringUuid.isEmpty()) {
            m_serviceUuid = UUID.fromString(m_serviceStringUuid);
        }
        return m_serviceUuid;
    }

    public int getScanPeriodInMillisecond() {
        return m_scanPeriodInMillisecond;
    }

    @Override
    public String toString() {
        return "Bluetooth Low Energy Configuration:" +
                "\nScan period in milliseconds=" + m_scanPeriodInMillisecond +
                "\nService UUID=" + m_serviceStringUuid +
                "\nCharacteristic Echo UUID=" + m_characteristicEchoStringUuid;
    }
}
