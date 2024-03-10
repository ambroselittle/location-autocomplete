package com.indeed.suggest;

import java.io.*;
import java.util.Map;

/**
 * Reads through a query log, line by line.
 *
 * You will probably want to modify this class to do something useful with the
 * log file.
 */
public class LogReader {
    private String filename;
    private final LogEntryProcessor logEntryProcessor;

    public LogReader(String filename, LogEntryProcessor processor) {
        this.filename = filename;
        this.logEntryProcessor = processor;
    }

    public void read() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        int lineCount = 0;

        try {
            String line;
            while ((line = in.readLine()) != null) {
                Map<String,String> map = Utils.getNameValues(line);
                logEntryProcessor.processEntries(map);
                lineCount++;
            }

            System.out.println("lineCount = " + lineCount);
        } finally {
            in.close();
        }
    }


}
