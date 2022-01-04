/*
 * Copyright 2022 Mikhail Lopatkin
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

package name.mlopatkin.andlogview.device;

import name.mlopatkin.andlogview.utils.events.Observable;

import java.util.concurrent.Executor;

/**
 * Live view of the connected Android devices. Added or removed devices change the contents of the list. Iterating the
 * list creates a snapshot of currently connected devices. Use {@link AdbServer#getDeviceList(Executor)} to obtain an
 * instance of the list.
 * <p>
 * This list is not thread-safe and has to be used on the same executor that was used to obtain its instance.
 */
public interface AdbDeviceList extends Iterable<AdbDevice> {
    /**
     * The device change observer.
     */
    interface DeviceChangeObserver {
        /**
         * Called when the new device is connected.
         *
         * @param device the connected device
         */
        default void onDeviceConnected(AdbDevice device) {
        }

        /**
         * Called when the device is disconnected
         *
         * @param device the disconnected device
         */
        default void onDeviceDisconnected(AdbDevice device) {
        }

        /**
         * Called when the device status is changed.
         *
         * @param device the changed device
         */
        default void onDeviceChanged(AdbDevice device) {
        }
    }

    /**
     * @return the observable to register observers on this list
     */
    Observable<DeviceChangeObserver> asObservable();
}
