/*
 * Copyright 2023 Einholz
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

package de.einholz.ehmooshroom.util;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public class LoggerHelper {
    protected Logger logger;
    protected String bugTracker;

    public LoggerHelper(String name, String bugTracker) {
        this(LogManager.getLogger(name), bugTracker);
    }

    public LoggerHelper(Logger logger, String bugTracker) {
        this.logger = logger;
        this.bugTracker = bugTracker;
    }

    public void trace(Object... objs) {
        logger.trace(concatObjToString(objs));
    }

    public void debug(Object... objs) {
        logger.debug(concatObjToString(objs));
    }

    public void info(Object... objs) {
        logger.info(concatObjToString(objs));
    }

    public void warn(Object... objs) {
        logger.warn(concatObjToString(objs));
    }

    public void error(Object... objs) {
        logger.error(concatObjToString(objs));
    }

    public void fatal(Object... objs) {
        logger.fatal(concatObjToString(objs));
    }

    public void warnBug(int nestingDepth, boolean trace) {
        warn("A small bug occured");
        if (trace)
            warn("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
        warn("Please report this bug at:", bugTracker);
    }

    public void warnBug() {
        warnBug(1, true);
    }

    public void warnBug(int nestingDepth, Object... objs) {
        warnBug(1 + nestingDepth, true);
        warn("Post this error there:", objs[0]);
        warn(Arrays.copyOfRange(objs, 1, objs.length));
    }

    // ideally would also use Object... but that would cause collisions
    public void warnBug(String... strs) {
        warnBug(1, (Object[]) strs);
    }

    public void errorBug(int nestingDepth, boolean trace) {
        error("This is a critical bug! This can lead to malfunctions!");
        if (trace)
            error("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
        error("Please report this bug at:", bugTracker);
    }

    public void errorBug() {
        errorBug(1, true);
    }

    public Throwable errorBug(int nestingDepth, String msg, Throwable e) {
        errorBug(1 + nestingDepth, false);
        logger.error(msg, e);
        return e;
    }

    public Throwable errorBug(String msg, Throwable e) {
        return errorBug(1, msg, e);
    }

    public Throwable fatalBug(String msg, Throwable e) throws CrashException {
        fatal("This is a fatal bug! This will lead to a crash!");
        fatal("Please report this bug at:", bugTracker);
        if (e instanceof CrashException ce)
            throw ce;
        if (msg == null)
            msg = "No titel available!";
        CrashReport report = CrashReport.create(e, msg);
        throw new CrashException(report);
    }

    public void test(int nestingDepth, boolean suppress, String msg) {
        if (suppress && !FabricLoader.getInstance().isDevelopmentEnvironment())
            return;
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            warn("This is a message from the developer for testing. This should not be in a release version!");
            warn("Please report this bug at:", bugTracker);
        }
        fatal(msg);
        info("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
    }

    public void test(String msg) {
        test(1, false, msg);
    }

    public void wip(String feature) {
        warn(feature, "is/are in a work in progress state and may not work properly");
    }

    private static String concatObjToString(Object... objs) {
        String msg = "";
        for (Object obj : objs)
            msg = msg + " " + obj;
        return msg;
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public void smallBug() {
        warnBug(1);
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public void smallBug(Object... objs) {
        warnBug(1, objs);
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public Throwable smallBug(Throwable e, String... strs) {
        warnBug(1, e, strs);
        /*
         * smallBug(e.getLocalizedMessage(), strs);
         * for (StackTraceElement stackTrace : e.getStackTrace())
         * debug(stackTrace);
         */
        return e;
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public void bigBug() {
        errorBug(1, true);
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public void bigBug(int add) {
        errorBug(1 + add, true);
    }

    @Deprecated(since = "0.0.6", forRemoval = true)
    public Throwable bigBug(Throwable e) {
        bigBug(1);
        e.printStackTrace();
        return e;
    }
}
