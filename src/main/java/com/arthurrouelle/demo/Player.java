package com.arthurrouelle.demo;

import org.springframework.data.annotation.Id;

record Player(
    @Id Long id,
    String pseudo,
    String owner,
    Long experience,
    Integer level,
    Long gold,
    Double hp,
    Double atk,
    Double defense,
    Double atkSpd,
    Double critRate,
    Double critDmg
) {
}
