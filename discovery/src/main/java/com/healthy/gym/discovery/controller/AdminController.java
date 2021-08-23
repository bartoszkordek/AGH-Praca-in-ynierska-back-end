package com.healthy.gym.discovery.controller;

import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/registry")
public class AdminController {

    private final PeerAwareInstanceRegistry peerAwareInstanceRegistry;

    public AdminController(PeerAwareInstanceRegistry peerAwareInstanceRegistry) {
        this.peerAwareInstanceRegistry = peerAwareInstanceRegistry;
    }

    @GetMapping
    public List<Application> getCurrentRegistry() {
        return peerAwareInstanceRegistry.getApplications().getRegisteredApplications();
    }

    @GetMapping("/{service}")
    public List<Application> getCurrentRegistryForName(@PathVariable String service) {
        Applications applications = peerAwareInstanceRegistry.getApplications();

        return applications
                .getRegisteredApplications()
                .stream()
                .filter(application -> application.getName().equals(service.toUpperCase()))
                .collect(Collectors.toList());
    }
}
