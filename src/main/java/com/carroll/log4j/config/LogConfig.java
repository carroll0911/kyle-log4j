package com.carroll.log4j.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
@Component
@ConfigurationProperties(prefix = "carroll.log4j")
public class LogConfig {
    private boolean enable = true;

    private String contextName;

    private String serverPort;

    private String charset = "UTF-8";

    private String level = "INFO";

    private List<String> nameFilter;

    private String logHome = System.getProperty("user.home") + File.separator;
    ;

    private String appName;

    private int maxHistory = 30;

    private String maxFileSize = "100MB";

    /**
     * 保存日志总大小
     */
    private String totalSizeCap;

    private List<String> loggers;

    private List<LoggerConfig> loggerConfigs;

    private boolean rootLogger = false;

    public List<String> getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(List<String> nameFilter) {
        this.nameFilter = nameFilter;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getLogHome() {
        return logHome;
    }

    public void setLogHome(String logHome) {
        this.logHome = logHome;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getLoggers() {
        return loggers;
    }

    public void setLoggers(List<String> loggers) {
        this.loggers = loggers;
    }

    public boolean isRootLogger() {
        return rootLogger;
    }

    public void setRootLogger(boolean rootLogger) {
        this.rootLogger = rootLogger;
    }

    public List<LoggerConfig> getLoggerConfigs() {
        return loggerConfigs;
    }

    public void setLoggerConfigs(List<LoggerConfig> loggerConfigs) {
        this.loggerConfigs = loggerConfigs;
    }

    public String getTotalSizeCap() {
        return totalSizeCap;
    }

    public void setTotalSizeCap(String totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }
}
