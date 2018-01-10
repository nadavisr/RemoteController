/*
 * Created by admin on 28/09/2017
 * Last modified 10:18 28/09/17
 */

package services.properties;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.properties.</P>
 * <P>{@link LocalProperties} singleton manager, must be initialized before use!</P>
 */

public class PropertiesManager {

    private static LocalProperties m_instance;

    private static boolean m_isInitialized = false;

    @Nullable
    public static LocalProperties getInstance() {
        return m_isInitialized ? m_instance : null;
    }

    /**
     * Initialize {@link LocalProperties} instance from {@link InputStream} of "*.properties" file.
     *
     * @param inputStream Must be {@link InputStream} of "*.properties" file!
     * @throws IOException If the initializing from the received input stream failed.
     */
    public synchronized void initializeProperties(InputStream inputStream) throws IOException {

        m_isInitialized = false;

        Properties properties = new Properties();

        //Loads properties from the specified InputStream:
        properties.load(inputStream);

        m_instance = new LocalProperties(properties);

        m_isInitialized = true;
    }

}
