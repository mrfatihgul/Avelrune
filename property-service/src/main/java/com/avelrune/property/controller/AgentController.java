package com.avelrune.property.controller;

import com.avelrune.property.dto.AgentResponse;
import com.avelrune.property.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public ResponseEntity<List<AgentResponse>> list() {
        return ResponseEntity.ok(agentService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.get(id));
    }
}
