package io.github.kimmking.gateway.benchmark;

import lombok.Data;

/**
 * @author lixiaobing
 * @date 2020-11-03 18:27
 * @Description:
 */
@Data
public class BenchMarkResult {
    private Double qps;
    private Double avgLatency;
    private Double tp999;
    private Double tp99;
    private Double tp95;
    private Double tp90;
    private Double tp50;
}
