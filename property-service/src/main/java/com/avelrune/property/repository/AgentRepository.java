package com.avelrune.property.repository;

import com.avelrune.property.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    List<Agent> findAllByOrderBySortOrderAsc();
}
