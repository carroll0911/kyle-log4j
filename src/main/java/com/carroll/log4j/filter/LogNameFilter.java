package com.carroll.log4j.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.List;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
public class LogNameFilter extends Filter<ILoggingEvent> {
    private List<String> nameFilter;

    public LogNameFilter(List<String> nameFilter) {
        this.nameFilter = nameFilter;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        boolean result = false;

        if(null != nameFilter){
            for (String str : nameFilter) {
                if(event.getLoggerName().contains(str)){
                    result = true;
                    break;
                }
            }
        }

        if (result) {
            return FilterReply.DENY;
        } else {
            return FilterReply.ACCEPT;
        }
    }
}
