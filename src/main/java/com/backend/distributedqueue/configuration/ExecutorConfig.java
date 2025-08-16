package com.backend.distributedqueue.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    /**
     * Creates a dedicated fixed-size thread pool for processing job tasks in parallel.
     * The size of the pool is configurable via application properties.
     *
     * @param poolSize The number of threads in the pool, injected from 'app.task.executor.pool-size'.
     * @return A configured ExecutorService bean named 'taskExecutor'.
     */
    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor(@Value("${app.task.executor.pool-size:10}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
