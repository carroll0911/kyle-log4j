package com.carroll.log4j.extend;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
public class ThreadIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return String.valueOf(Thread.currentThread().getId());
    }

}