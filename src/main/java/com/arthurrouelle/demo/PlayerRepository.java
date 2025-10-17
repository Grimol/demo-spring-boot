package com.arthurrouelle.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface PlayerRepository extends CrudRepository<Player, Long>, PagingAndSortingRepository<Player, Long> {
    Player findByIdAndOwner(Long id, String owner);
    Page<Player> findByOwner(String owner, Pageable pageable);
    boolean existsByIdAndOwner(Long id, String owner);
    
    // Méthode simple sans pagination
    Iterable<Player> findAllByOwnerOrderByPseudo(String owner);
    
    // Suppression sécurisée par owner
    Long deleteByIdAndOwner(Long id, String owner);
}
