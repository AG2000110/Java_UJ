package uj.wmii.pwj.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MyExecService implements ExecutorService {

    private boolean isShutdown = false;
    private final List<Thread> workers = new ArrayList<Thread>();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();


    static MyExecService newInstance() {
        return new MyExecService();
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        for (Thread worker : workers) {
            worker.interrupt();
        }
        queue.clear();
        return new ArrayList<>();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown && workers.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long timeoutMillis = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();
        for (Thread worker : workers) {
            long timeLeft = timeoutMillis - (System.currentTimeMillis() - startTime);
            if (timeLeft > 0) {
                worker.join(timeLeft);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if(isShutdown){
            throw new RejectedExecutionException();
        } else {
            FutureTask<T> futureTask = new FutureTask<>(task);
            execute(futureTask);
            return futureTask;
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if(isShutdown){
            throw new RejectedExecutionException();
        } else {
            FutureTask<T> futureTask = new FutureTask<>(task, result);
            execute(futureTask);
            return futureTask;
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if(isShutdown){
            throw new RejectedExecutionException();
        }
        if (tasks == null || tasks.isEmpty()) {
            throw new NullPointerException("Task list is null or empty");
        }
        List<Future<T>> futureList = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futureList.add(submit(task));
        }
        for(Future<T> future : futureList){
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return futureList;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        if (tasks == null || tasks.isEmpty()) {
            throw new NullPointerException("Task list is null or empty");
        }
        List<Future<T>> futureList = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futureList.add(submit(task));
        }
        for (Future<T> future : futureList) {
            try {
                    future.get(timeout, unit);
            } catch (ExecutionException e){
                throw new RuntimeException(e);
            } catch (TimeoutException t){
                future.cancel(true);
            }
        }
        return futureList;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        if (tasks == null || tasks.isEmpty()) {
            throw new NullPointerException("Task list is null or empty");
        }
        List<Future<T>> futureList = invokeAll(tasks);
        for(Future<T> future : futureList){
            try {
                return future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new ExecutionException(null);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        if (tasks == null || tasks.isEmpty()) {
            throw new NullPointerException("Task list is null or empty");
        }
        List<Future<T>> futureList = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futureList.add(submit(task));
        }
        for (Future<T> future : futureList) {
            try {
                return future.get(timeout, unit);
            } catch (ExecutionException e){
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                future.cancel(true);
            }
        }
        throw new ExecutionException("", new Exception());
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown) {
            throw new RejectedExecutionException();
        }
        if (command == null) {
            throw new NullPointerException("Command is null");
        }
        var thread = new Thread( () -> {
            command.run();
            workers.remove(Thread.currentThread());
        });
        workers.add(thread);
        thread.start();
    }
}
