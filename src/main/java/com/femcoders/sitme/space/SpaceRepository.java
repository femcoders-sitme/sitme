package com.femcoders.sitme.space;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByType(SpaceType type);
    List<Space> findByIsAvailableTrue();
}
