package de.flowwindustries.flowwttt.domain.items;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Material;

@Getter
@ToString
@RequiredArgsConstructor
public class ChestItem {
    private final String name;
    private final Material material;
    private final Integer amount;
}
