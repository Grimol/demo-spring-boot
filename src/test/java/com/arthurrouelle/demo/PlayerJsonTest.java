package com.arthurrouelle.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;


@JsonTest
class PlayerJsonTest {

    @Autowired
    private JacksonTester<Player> json;

    @Autowired
    private JacksonTester<Player[]> jsonList;

    private Player[] players;

    @BeforeEach
    void setUp() {
        players = new Player[]{
                new Player(15L, "IsisTheWarrior", "isis", 
                1500L, 5, 2500L, 140.0, 18.0, 9.0, 1.2, 8.5, 175.0),
                new Player(25L, "LunaMage", "luna", 
                800L, 3, 1200L, 120.0, 14.0, 7.0, 0.8, 12.0, 200.0),
                new Player(35L, "PipouneArcher", "pipoune", 
                0L, 1, 0L, 100.0, 10.0, 5.0, 1.0, 5.0, 150.0)
        };
    }

    @Test
    void playerSerializationTest() throws IOException {
        Player player = players[0];
        assertThat(json.write(player)).isStrictlyEqualToJson("player_single.json");
        assertThat(json.write(player)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(15);
        assertThat(json.write(player)).hasJsonPathStringValue("@.pseudo");
        assertThat(json.write(player)).extractingJsonPathStringValue("@.pseudo")
                .isEqualTo("IsisTheWarrior");
        assertThat(json.write(player)).hasJsonPathStringValue("@.owner");
        assertThat(json.write(player)).extractingJsonPathStringValue("@.owner")
                .isEqualTo("isis");
        assertThat(json.write(player)).hasJsonPathNumberValue("@.experience");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.experience")
                .isEqualTo(1500);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.level");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.level")
                .isEqualTo(5);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.gold");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.gold")
                .isEqualTo(2500);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.hp");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.hp")
                .isEqualTo(140.0);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.atk");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.atk")
                .isEqualTo(18.0);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.defense");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.defense")
                .isEqualTo(9.0);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.atkSpd");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.atkSpd")
                .isEqualTo(1.2);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.critRate");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.critRate")
                .isEqualTo(8.5);
        assertThat(json.write(player)).hasJsonPathNumberValue("@.critDmg");
        assertThat(json.write(player)).extractingJsonPathNumberValue("@.critDmg")
                .isEqualTo(175.0);
    }

    @Test
    void playerDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 15,
                    "pseudo": "IsisTheWarrior",
                    "owner": "isis",
                    "experience": 1500,
                    "level": 5,
                    "gold": 2500,
                    "hp": 140.0,
                    "atk": 18.0,
                    "defense": 9.0,
                    "atkSpd": 1.2,
                    "critRate": 8.5,
                    "critDmg": 175.0
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new Player(15L, "IsisTheWarrior", "isis", 
                1500L, 5, 2500L, 140.0, 18.0, 9.0, 1.2, 8.5, 175.0));
        assertThat(json.parseObject(expected).id()).isEqualTo(15L);
        assertThat(json.parseObject(expected).pseudo()).isEqualTo("IsisTheWarrior");
        assertThat(json.parseObject(expected).owner()).isEqualTo("isis");
        assertThat(json.parseObject(expected).experience()).isEqualTo(1500L);
        assertThat(json.parseObject(expected).level()).isEqualTo(5);
        assertThat(json.parseObject(expected).gold()).isEqualTo(2500L);
        assertThat(json.parseObject(expected).hp()).isEqualTo(140.0);
        assertThat(json.parseObject(expected).atk()).isEqualTo(18.0);
        assertThat(json.parseObject(expected).defense()).isEqualTo(9.0);
        assertThat(json.parseObject(expected).atkSpd()).isEqualTo(1.2);
        assertThat(json.parseObject(expected).critRate()).isEqualTo(8.5);
        assertThat(json.parseObject(expected).critDmg()).isEqualTo(175.0);
    }
    
    @Test
    void playerListSerializationTest() throws IOException {
        assertThat(jsonList.write(players)).isStrictlyEqualToJson("player_list.json");
    }

    @Test
    void playerListDeserializationTest() throws IOException {
        String expected = """
                [
                    {
                        "id": 15,
                        "pseudo": "IsisTheWarrior",
                        "owner": "isis",
                        "experience": 1500,
                        "level": 5,
                        "gold": 2500,
                        "hp": 140.0,
                        "atk": 18.0,
                        "defense": 9.0,
                        "atkSpd": 1.2,
                        "critRate": 8.5,
                        "critDmg": 175.0
                    },
                    {
                        "id": 25,
                        "pseudo": "LunaMage",
                        "owner": "luna",
                        "experience": 800,
                        "level": 3,
                        "gold": 1200,
                        "hp": 120.0,
                        "atk": 14.0,
                        "defense": 7.0,
                        "atkSpd": 0.8,
                        "critRate": 12.0,
                        "critDmg": 200.0
                    },
                    {
                        "id": 35,
                        "pseudo": "PipouneArcher",
                        "owner": "pipoune",
                        "experience": 0,
                        "level": 1,
                        "gold": 0,
                        "hp": 100.0,
                        "atk": 10.0,
                        "defense": 5.0,
                        "atkSpd": 1.0,
                        "critRate": 5.0,
                        "critDmg": 150.0
                    }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(players);
    }
}
