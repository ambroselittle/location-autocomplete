package com.indeed.suggest;

import java.util.Map;

public interface LogEntryProcessor {
    public void processEntries(Map<String, String> map);
}
