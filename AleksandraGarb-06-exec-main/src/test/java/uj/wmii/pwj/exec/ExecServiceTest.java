package uj.wmii.pwj.exec;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExecServiceTest {

    @Test
    void testExecute() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.execute(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnable() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.submit(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnableWithResult() throws Exception {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        Object expected = new Object();
        Future<Object> f = s.submit(r, expected);
        doSleep(10);
        assertTrue(r.wasRun);
        assertTrue(f.isDone());
        assertEquals(expected, f.get());
    }

    @Test
    void testScheduleCallable() throws Exception {
        MyExecService s = MyExecService.newInstance();
        StringCallable c = new StringCallable("X", 10);
        Future<String> f = s.submit(c);
        doSleep(20);
        assertTrue(f.isDone());
        assertEquals("X", f.get());
    }

    @Test
    void testShutdown() {
        ExecutorService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        assertThrows(
            RejectedExecutionException.class,
            () -> s.submit(new TestRunnable()));
    }




    @Test
    void testShutdown2() {
        MyExecService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        assertThrows(RejectedExecutionException.class, () -> s.execute(new TestRunnable()));
    }

    @Test
    void testIsShutdown() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isShutdown());
        s.shutdown();
        assertTrue(s.isShutdown());
    }

    @Test
    void testIsTerminated() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isTerminated());
        s.shutdown();
        assertTrue(s.isTerminated());
    }

    @Test
    void testInvokeAll() throws InterruptedException, ExecutionException {
        MyExecService s = MyExecService.newInstance();
        List<Callable<Integer>> tasks = Arrays.asList(
                () -> 1,
                () -> 2 + 2,
                () -> 5 * 5
        );

        List<Future<Integer>> futures = s.invokeAll(tasks);

        assertEquals(3, futures.size());

        assertTrue(futures.get(0).isDone());
        assertTrue(futures.get(1).isDone());
        assertTrue(futures.get(2).isDone());

        assertEquals(1, futures.get(0).get().intValue());
        assertEquals(4, futures.get(1).get().intValue());
        assertEquals(25, futures.get(2).get().intValue());

        s.shutdown();

        assertThrows(RejectedExecutionException.class, () -> s.invokeAll(tasks));
    }

    @Test
    void testInvokeAll_time() throws InterruptedException, ExecutionException {
        MyExecService s = MyExecService.newInstance();
        List<Callable<Integer>> tasks = Arrays.asList(
                () -> { Thread.sleep(100); return 1; },
                () -> { Thread.sleep(200); return 2; },
                () -> { Thread.sleep(300); return 3; }
        );

        List<Future<Integer>> futures = s.invokeAll(tasks, 250, TimeUnit.MILLISECONDS);

        assertEquals(3, futures.size());

        assertTrue(futures.get(0).isDone());
        assertTrue(futures.get(1).isDone());
        assertFalse(futures.get(2).isCancelled());

        s.shutdown();

        assertThrows(RejectedExecutionException.class, () -> s.invokeAll(tasks, 250, TimeUnit.MILLISECONDS));
    }

    @Test
    void testInvokeAny() throws InterruptedException, ExecutionException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task_0", 100),
                new StringCallable("task_1", 200),
                new StringCallable("task_2", 300)
        );

        String result = s.invokeAny(tasks);
        assertEquals("task_0", result);
    }

    @Test
    void testInvokeAny_time() throws InterruptedException, ExecutionException, TimeoutException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task_0", 500),
                new StringCallable("task_1", 200),
                new StringCallable("task_2", 300)
        );

        String result = s.invokeAny(tasks, 250, TimeUnit.MILLISECONDS);
        assertEquals("task_1", result);

    }

    static void doSleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

class StringCallable implements Callable<String> {

    private final String result;
    private final int milis;

    StringCallable(String result, int milis) {
        this.result = result;
        this.milis = milis;
    }

    @Override
    public String call() throws Exception {
        ExecServiceTest.doSleep(milis);
        return result;
    }
}
class TestRunnable implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
    }
}
