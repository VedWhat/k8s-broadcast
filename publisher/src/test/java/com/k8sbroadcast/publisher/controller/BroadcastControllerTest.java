package com.k8sbroadcast.publisher.controller;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BroadcastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoreV1Api coreV1Api;

    @BeforeEach
    void setUp() throws Exception {
        V1Pod pod = new V1Pod();
        V1PodStatus status = new V1PodStatus();
        status.setPodIP("10.0.0.1");
        pod.setStatus(status);
        V1PodList podList = new V1PodList().items(Collections.singletonList(pod));

        Mockito.when(coreV1Api.listNamespacedPod(
                eq("default"),
                any(), any(), any(), any(),
                eq("app=listener"),
                any(), any(), any(), any(), any())).thenReturn(podList);
    }

    // TODO: Fix this broken test
    // @Test
    // public void testBroadcastReturnsResponses() throws Exception {
    // mockMvc.perform(get("/broadcast"))
    // .andExpect(status().isOk())
    // .andExpect(content().string(containsString("Failed to reach pod")));
    // }
}