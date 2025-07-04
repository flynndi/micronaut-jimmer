package io.framework.benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenchmarkApplication {

    private static final int WARMUP_ITERATIONS = 5;

    private static final int MEASUREMENT_ITERATIONS = 5;

    public static void main(String[] args) throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include("\\.SpringbootBenchmark\\.")
                        .include("\\.MicronautBenchmark\\.")
                        .warmupIterations(WARMUP_ITERATIONS)
                        .warmupTime(TimeValue.seconds(1))
                        .measurementIterations(MEASUREMENT_ITERATIONS)
                        .measurementTime(TimeValue.seconds(1))
                        .resultFormat(ResultFormatType.JSON)
                        .forks(1)
                        .shouldDoGC(true)
                        .result("benchmark-report.json")
                        .shouldFailOnError(true)
                        .jvmArgs("-server", "-XX:+PreserveFramePointer")
                        .build();
        new Runner(opt).run();
    }
}
