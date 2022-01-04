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
package name.mlopatkin.andlogview.liblogcat.ddmlib;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.google.common.base.Joiner;

import org.apache.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdbDeviceManager {
    private static final Logger logger = Logger.getLogger(AdbDeviceManager.class);

    private final AdbConnectionManager adbConnectionManager;

    @Inject
    public AdbDeviceManager(AdbConnectionManager adbConnectionManager) {
        this.adbConnectionManager = adbConnectionManager;
        addDeviceChangeListener(new DeviceStateLogger());
    }

    public void addDeviceChangeListener(IDeviceChangeListener listener) {
        adbConnectionManager.getAdb();
        AndroidDebugBridge.addDeviceChangeListener(listener);
    }

    public void removeDeviceChangeListener(IDeviceChangeListener listener) {
        AndroidDebugBridge.removeDeviceChangeListener(listener);
    }

    public @Nullable IDevice getDefaultDevice() {
        AndroidDebugBridge adb = adbConnectionManager.getAdb();
        if (adb.hasInitialDeviceList() && adb.getDevices().length > 0 && adb.getDevices()[0].isOnline()) {
            return adb.getDevices()[0];
        } else {
            return null;
        }
    }

    public abstract static class AbstractDeviceListener implements IDeviceChangeListener {
        @Override
        public void deviceConnected(IDevice device) {}

        @Override
        public void deviceDisconnected(IDevice device) {}

        @Override
        public void deviceChanged(IDevice device, int changeMask) {}
    }

    private static class DeviceStateLogger implements IDeviceChangeListener {
        @Override
        public void deviceConnected(IDevice device) {
            logger.debug("Device connected: " + device.getSerialNumber());
        }

        @Override
        public void deviceDisconnected(IDevice device) {
            logger.debug("Device disconnected: " + device.getSerialNumber());
        }

        @Override
        public void deviceChanged(IDevice device, int changeMask) {
            logger.debug("Device state changed: " + device.getSerialNumber());
            List<String> changes = new ArrayList<>(3);
            if ((changeMask & IDevice.CHANGE_BUILD_INFO) != 0) {
                changes.add("CHANGE_BUILD_INFO");
            }
            if ((changeMask & IDevice.CHANGE_CLIENT_LIST) != 0) {
                changes.add("CHANGE_CLIENT_LIST");
            }
            if ((changeMask & IDevice.CHANGE_STATE) != 0) {
                changes.add("CHANGE_STATE");
            }
            logger.debug(Joiner.on(" | ").join(changes));
        }
    }
}
