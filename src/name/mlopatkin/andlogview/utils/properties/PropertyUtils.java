/*
 * Copyright 2011 Mikhail Lopatkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.mlopatkin.andlogview.utils.properties;

import name.mlopatkin.andlogview.Main;
import name.mlopatkin.andlogview.thirdparty.systemutils.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {
    private PropertyUtils() {}

    public static Properties getPropertiesFromStream(InputStream stream) throws IOException {
        Properties props = new Properties();
        props.load(stream);
        return props;
    }

    public static Properties getPropertiesFromResources(String resourceName) {
        // TODO(mlopatkin) remove reference to Main.class here
        InputStream resourceStream = Main.class.getResourceAsStream(resourceName);
        if (resourceStream == null) {
            throw new IllegalArgumentException("Couldn't load resource " + resourceName);
        }
        try {
            try {
                return getPropertiesFromStream(resourceStream);
            } finally {
                resourceStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while loading resource", e);
        }
    }

    public static void loadValuesFromStream(ConfigurationMap cfg, InputStream stream) throws IOException {
        cfg.assign(getPropertiesFromStream(stream));
    }

    public static void loadValuesFromResource(ConfigurationMap cfg, String resourceName) {
        cfg.assign(getPropertiesFromResources(resourceName));
    }

    public static File getSystemConfigDir() {
        if (SystemUtils.IS_OS_WINDOWS) {
            String appdata = System.getenv("APPDATA");
            return new File(appdata);
        } else {
            return new File(SystemUtils.USER_HOME);
        }
    }

    public static File getAppConfigDir(String shortAppName) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(getSystemConfigDir(), shortAppName);
        } else {
            return new File(getSystemConfigDir(), "." + shortAppName);
        }
    }
}
