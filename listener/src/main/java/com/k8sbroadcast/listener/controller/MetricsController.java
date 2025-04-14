package com.k8sbroadcast.listener.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    @Value("${HOSTNAME:unknown}")
    private String hostname;

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double usedMB = heap.getUsed() / (1024.0 * 1024.0);

        Map<String, Object> data = new HashMap<>();
        data.put("pod", hostname);
        data.put("timestamp", Instant.now().toString());
        data.put("memoryMB", Math.round(usedMB * 10.0) / 10.0);
        return data;
    }
}
