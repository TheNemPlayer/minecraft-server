package net.minecraft.server;

import java.io.OutputStream;

public class DebugOutputStream extends RedirectStream {

    public DebugOutputStream(String s, OutputStream outputstream) {
        super(s, outputstream);
    }

    @Override
    protected void logLine(String s) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];

        DebugOutputStream.LOGGER.info("[{}]@.({}:{}): {}", this.name, stacktraceelement.getFileName(), stacktraceelement.getLineNumber(), s);
    }
}
