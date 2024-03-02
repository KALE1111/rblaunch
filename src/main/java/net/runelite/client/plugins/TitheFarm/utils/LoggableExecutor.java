package net.runelite.client.plugins.TitheFarm.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
public class LoggableExecutor extends ScheduledThreadPoolExecutor {
    public LoggableExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ignored) {
                log.info(" Task cancelled");
            } catch (ExecutionException ee) {
                log.info(" Task execution failed");
                t = ee.getCause();
            } catch (InterruptedException ie) {
                log.info(" LoggableExecutor interrupted");
                Thread.currentThread().interrupt();
            }
        }

        if (t != null) {
            log.info(" Error in loop", t);
        }
    }
}
