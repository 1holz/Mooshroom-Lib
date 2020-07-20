package de.alberteinholz.ehmooshroom.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerHelper {
    public Logger logger;
    public String bugTracker;

    public LoggerHelper(String bugTracker) {
        this(LogManager.getLogger(), bugTracker);
    }

    public LoggerHelper(Logger logger, String bugTracker) {
        this.logger = logger;
        this.bugTracker = bugTracker;
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void fatal(String message) {
        logger.fatal(message);
    }

    public void smallBug() {
        this.warn("If you see this please report this as a bug at: " + bugTracker);
    }

    public void smallBug(Throwable exception) {
        smallBug();
        logger.warn("Post this error there:", exception);
    }

    public void bigBug() {
        this.error("This is a critical bug! This can lead to malfunctions!");
        this.error("Please report this as a bug at: " + bugTracker);
    }

    public void bigBug(Throwable exception) {
        bigBug();
        logger.error("Post this error there:", exception);
    }

    public void test(String message) {
        warn("This is a message from the developer for testing. This shouldn't be in a release version.");
        fatal(message);
        info("at: "+ new Throwable().getStackTrace()[1]);
        smallBug();
    }

    public void wip(String feature) {
        warn(feature + " is/are in a work in progress state.");
        warn("This feature may not work properly");
    }
}