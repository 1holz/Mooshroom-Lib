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

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void fatal(String msg) {
        logger.fatal(msg);
    }

    public void smallBug() {
        this.warn("If you see this please report this as a bug at: " + bugTracker);
    }

    public Throwable smallBug(Throwable e) {
        smallBug();
        logger.warn("Post this error there:", e);
        return e;
    }

    public void bigBug() {
        this.error("This is a critical bug! This can lead to malfunctions!");
        this.error("Please report this as a bug at: " + bugTracker);
    }

    public Throwable bigBug(Throwable e) {
        bigBug();
        logger.error("Post this error there:", e);
        try {
            throw e;
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        return e;
    }

    public void test(String msg) {
        warn("This is a message from the developer for testing. This shouldn't be in a release version.");
        fatal(msg);
        info("at: "+ new Throwable().getStackTrace()[1]);
        smallBug();
    }

    public void wip(String feature) {
        warn(feature + " is/are in a work in progress state.");
        warn("This feature may not work properly");
    }
}