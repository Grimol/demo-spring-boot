package com.arthurrouelle.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface PlayerRepository extends CrudRepository<Player, Long>, PagingAndSortingRepository<Player, Long> {
    Player findByIdAndOwner(Long id, String owner);
    Page<Player> findByOwner(String owner, PageRequest pageRequest);
    boolean existsByIdAndOwner(Long id, String owner);
}
