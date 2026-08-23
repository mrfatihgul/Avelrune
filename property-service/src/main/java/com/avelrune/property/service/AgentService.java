package com.avelrune.property.service;

import com.avelrune.property.dto.AgentResponse;

import java.util.List;

public interface AgentService {

    List<AgentResponse> list();

    AgentResponse get(Long id);
}
