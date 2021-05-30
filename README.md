# Log4j appender for Horus

This appender can be used to redirect Log4j messages
to Horus. This is a sample configuration file:

    log4j.rootLogger=INFO, console
    
    log4j.appender.console=org.apache.log4j.ConsoleAppender
    log4j.appender.console.layout=org.apache.log4j.PatternLayout
    log4j.appender.console.layout.ConversionPattern=%d{ISO8601} - %-5p [%t:%C@%L] - %m\n
    
    log4j.appender.horus=pt.haslab.horus.HorusLog4jAppender
    log4j.appender.horus.bootstraphoruss=kafka:9092
    log4j.appender.horus.topic=log_events
    log4j.appender.horus.group=horus
    log4j.appender.horus.timeout=5
    
    log4j.logger.my.application.package=DEBUG, console, horus

:warning: **If you are using slf4j**: Do not redirect
the root logger to this appender! Make sure that
only your application classes are logging to this appender.

The reason for this is that, as Kafka client itself uses
slf4j, if slf4j points to log4j and then to this
appender it leads to re-entering the producer and to a
**deadlock**.