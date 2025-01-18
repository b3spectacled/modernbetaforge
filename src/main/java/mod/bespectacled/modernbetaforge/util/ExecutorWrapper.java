package mod.bespectacled.modernbetaforge.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public class ExecutorWrapper {
    public enum TaskResult {
        SUCCESS, FAILURE
    }
    
    private static final int INITIAL_SHUTDOWN_WAIT_TIME_MS = 1000;
    private static final int DELAYED_SHUTDOWN_WAIT_TIME_MS = 5000;
    
    private final ExecutorService executor;
    private final String name;
    
    public ExecutorWrapper(int threads, String name) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.name = name;
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.shutdown()));
    }
    
    public void queueRunnable(Runnable runnable) {
        this.executor.execute(runnable);
    }
    
    public Future<TaskResult> queueCallable(Callable<TaskResult> callable) {
        return this.executor.submit(callable);
    }
    
    public void shutdown() {
        ModernBeta.log(Level.DEBUG, String.format("Shutting down executor service '%s'..", this.name));
        
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(INITIAL_SHUTDOWN_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                this.executor.shutdownNow();
                
                if (!this.executor.awaitTermination(DELAYED_SHUTDOWN_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                    ModernBeta.log(Level.DEBUG, String.format("Executor service '%s' still did not shutdown!", this.name));
                }
            } 
        } catch (InterruptedException e) {
            ModernBeta.log(Level.DEBUG, String.format("Executor service '%s' shutdown was interrupted!", this.name));
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
