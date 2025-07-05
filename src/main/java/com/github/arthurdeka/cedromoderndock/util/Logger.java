package com.github.arthurdeka.cedromoderndock.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Final utility class for application logging.
 * Provides static methods for informational and error logs.
 */
public final class Logger {

    private static final String LOG_FILE_NAME = "log.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor to prevent this utility class from being instantiated.
    private Logger() {
    }

    /**
     * Logs an informational message to the console.
     * The format includes the [INFO] log level.
     */
    public static void info(String message) {
        System.out.println(String.format("[INFO] - " + message));
    }

    /**
     * Logs an error message to a log file (log.txt).
     * Messages are appended to the end of the file.
     */
    public static void error(String message) {
        String timestamp = dtf.format(LocalDateTime.now());
        String logMessage = String.format("[%s] [ERROR] %s", timestamp, message);

        // Prints a notification to the console that an error has been logged.
        System.err.println(logMessage + " | Consulte log.txt para mais detalhes.");

        try (FileWriter fw = new FileWriter(LOG_FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(logMessage);
            out.println("----------------------------------------------------");

        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not write to the log file.");
            e.printStackTrace();
        }
    }
}