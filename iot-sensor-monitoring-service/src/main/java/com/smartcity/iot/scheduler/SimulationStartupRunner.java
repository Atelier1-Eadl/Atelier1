package com.smartcity.iot.scheduler;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.service.simulation.SimulationOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Starts all active sensor simulations when the application boots.
 */
@Component
@Slf4j
public class SimulationStartupRunner implements ApplicationRunner {

    private final SimulationOrchestrator orchestrator;
    private final AppProperties appProperties;

    @Autowired
    public SimulationStartupRunner(SimulationOrchestrator orchestrator,
                                    AppProperties appProperties) {
        this.orchestrator = orchestrator;
        this.appProperties = appProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appProperties.getSimulation().isEnabled()) {
            log.info("Simulation disabled, skipping auto-start");
            return;
        }
        log.info("Starting all active sensor simulations on application boot...");
        orchestrator.startAll();
        log.info("Simulation started. Active simulators: {}", orchestrator.activeCount());
    }
}
