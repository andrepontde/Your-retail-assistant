package dev.andrepontde.retailmanager.retail_system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Public endpoints for server discovery and configuration
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Server information endpoint - no authentication required
     * Helps mobile apps discover server capabilities
     */
    @GetMapping("/server-info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("applicationName", applicationName);
        serverInfo.put("version", "1.0.0");
        serverInfo.put("apiVersion", "v1");
        serverInfo.put("supportedFeatures", new String[]{
            "user-management",
            "inventory-management", 
            "sales-processing",
            "multi-store-support",
            "jwt-authentication"
        });
        serverInfo.put("authenticationMethods", new String[]{"jwt"});
        serverInfo.put("status", "online");
        
        return ResponseEntity.ok(serverInfo);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(status);
    }
}
