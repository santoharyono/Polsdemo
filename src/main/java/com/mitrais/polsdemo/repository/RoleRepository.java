package com.mitrais.polsdemo.repository;

import com.mitrais.polsdemo.model.Role;
import com.mitrais.polsdemo.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
