package com.thehecklers.playgroundjavamvc;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootApplication
public class PlaygroundJavaMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaygroundJavaMvcApplication.class, args);
    }

}

@Component
@AllArgsConstructor
class DataLoader {
    private final ShipRepository repo;

    @PostConstruct
    void load() {
        List<String> shipNames = Arrays.asList("Enterprise", "Constitution", "Farragut", "Defiant", "Excalibur", "Exeter",
                "Lexington", "Hood", "Intrepid", "Voyager");
        List<String> captains = Arrays.asList("Kirk", "Pike", "Decker", "Tracey", "Sulu", "Janeway", "Archer");

        Random rnd = new Random();

        for (int x = 0;
             x < 1000;
             x++) {
            repo.save(new Ship(shipNames.get(rnd.nextInt(shipNames.size())), captains.get(rnd.nextInt(captains.size()))));
        }

        repo.findAll().forEach(System.out::println);
    }
}

@RestController
@AllArgsConstructor
class ShipController {
    private final ShipRepository repo;

    @GetMapping("/ships")
    Iterable<Ship> getAllShips() {
        return repo.findAll();
    }

    @GetMapping("/ships/{id}")
    Optional<Ship> getShipById(@PathVariable String id) {
        return repo.findById(id);
    }

    @GetMapping("/search")
    Iterable<Ship> getShipByCaptain(@RequestParam(defaultValue = "Kirk") String captain) {
        return repo.findShipByCaptain(captain);
    }
}

interface ShipRepository extends CrudRepository<Ship, String> {
    Iterable<Ship> findShipByCaptain(String captain);
}

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Document
class Ship {
    @Id
    private String id;
    @NonNull
    private String name, captain;
}