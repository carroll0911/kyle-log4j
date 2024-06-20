package com.kyle.log4j.config;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
public class LoggerConfig {
    private String logLevel;
    
    private String url;
    
    private String fileName;
    
    private String dir;

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
