package com.order_service.config;

import com.order_service.service.activity.OrderActivitiesImpl;
import com.order_service.service.workflow.OrderWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    public static final String TASK_QUEUE = "ORDER_TASK_QUEUE";

    private final OrderActivitiesImpl orderActivitiesImpl;

    public TemporalConfig(OrderActivitiesImpl orderActivitiesImpl) {
        this.orderActivitiesImpl = orderActivitiesImpl;
    }

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget("localhost:7233")
                .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        Worker worker = factory.newWorker(TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(orderActivitiesImpl);

        factory.start();

        return factory;
    }
}