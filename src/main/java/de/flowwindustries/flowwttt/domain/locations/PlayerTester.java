package de.flowwindustries.flowwttt.domain.locations;

import de.flowwindustries.flowwttt.domain.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * Entity for an arenas player tester.
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player_tester")
public class PlayerTester implements Identifiable<Integer> {

    /**
     * Database ID.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // TODO add block locations + button location
}
