package dev.boghy.kubernetes.service;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobSchedulerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelay = 25000)
    public void runJob() {
        LocalDateTime date = LocalDateTime.now();
        logger.info("Time {}", date.toString());
        String namespace = "boghy933-dev";
        String jobName = "boghy933-dev-demo-" + UUID.randomUUID().toString();
        KubernetesClient kubernetesClient = new DefaultKubernetesClient();
        Job job = new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                .withName(jobName)
                .endMetadata()
                .withNewSpec()
                .withBackoffLimit(4)
                .withTtlSecondsAfterFinished(100)
                .withNewTemplate()
                .withNewSpec()
                .addNewContainer()
                .withName(jobName)
                .withImage("perl:5.34.0")
                .withCommand("perl", "-Mbignum=bpi", "-wle", "print bpi(2000)")
                .endContainer()
                .withRestartPolicy("Never")
                .endSpec()
                .endTemplate()
                .endSpec().build();

        kubernetesClient.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);
        kubernetesClient.close();
    }
}
