package com.arthurrouelle.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/players")
class PlayerController {
    private final PlayerRepository playerRepository;

    private PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    
    @GetMapping("/{requestedId}")
    private ResponseEntity<Player> findById(@PathVariable Long requestedId, Principal principal) {
        Player player = findPlayer(requestedId, principal);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    private ResponseEntity<List<Player>> findAll(Pageable pageable, Principal principal) {
        Page<Player> page = playerRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "pseudo"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping()
    private ResponseEntity<Void> createNewPlayer(@RequestBody Player newPlayerRequest, UriComponentsBuilder ucb, Principal principal) {
        Player playerWithOwner = new Player(
            null, 
            newPlayerRequest.pseudo(), 
            principal.getName(), 
            0L, 
            1, 
            0L, 
            100.0, 
            10.0, 
            5.0, 
            1.0, 
            5.0, 
            150.0
        );
        Player savedPlayer = playerRepository.save(playerWithOwner);
        URI locationOfNewPlayer = ucb
                .path("players/{id}")
                .buildAndExpand(savedPlayer.id())
                .toUri();
        return ResponseEntity.created(locationOfNewPlayer).build();
    }
    
    private Player findPlayer(Long requestedId, Principal principal) {
        return playerRepository.findByIdAndOwner(requestedId, principal.getName());
    }
}
