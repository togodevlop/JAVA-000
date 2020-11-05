package io.github.kimmking.gateway.benchmark;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @author lixiaobing
 * @date 2020-11-03 18:26
 * @Description:
 */
public class Benchmark {
    public static void main(String[] args) {
        String url = "http://www.baidu.com";
        httpGet(url);
        BenchMarkResult benchMarkResult = benchMark(8 * 3, 60 * 1000, url);
        System.out.println(benchMarkResult);
    }

    public static BenchMarkResult benchMark(int threads, long milis, String url) {
        ConcurrentLinkedDeque<Long> durations    = new ConcurrentLinkedDeque<>();
        long                        programStart = System.currentTimeMillis();
        Runnable task = () -> {
            long startTimeMillis = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTimeMillis) < milis) {
                long start = System.currentTimeMillis();
                httpGet(url);
                long end = System.currentTimeMillis();
                durations.offer(end - start);
            }
        };
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            threadList.add(new Thread(task));

        }
        threadList.forEach(Thread::start);
        threadList.forEach(Benchmark::join);
        long programEnd = System.currentTimeMillis();

        BenchMarkResult benchMarkResult = new BenchMarkResult();
        List<Long>      durationList    = durations.stream().collect(Collectors.toList());
        Collections.sort(durationList);
        int    size = durationList.size();
        double min  = (programEnd - programStart) / 60 / 1000.0;
        double qps  = size / min;
        benchMarkResult.setQps(qps);

        Long   totalTime  = durationList.stream().reduce((x, y) -> x + y).get();
        double avgLatency = totalTime / size;
        benchMarkResult.setAvgLatency(avgLatency);
        benchMarkResult.setTp50(durationList.get((int) (size * 0.5)).doubleValue());
        benchMarkResult.setTp90(durationList.get((int) (size * 0.9)).doubleValue());
        benchMarkResult.setTp95(durationList.get((int) (size * 0.95)).doubleValue());
        benchMarkResult.setTp99(durationList.get((int) (size * 0.99)).doubleValue());
        benchMarkResult.setTp999(durationList.get((int) (size * 0.999)).doubleValue());
        return benchMarkResult;
    }

    private static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void httpGet(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet             httpGet    = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
