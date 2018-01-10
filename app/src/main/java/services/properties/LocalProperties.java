/*
 * Created by admin on 28/09/2017
 * Last modified 10:33 28/09/17
 */

package services.properties;

import java.util.Properties;

import javassist.NotFoundException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.properties.</P>
 * <P>{@link Properties} container, instance created by {@link PropertiesManager}.</P>
 */

public class LocalProperties {

    private Properties m_properties;

    LocalProperties(Properties properties) {
        if (properties == null)
            throw new NullPointerException("LocalProperties parameter is null!");
        m_properties = properties;
    }

    /**
     * @param key Property key.
     * @return The value associated with the received key.
     * @throws NullPointerException If the key is null.
     * @throws NotFoundException    If the received key is not found in the properties.
     */
    public String getValue(String key) throws NullPointerException, NotFoundException {
        if (key == null) {
            throw new NullPointerException("Key parameter is null");
        }
        String value = (String) m_properties.get(key);
        if (value == null) {
            throw new NotFoundException("The key: \"" + key + "\", not found in the configuration.");
        }
        return value;
    }
}
