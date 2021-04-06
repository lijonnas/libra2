package com.envisioncn.gssc.libra.utils;

import java.util.Properties;

/**
 * @author jonnas
 * @date 2021-04-06
 */
public class PropertiesUtils {
    public static Properties fromCommaSeparatedString(String s) {
        Properties properties = new Properties();
        String[] entries = s.split(",");
        for (final String entry : entries) {
            if (entry.length() > 0) {
                final int index = entry.indexOf('=');
                if (index > 0) {
                    final String name = entry.substring(0, index).trim();
                    final String value = entry.substring(index + 1).trim();
                    properties.setProperty(name, value);
                } else {
                    // no value is empty string which is how java.util.Properties works
                    properties.setProperty(entry, "");
                }
            }
        }
        return properties;
    }
}
