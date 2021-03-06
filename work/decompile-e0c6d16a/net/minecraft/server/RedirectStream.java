package net.minecraft.server;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedirectStream extends PrintStream {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected final String name;

    public RedirectStream(String s, OutputStream outputstream) {
        super(outputstream);
        this.name = s;
    }

    public void println(@Nullable String s) {
        this.logLine(s);
    }

    public void println(Object object) {
        this.logLine(String.valueOf(object));
    }

    protected void logLine(@Nullable String s) {
        RedirectStream.LOGGER.info("[{}]: {}", this.name, s);
    }
}
