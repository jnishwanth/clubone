package com.firstclub.membership.catalog.repository;

import com.firstclub.membership.catalog.domain.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {

    List<Tier> findAllByOrderByRankAsc();

    List<Tier> findByActiveTrueOrderByRankAsc();

    Optional<Tier> findByRank(int rank);

    Optional<Tier> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByRank(int rank);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByRankAndIdNot(int rank, Long id);
}
