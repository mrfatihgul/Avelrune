package com.avelrune.property.service.impl;

import com.avelrune.property.dto.AgentResponse;
import com.avelrune.property.exception.ApiException;
import com.avelrune.property.repository.AgentRepository;
import com.avelrune.property.service.AgentService;
import com.avelrune.property.service.PropertyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    public AgentServiceImpl(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public List<AgentResponse> list() {
        return agentRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(PropertyMapper::toAgent)
                .toList();
    }

    @Override
    public AgentResponse get(Long id) {
        return agentRepository.findById(id)
                .map(PropertyMapper::toAgent)
                .orElseThrow(() -> new ApiException(404, "Danışman bulunamadı."));
    }
}
