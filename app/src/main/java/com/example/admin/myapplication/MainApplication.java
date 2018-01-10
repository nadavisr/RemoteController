/*
 * Created by admin on  27/09/2017
 * Last modified 09:46 27/09/17
 */

package com.example.admin.myapplication;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.Environment;

import com.example.admin.myapplication.configurations.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import businessLogic.common.interfaces.ILog;
import butterknife.ButterKnife;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.</P>
 * <P></P>
 */

public class MainApplication extends Application {

    //region Statics

    private static MainApplication s_instance;

    private static String s_externalFolderPath;

    public static MainApplication getInstance() {
        return s_instance;
    }

    public static String getApplicationExternalFolderPath() {
        return s_externalFolderPath;
    }
    //endregion

    //region Fields

    private ILog m_logger;

    //endregion

    //region Application Lifecycle Methods

    @Override
    public void onCreate() {

        createApplicationFolders();

        m_logger = LogManager.getLogger();

        m_logger.verbose("in onCreate");

        s_instance = this;

        if (BuildConfig.DEBUG) {
            ButterKnife.setDebug(true);
            m_logger.debug("Butter Knife initialized in debug mode.");
        }

        initConfigurationFile();

        super.onCreate();
    }

    private void createApplicationFolders() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        String appName = getResources().getString(R.string.app_name);

        if (appName.isEmpty()) {
            appName = "Application";
        }

        String path = File.separator + appName + File.separator;
        File applicationExternalFolder = new File(externalStorageDirectory, path);

        if (applicationExternalFolder.exists()) {
            s_externalFolderPath = applicationExternalFolder.getPath();
            return;
        }

        boolean makeDirectoryResult = applicationExternalFolder.mkdir();
        if (makeDirectoryResult) {
            s_externalFolderPath = applicationExternalFolder.getPath();
        } else {
            s_externalFolderPath = externalStorageDirectory.getPath();
        }

    }

    @Override
    public void onTerminate() {
        m_logger.verbose("in onTerminate");

        LogManager.terminateLogger();

        s_instance = null;

        s_externalFolderPath = null;

        super.onTerminate();
    }

    //endregion

    //region Methods

    private void initConfigurationFile() {
        final String PATH = getString(R.string.assets_configuration_path);

        AssetManager assetManager = getAssets();

        InputStream inputStream = null;
        try {
            //Open an asset using ACCESS_STREAMING mode:
            inputStream = assetManager.open(PATH);

            ConfigurationManager.initializeConfiguration(inputStream);

            m_logger.info("ConfigurationManager initialized successfully from: " + PATH
                    + ". " + ConfigurationManager.getAllConfig());
        } catch (Exception e) {
            m_logger.fatal("Could not initialized application configuration", e);
            onTerminate();
            System.exit(1);
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            m_logger.warning("Failed to close the configuration InputStream", e);
        }
    }

    //endregion
}
