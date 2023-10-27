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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Prints a message indicating that a small bug occured which will like be
     * corrected/compensated for automatically. Also adds the bug tracker to report
     * it at and if {@code trace} where it occured.
     *
     * @param nestingDepth how many entries before the caller of this methode in the
     *                     stack trace the bug occured
     * @param trace        whether the location where the bug occured should be
     *                     printed
     */
    public void warnBugNested(int nestingDepth, boolean trace) {
        warnRaw("A small bug occured");
        if (trace)
            warnRaw("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
        warnRaw("Please report this bug at:", bugTracker);
    }

    /**
     * Prints a message indicating that a small bug occured which will like be
     * corrected/compensated for automatically. Also adds the bug tracker to report
     * it at and where it occured.
     */
    public void warnBug() {
        warnBugNested(1, true);
    }

    /**
     * Prints a message indicating that a small bug occured which will like be
     * corrected/compensated for automatically. Also adds an error message, the bug
     * tracker to report it at and where it occured.
     *
     * @param nestingDepth how many entries before the caller of this methode in the
     *                     stack trace the bug occured
     * @param msg          the error message that is printed
     * @param objs         various objects which are printed to provide further
     *                     information
     */
    public void warnBugNested(int nestingDepth, String msg, Object... objs) {
        warnBugNested(1 + nestingDepth, true);
        warnRaw("Post this error there:", msg);
        if (objs.length > 0)
            warnRaw(objs);
    }

    /**
     * Prints a message indicating that a small bug occured which will like be
     * corrected/compensated for automatically. Also adds the bug tracker to report
     * it at and where it occured.
     *
     * @param objs various objects which are printed to provide further information
     */
    public void warnBug(Object... objs) {
        warnBug(1, (Object[]) objs);
    }

    /**
     * Prints a message indicating that a critical bug occured which will like lead
     * to malfunctions. Also adds the bug tracker to report it at and if
     * {@code trace} where it occured.
     *
     * @param nestingDepth how many entries before the caller of this methode in the
     *                     stack trace the bug occured
     * @param trace        whether the location where the bug occured should be
     *                     printed
     */
    public void errorBugNested(int nestingDepth, boolean trace) {
        errorRaw("This is a critical bug! This can lead to malfunctions!");
        if (trace)
            errorRaw("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
        errorRaw("Please report this bug at:", bugTracker);
    }

    /**
     * Prints a message indicating that a critical bug occured which will like lead
     * to malfunctions. Also adds the bug tracker to report it at and where it
     * occured.
     */
    public void errorBug() {
        errorBugNested(1, true);
    }

    /**
     * Prints a message indicating that a critical bug occured which will like lead
     * to malfunctions. Also adds an error message, a {@link Throwable} and the bug
     * tracker to report it at.
     *
     * @param nestingDepth how many entries before the caller of this methode in the
     *                     stack trace the bug occured
     * @param msg          the error message that is printed
     * @param e            a {@link Throwable} that will be printed
     * @return e
     */
    public Throwable errorBugNested(int nestingDepth, String msg, Throwable e) {
        errorBugNested(1 + nestingDepth, false);
        logger.error(msg, e);
        return e;
    }

    /**
     * Prints a message indicating that a critical bug occured which will like lead
     * to malfunctions. Also adds an error message, a {@link Throwable} and the bug
     * tracker to report it at.
     *
     * @param msg the error message that is printed
     * @param e   a {@link Throwable} that will be printed
     * @return e
     */
    public Throwable errorBug(String msg, Throwable e) {
        return errorBugNested(1, msg, e);
    }

    /**
     * Prints a message indicating that a fatal bug occured and will create a
     * {@link CrashException} and crash the game. Also adds an error message, a
     * {@link Throwable} and the bug tracker to report it at. If {@code e} already
     * is a {@link CrashException} it will be used for the crash report.
     *
     * @param msg the error message that is printed
     * @param e   a {@link Throwable} that will be printed
     * @return e
     * @throws CrashException
     */
    public Throwable fatalBug(@Nullable String msg, Throwable e) throws CrashException {
        fatalRaw("This is a fatal bug! This will lead to a crash!");
        fatalRaw("Please report this bug at:", bugTracker);
        if (e instanceof CrashException ce)
            throw ce;
        if (msg == null)
            msg = "No titel available!";
        CrashReport report = CrashReport.create(e, msg);
        throw new CrashException(report);
    }

    /**
     * Logs a message indicating that {@code msg} is printed for testing/debuging
     * purposes. It also logs the location from where the message originates.
     * If {@code suppress == true} this will do nothing except this is in a
     * development enviroment.
     *
     * @param nestingDepth How much the origination location should move up the
     *                     stack trace. (usually 0)
     * @param suppress     Whether this should log nothing except in a development
     *                     enviroment.
     * @param msg          The message that will be logged.
     */
    public void test(int nestingDepth, boolean suppress, String msg) {
        if (suppress && !FabricLoader.getInstance().isDevelopmentEnvironment())
            return;
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            warnRaw("This is a message from the developer for testing. This should not be in a release version!");
            warnRaw("Please report this bug at:", bugTracker);
        }
        fatalRaw(msg);
        infoRaw("at:", new Throwable().getStackTrace()[1 + nestingDepth]);
    }

    /**
     * Logs a message indicating that {@code msg} is printed for testing/debuging
     * purposes. It also logs the location from where the message originates.
     *
     * @param msg The message that will be logged.
     */
    public void test(String msg) {
        test(1, false, msg);
    }

    /**
     * Logs a message that indicates that a given feature is work in progress.
     *
     * @param feature Name of the feature
     */
    public void wip(String feature) {
        warnRaw(feature, "is/are in a work in progress state and may not work properly");
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as trace.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void traceRaw(Object... objs) {
        logger.trace(concatObjToString(objs));
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as debug.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void debugRaw(Object... objs) {
        logger.debug(concatObjToString(objs));
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as info.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void infoRaw(Object... objs) {
        logger.info(concatObjToString(objs));
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as warn.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void warnRaw(Object... objs) {
        logger.warn(concatObjToString(objs));
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as error.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void errorRaw(Object... objs) {
        logger.error(concatObjToString(objs));
    }

    /**
     * Concats {@code objs} to a {@link String} and logs it as fatal.
     * Consider using one of the other methodes in this class.
     *
     * @param objs The concatenated {@link Object}s
     */
    public void fatalRaw(Object... objs) {
        logger.fatal(concatObjToString(objs));
    }

    private static String concatObjToString(Object... objs) {
        String msg = "";
        for (Object obj : objs)
            msg = msg + " " + obj;
        return msg;
    }
}
