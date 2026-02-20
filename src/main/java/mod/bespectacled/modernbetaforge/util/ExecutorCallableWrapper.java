package mod.bespectacled.modernbetaforge.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public class ExecutorCallableWrapper<T> extends ExecutorWrapper {
    public ExecutorCallableWrapper(int threads, String name) {
        super(threads, name);
    }
    
    public Future<T> queueCallable(Callable<T> callable) {
        try {
            return this.executor.submit(callable);
        } catch (RejectedExecutionException e) {
            ModernBeta.log(Level.DEBUG, String.format("Executor service '%s' is busy!", this.name));
            return null;
        }
    }
}
