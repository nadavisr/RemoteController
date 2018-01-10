/*
 * Created by admin on 28/09/2017
 * Last modified 11:38 28/09/17
 */

package com.example.admin.myapplication.configurations;

import com.example.admin.myapplication.configurations.POJO.Configuration;
import com.google.gson.annotations.SerializedName;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.configurations.</P>
 * <P>Package-private class, used by {@link ConfigurationManager} to create {@link Configuration} instance.</P>
 */

class ConfigContainer {
    @SerializedName("configuration")
    private Configuration m_configuration;

    Configuration getConfiguration() {
        return m_configuration;
    }
}
