package com.kyle.log4j;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.kyle.log4j.config.LogConfig;
import com.kyle.log4j.filter.LogNameFilter;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
@Component
public class LogUtil {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LogUtil.class);

    private static LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    private static final String APPLICATION_NAME_KEY = "spring.application.name";

    @Autowired
    LogConfig logConfig;

    @Autowired
    private Environment env;

    private String appName;

    Map<String, RollingFileAppender> appenderMap = new HashMap();

    @EventListener(ApplicationReadyEvent.class)
    @Order
    public void init() {
        appenderMap.clear();
        if (StringUtils.isEmpty(logConfig.getAppName())) {
            appName = env.getProperty(APPLICATION_NAME_KEY);
        } else {
            appName = logConfig.getAppName();
        }
        if (StringUtils.isEmpty(appName)) {
            appName = "app";
        }
        if (logConfig.isEnable()) {
            context.setName("logStash");
            if (logConfig.isRootLogger()) {
                context.getLogger(Logger.ROOT_LOGGER_NAME).detachAndStopAllAppenders();
            }
            addLogStashAppender();
            addRollingFileAppender("rollingFILE", Logger.ROOT_LOGGER_NAME, logConfig.getLogHome(), appName);
            addLogger();
            addLoggerConfigs();
            log.info("init end");
        }
    }

    public void addLogStashAppender() {
        log.info("Initializing LogStash logging");

        if (StringUtils.isEmpty(logConfig.getServerPort())) {
            log.info("LogStash server not set");
            return;
        }

        LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setContext(context);
        encoder.start();

        addFilter(appender);
        appender.setName("stash");
        appender.setContext(context);
        appender.addDestination(logConfig.getServerPort());
        appender.setEncoder(encoder);
        appender.start();

        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.toLevel(logConfig.getLevel()));
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
    }

    public void addConsoleAppender() {
        log.info("Initializing Console logging");

        ConsoleAppender appender = new ConsoleAppender();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(Charset.forName(logConfig.getCharset()));
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %contextName [%thread] trace-id-%X{TraceId} %logger{60} : %msg%n");
        encoder.setContext(context);
        encoder.start();

        addFilter(appender);
        appender.setName("console");
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.start();

        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.toLevel(logConfig.getLevel()));
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
    }

    public void addRollingFileAppender(String appenderName, String loggerName, String dir, String appName) {
        log.info("Initializing File logging");
        RollingFileAppender appender;
        if (appenderMap.get(appName) == null) {
            appender = new RollingFileAppender();
            appender.setName(appenderName);
            appender.setFile(String.format("%s%s.log", dir, appName));
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setCharset(Charset.forName(logConfig.getCharset()));
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %contextName [%thread] trace-id-%X{TraceId} %logger{60} : %msg%n");
            encoder.setImmediateFlush(true);
            encoder.setContext(context);
            encoder.start();

            TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
            policy.setFileNamePattern(String.format("%s%s", dir, appName) + ".%d{yyyy-MM-dd}.%i.log");
            policy.setMaxHistory(logConfig.getMaxHistory());
            if (!StringUtils.isEmpty(logConfig.getTotalSizeCap())) {
                policy.setTotalSizeCap(FileSize.valueOf(logConfig.getTotalSizeCap()));
            }
            policy.setContext(context);
            policy.setParent(appender);

            appender.setRollingPolicy(policy);
            SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();
            sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf(logConfig.getMaxFileSize()));
            sizeAndTimeBasedFNATP.setContext(context);
            sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(policy);
            policy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
            policy.start();

            addFilter(appender);
            appender.setContext(context);
            appender.setEncoder(encoder);
            appender.start();
            appenderMap.put(appName, appender);
        } else {
            appender = appenderMap.get(appName);
        }
        context.getLogger(loggerName).addAppender(appender);
    }

    private void addFilter(Appender appender) {
        List<String> nameFilter = logConfig.getNameFilter();

        if (null != nameFilter && nameFilter.size() > 0) {
            LogNameFilter logNameFilter = new LogNameFilter(logConfig.getNameFilter());
            logNameFilter.start();
            appender.addFilter(logNameFilter);
        }
    }

    private void addLogger() {
        if (logConfig.getLoggers() != null && !logConfig.getLoggers().isEmpty()) {
            logConfig.getLoggers().forEach(logger -> {
                String[] logArray = logger.split("=");
                if (logArray.length == 2) {
                    context.getLogger(logArray[0]).setLevel(Level.toLevel(logArray[1]));
                }
            });
        }
    }

    private void addLoggerConfigs() {
        if (logConfig.getLoggerConfigs() != null) {
            logConfig.getLoggerConfigs().forEach(loggerConfig -> {
                context.getLogger(loggerConfig.getUrl()).setLevel(Level.toLevel(loggerConfig.getLogLevel()));
                String fileName = StringUtils.isEmpty(loggerConfig.getFileName()) ? appName : loggerConfig.getFileName();
                String dir = StringUtils.isEmpty(loggerConfig.getDir()) ? logConfig.getLogHome() : loggerConfig.getDir();
                addRollingFileAppender(loggerConfig.getUrl(), loggerConfig.getUrl(), dir, fileName);
                context.getLogger(loggerConfig.getUrl()).setAdditive(false);
            });
        }
    }
}
