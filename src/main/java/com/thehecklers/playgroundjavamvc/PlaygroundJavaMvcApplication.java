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
        String[] shipNames = "Enterprise, Constitution, Farragut, Defiant, Excalibur, Exeter, Lexington, Hood, Intrepid, Voyager".split(", ");
        String[] captains = "Kirk, Pike, Decker, Tracey, Sulu, Janeway, Archer".split(", ");
        Random rnd = new Random();

        for (int x = 0;
             x < 1000;
             x++) {
            repo.save(new Ship(shipNames[rnd.nextInt(shipNames.length)], captains[rnd.nextInt(captains.length)]));
        }

        repo.findAll().forEach(System.out::println);
    }
}

@RestController
@RequestMapping("/ships")
@AllArgsConstructor
class ShipController {
    private final ShipRepository repo;

    @GetMapping
    Iterable<Ship> getAllShips() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
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