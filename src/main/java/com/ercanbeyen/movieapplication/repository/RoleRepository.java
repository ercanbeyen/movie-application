package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName);
    @Modifying
    @Query("""
            UPDATE Role role
            SET role.roleName = :roleName
            WHERE role.id = :id
           """
    )
    void updateRole(@Param("id") Integer id, @Param("roleName") String roleName);
}
