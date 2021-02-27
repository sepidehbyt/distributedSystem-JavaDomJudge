package com.distributed.systems.dom_judge.repository;

import com.distributed.systems.dom_judge.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Override
    void delete(Role role);
}
