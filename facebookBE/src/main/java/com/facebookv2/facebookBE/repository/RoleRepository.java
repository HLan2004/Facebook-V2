package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
