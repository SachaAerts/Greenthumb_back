package com.GreenThumb.api.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisConnectionTest {

    @Test
    void shouldConnectToRedisCloud() {
        // 🔐 Connexion directe (remplace les infos par les tiennes)
        RedisURI redisURI = RedisURI.Builder
                .redis("redis-16678.c241.us-east-1-4.ec2.redns.redis-cloud.com", 16678)
                .withAuthentication("default", "O7jq125rlEtvNYowONAmIrznTyuezEU5")
                .build();

        // ✅ Création du client et connexion
        RedisClient client = RedisClient.create(redisURI);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        // 👇 Test simple : écrire / lire une clé
        commands.set("test:hard", "Hello Redis Cloud!");
        String value = commands.get("test:hard");

        // Vérifie la valeur
        assertThat(value).isEqualTo("Hello Redis Cloud!");
        System.out.println("✅ Redis Cloud OK → " + value);

        // 🔒 Ferme proprement la connexion
        connection.close();
        client.shutdown();
    }
}
