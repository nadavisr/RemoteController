/*
 * Created by admin on 28/09/2017
 * Last modified 08:25 28/09/17
 */

package com.example.admin.myapplication.configurations;

import android.support.annotation.Nullable;

import com.example.admin.myapplication.configurations.POJO.BitsConfiguration;
import com.example.admin.myapplication.configurations.POJO.BleConfiguration;
import com.example.admin.myapplication.configurations.POJO.Configuration;
import com.example.admin.myapplication.configurations.POJO.ConnectivityConfiguration;
import com.example.admin.myapplication.configurations.POJO.DrivingConfiguration;
import com.example.admin.myapplication.configurations.POJO.MapConfiguration;
import com.example.admin.myapplication.configurations.POJO.VideoConfiguration;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.</P>
 * <P>A class which managing singleton instance of {@link Configuration}, must be initialized before use!</P>
 */

public class ConfigurationManager {

    private static Configuration m_instance = null;

    /**
     * Initialize {@link Configuration} instance from {@link InputStream} of "*.json" file.
     *
     * @param inputStream Must be {@link InputStream} of "*.json" file!
     * @throws Exception If the initializing from the received input stream failed.
     */
    public static synchronized void initializeConfiguration(InputStream inputStream) throws Exception {

        if (inputStream == null) {
            throw new NullPointerException("InputStream parameter is null!");
        }

        m_instance = null;

        InputStreamReader inputStreamReader = null;

        JsonReader reader = null;

        try {
            //Open an asset using ACCESS_STREAMING mode:
            inputStreamReader = new InputStreamReader(inputStream);

            //Read Json file:
            reader = new JsonReader(inputStreamReader);

            Gson gson = new Gson();

            //Loads m_configuration from the JsonReader:
            ConfigContainer configContainer = gson.fromJson(reader, ConfigContainer.class);

            if (configContainer.getConfiguration() == null) {
                throw new Exception("Failed to create configuration instance!");
            }

            m_instance = configContainer.getConfiguration();

        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
    }

    @Nullable
    public static Configuration getAllConfig() {
        return m_instance;
    }

    @Nullable
    public static VideoConfiguration getVideoConfiguration() {
        return m_instance != null ? m_instance.getVideo() : null;
    }

    @Nullable
    public static ConnectivityConfiguration getConnectivityConfiguration() {
        return m_instance != null ? m_instance.getConnectivity() : null;
    }

    @Nullable
    public static BitsConfiguration getBitsConfiguration() {
        return m_instance != null ? m_instance.getBits() : null;
    }

    @Nullable
    public static DrivingConfiguration getDrivingConfiguration() {
        return m_instance != null ? m_instance.getDriving() : null;
    }

    @Nullable
    public static MapConfiguration getMapConfiguration() {
        return m_instance != null ? m_instance.getMap() : null;
    }

    @Nullable
    public static BleConfiguration getBleConfiguration() {
        return m_instance != null ? m_instance.getBle() : null;
    }

    public static boolean isInitialized() {
        return m_instance != null;
    }
}
