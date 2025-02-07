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

import name.mlopatkin.andlogview.device.AdbDevice;
import name.mlopatkin.andlogview.device.Command;
import name.mlopatkin.andlogview.device.DeviceGoneException;
import name.mlopatkin.andlogview.liblogcat.LogRecord;
import name.mlopatkin.andlogview.liblogcat.LogRecordParser;
import name.mlopatkin.andlogview.utils.Threads;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class retrieves log records from the device using a background thread
 * and pushes them back to creator.
 */
class AdbBuffer {
    public interface BufferReceiver {
        void pushRecord(LogRecord record);
    }

    private static final Logger logger = Logger.getLogger(AdbBuffer.class);

    private final BufferReceiver receiver;
    private final LogRecord.Buffer buffer;
    private final Map<Integer, String> pidToProcess;
    private final ExecutorService executorService;
    private final Command command;

    public AdbBuffer(BufferReceiver receiver, AdbDevice device, LogRecord.Buffer buffer, List<String> commandLine,
            Map<Integer, String> pidToProcess) {
        this.receiver = receiver;
        this.buffer = buffer;
        this.pidToProcess = pidToProcess;
        this.executorService = Executors.newSingleThreadExecutor(
                Threads.withName(String.format("logcat-%s-%s", buffer, device.getSerialNumber())));
        this.command = device.command(commandLine);
    }

    public void start() {
        executorService.execute(this::executeCommand);
    }

    public void close() {
        executorService.shutdownNow();
    }

    private void executeCommand() {
        try {
            command.executeStreaming(line -> {
                LogRecord record = LogRecordParser.parseThreadTime(buffer, line, pidToProcess);
                if (record != null) {
                    receiver.pushRecord(record);
                } else {
                    logger.debug("Non-parsed line: " + line);
                }
            });
            if (Thread.currentThread().isInterrupted()) {
                logger.debug("cancelled because of interruption, stopping providing new lines");
            } else {
                // TODO(mlopatkin) if we get there without interruption then logcat has died unexpectedly. This should
                //  be handled somehow because it is not that different from device disconnect. In theory it should be
                //  possible even to recover, e.g. by restarting and waiting for the last read line to come.
                logger.error("logcat streaming completed on its own");
            }
        } catch (InterruptedException e) {
            logger.debug("interrupted, stopping providing new lines");
            Thread.currentThread().interrupt();
        } catch (DeviceGoneException | IOException e) {
            logger.error("Device is gone, closing", e);
        }
    }
}
