package com.k8sbroadcast.publisher.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

@RestController
public class BroadcastController {

    private final CoreV1Api coreV1Api;

    public BroadcastController() throws IOException {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        this.coreV1Api = new CoreV1Api();
    }

    @GetMapping("/broadcast")
    public List<String> broadcast() throws ApiException {
        V1PodList pods = coreV1Api.listNamespacedPod(
                "default", null, null, null, null,
                "app=listener",
                null, null, null, null, null);

        RestTemplate restTemplate = new RestTemplate();
        List<String> responses = new ArrayList<>();

        for (V1Pod pod : pods.getItems()) {
            String podIp = pod.getStatus().getPodIP();
            try {
                String result = restTemplate.getForObject("http://" + podIp + ":8080/ping", String.class);
                responses.add(result);
            } catch (Exception e) {
                responses.add("Failed to reach pod: " + podIp);
            }
        }

        return responses;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("/broadcastv2")
    public List<Map<String, Object>> broadcast(
            @RequestParam(required = false) List<String> label) throws ApiException {
        String labelSelector = (label != null && !label.isEmpty())
                ? String.join(",", label)
                : "app=listener";

        V1PodList pods = coreV1Api.listNamespacedPod(
                "default", null, null, null, null,
                labelSelector, null, null, null, null, null);

        List<Map<String, Object>> results = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        for (V1Pod pod : pods.getItems()) {
            String podIp = pod.getStatus().getPodIP();
            try {
                Map response = restTemplate.getForObject(
                        "http://" + podIp + ":8080/metrics", Map.class);
                results.add(response);
            } catch (Exception e) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("pod", podIp);
                failed.put("error", "unreachable");
                results.add(failed);
            }
        }

        return results;
    }

}