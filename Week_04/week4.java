
import org.junit.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;



public class week4 {
    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        int i = 1, j = 1;
        while (a-- > 1) {
            int sum = i + j;
            j = i;
            i = sum;
        }
        return i;
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过join子线程，等待子线程结束，得到计算结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        AtomicInteger sum = new AtomicInteger();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sum.set(sum());
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        System.out.println("sum=" + sum.get());
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过join子线程，等待子线程结束，得到计算结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            sum.set(sum());
        });
        thread.start();
        thread.join();
        System.out.println("sum=" + sum.get());
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过sleep和synchronized控制子线程线获取锁，等待子线程结束，主线程获取到锁得到计算结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test3() throws InterruptedException {
        Object mutex = new Object();
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            synchronized (mutex) {
                sum.set(sum());
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        synchronized (mutex) {
            System.out.println("sum=" + sum.get());
        }
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过synchronized和wait控制主线程等待子线程计算出结果，子线程结束后，通知主线程拿结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test4() throws InterruptedException {
        Object mutex = new Object();
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            synchronized (mutex) {
                sum.set(sum());
                mutex.notify();
            }
        });
        thread.start();
        synchronized (mutex) {
            // 防止子线程先执行完，wait后，没人notify
            if (sum.get() == 0) {
                mutex.wait();
            }
            System.out.println("sum=" + sum.get());
        }
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过ReentrantLock和Condition控制主线程等待子线程计算出结果，子线程结束后，通知主线程拿结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test5() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                sum.set(sum());
            } catch (Exception ignored) {
            } finally {
                condition.signal();
                lock.unlock();
            }
        });
        thread.start();
        lock.lock();
        // 防止子线程先执行完，wait后，没人signal
        if (sum.get() == 0) {
            condition.await();
        }
        System.out.println("sum=" + sum.get());
        lock.unlock();
    }

    /**
     * 通过设置外部变量保存结果，
     * 通过CountDownLatch控制主线程等待子线程计算出结果，子线程结束后，主线程拿结果
     *
     * @throws InterruptedException
     */
    @Test
    public void test6() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                sum.set(sum());
            } catch (Exception ignored) {
            } finally {
                countDownLatch.countDown();
            }
        });
        thread.start();
        countDownLatch.await();
        System.out.println("sum=" + sum.get());
    }

    /**
     * 通过设置外部变量保存结果，
     * CyclicBarrier初始值为2
     * 子线程计算完成后，与主线程互相等待，一起退出，主线程获取结果后退出
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    @Test
    public void test7() throws BrokenBarrierException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                sum.set(sum());
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        cyclicBarrier.await();
        System.out.println("sum=" + sum.get());
    }
    /**
     * 通过设置外部变量保存结果，
     * 主线程先park，通过CyclicBarrier的barrierAction唤醒主线程，然后获取结果
     */
    @Test
    public void test8() {
        Thread currentThread = Thread.currentThread();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            // 如果先unpark，后park，也没有关系，
            // 会把下一次park的线程唤醒
            LockSupport.unpark(currentThread);
        });

        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                sum.set(sum());
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        LockSupport.park();
        System.out.println("sum=" + sum.get());
    }

    /**
     * 通过设置外部变量保存结果，
     * 设置一个volatile的静态变量，表示是否计算结束
     * 主线程死循环遍历是否计算结束，
     * CyclicBarrier的barrierAction改变计算状态，主线程可以获取结果了
     */
    private static volatile boolean isOver = false;

    @Test
    public void test9() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            isOver = true;
        });
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                sum.set(sum());
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        while (true) {
            if (!isOver) continue;
            System.out.println("sum=" + sum.get());
            break;
        }
    }

    /**
     * 通过设置外部变量保存结果，
     * 初始化Semaphore是没有资源的，主线程获取资源阻塞，
     * 子线程计算结束后，释放一个资源，此时主线程可以获得许可，然后获取计算结果
     * @throws InterruptedException
     */
    @Test
    public void test10() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        AtomicInteger sum = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                sum.set(sum());
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        semaphore.acquire();
        System.out.println("sum=" + sum.get());
    }

    /**
     * 利用阻塞队列的特性，计算结果存在队列中
     * 主线程使用take出队，子线程offer入队
     * 如果队列为空则take会阻塞，会等待子线程把结果offer进队列，主线程才会获取到结果
     * @throws InterruptedException
     */
    @Test
    public void test11() throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1);
//        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(1);
        Thread thread = new Thread(() -> {
            try {
                int sum = sum();
                queue.offer(sum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        // 队列为空则阻塞
        Integer sum = queue.take();
        System.out.println("sum=" + sum);
    }

    /**
     * 利用FutureTask把执行体包装成有返回值的任务
     * 子线程启动后，主线程中get获取结果，会阻塞等待到获取到结果
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test12() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return sum();
            }
        });
        new Thread(futureTask).start();
        Integer sum = futureTask.get();
        System.out.println("sum=" + sum);
    }

    /**
     * 利用CompletableFuture执行异步任务，然后主线程中会等待子线程结束，然后获取子线程的结果
     */
    @Test
    public void test13() {
//        CompletableFuture.supplyAsync(HomeWeek::sum).thenAccept(System.out::println);
        CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread().getName() + "=执行计算=");
            return sum();
        }).thenAccept(o->{
            System.out.println(Thread.currentThread().getName()+"=获取结果=sum="+o);
        });
    }

}
