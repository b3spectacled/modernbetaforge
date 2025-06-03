package mod.bespectacled.modernbetaforge.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ExecutorCallableWrapper<T> extends ExecutorWrapper {
    public ExecutorCallableWrapper(int threads, String name) {
        super(threads, name);
    }
    
    public Future<T> queueCallable(Callable<T> callable) {
        return this.executor.submit(callable);
    }
}
