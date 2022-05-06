package de.flowwindustries.flowwttt.domain.locations;

import de.flowwindustries.flowwttt.domain.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity for an arenas player tester.
 */
@Data
@Entity
public class PlayerTester implements Identifiable<Integer> {

    /**
     * Database ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // TODO add block locations + button location
}
