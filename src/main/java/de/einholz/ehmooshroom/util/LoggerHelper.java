package de.einholz.ehmooshroom.util;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO redo
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
        logger.trace(concatObj(objs));
    }

    public void debug(Object... objs) {
        logger.debug(concatObj(objs));
    }

    public void info(Object... objs) {
        logger.info(concatObj(objs));
    }

    public void warn(Object... objs) {
        logger.warn(concatObj(objs));
    }

    public void error(Object... objs) {
        logger.error(concatObj(objs));
    }

    public void fatal(Object... objs) {
        logger.fatal(concatObj(objs));
    }

    public void smallBug() {
        warn("at: "+ new Throwable().getStackTrace()[1]);
        warn("If you see this please report this as a bug at:", bugTracker);
    }

    public void smallBug(Object... objs) {
        smallBug();
        warn("Post this error here:", objs[0]);
        warn(Arrays.copyOfRange(objs, 1, objs.length));
    }

    public Throwable smallBug(Throwable e, String... strs) {
        smallBug(e.getLocalizedMessage(), strs);
        for (StackTraceElement stackTrace : e.getStackTrace()) debug(stackTrace);
        return e;
    }

    public void bigBug() {
        bigBug(0);
    }

    public void bigBug(int add) {
        error("This is a critical bug! This can lead to malfunctions!");
        error("at: "+ new Throwable().getStackTrace()[1 + add]);
        error("Please report this as a bug at:", bugTracker);
    }

    public Throwable bigBug(Throwable e) {
        bigBug(1);
        e.printStackTrace();
        return e;
    }

    public void test(String msg) {
        warn("This is a message from the developer for testing. This should not be in a release version");
        fatal(msg);
        info("at: "+ new Throwable().getStackTrace()[1]);
        smallBug();
    }

    public void wip(String feature) {
        warn(feature, "is/are in a work in progress state and may not work properly");
    }

    protected static String concatObj(Object... objs) {
        String msg = "";
        for (Object obj : objs) msg = msg + " " + obj;
        return msg;
    }
}
