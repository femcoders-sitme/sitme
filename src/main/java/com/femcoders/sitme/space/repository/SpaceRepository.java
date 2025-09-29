package com.femcoders.sitme.space.repository;

import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByType(SpaceType type);
    boolean existsByName(String name);
}
