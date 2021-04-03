package pt.haslab.horus;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class Log4jPrintStream extends PrintStream {
    final static Logger log = LogManager.getLogger(Log4jPrintStream.class);

    public Log4jPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String x) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // Element 0 is getStackTrace
        // Element 1 is println
        // Element 2 is the caller
        StackTraceElement caller = stack[2];

        log.info(x);
    }
}