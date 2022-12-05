package de.einholz.ehmooshroom.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        warn("If you see this please report this as a bug at:", bugTracker);
    }

    public Throwable smallBug(Throwable e) {
        smallBug();
        logger.warn("Post this error here:", e);
        for (StackTraceElement stackTrace : e.getStackTrace()) debug(stackTrace);
        return e;
    }

    public void bigBug() {
        error("This is a critical bug! This can lead to malfunctions!");
        error("Please report this as a bug at:", bugTracker);
    }

    public Throwable bigBug(Throwable e) {
        bigBug();
        try {
            throw e;
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
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

    protected String concatObj(Object... objs) {
        String msg = "";
        for (Object obj : objs) msg = msg + " " + obj;
        return msg;
    }
}
