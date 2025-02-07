/*
 * Copyright 2017 Mikhail Lopatkin
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

package name.mlopatkin.andlogview.ui.device;

import name.mlopatkin.andlogview.device.AdbDevice;
import name.mlopatkin.andlogview.device.AdbDeviceList;
import name.mlopatkin.andlogview.device.DeviceChangeObserver;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

class DeviceListModel extends AbstractListModel<AdbDevice> implements DeviceChangeObserver {
    private static final Logger logger = Logger.getLogger(DeviceListModel.class);

    private final AdbDeviceList adbDeviceList;
    private final List<AdbDevice> devices;

    private DeviceListModel(AdbDeviceList adbDeviceList) {
        this.adbDeviceList = adbDeviceList;
        devices = new ArrayList<>(adbDeviceList.getDevices());
    }

    @Override
    public int getSize() {
        return devices.size();
    }

    @Override
    public AdbDevice getElementAt(int index) {
        return devices.get(index);
    }

    @Override
    public void onDeviceConnected(AdbDevice device) {
        logger.debug("device added " + device);
        devices.add(device);
        fireIntervalAdded(this, devices.size() - 1, devices.size() - 1);
    }

    @Override
    public void onDeviceDisconnected(AdbDevice device) {
        logger.debug("device removed " + device);
        int index = devices.indexOf(device);
        if (index >= 0) {
            devices.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    @Override
    public void onDeviceChanged(AdbDevice device) {
        logger.debug("Device changed: " + device);
        fireContentsChanged(DeviceListModel.this, 0, devices.size() - 1);
    }

    public int getFirstOnlineDeviceIndex() {
        for (int i = 0; i < devices.size(); ++i) {
            AdbDevice device = devices.get(i);
            if (device.isOnline()) {
                return i;
            }
        }
        return -1;
    }

    public void unsubscribe() {
        adbDeviceList.asObservable().removeObserver(this);
    }

    public static DeviceListModel create(AdbDeviceList adbDeviceList) {
        DeviceListModel model = new DeviceListModel(adbDeviceList);
        adbDeviceList.asObservable().addObserver(model);
        return model;
    }
}
