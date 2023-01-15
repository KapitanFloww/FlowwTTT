package de.flowwindustries.flowwttt.domain.items;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChestItemJson {
    private String name;
    private String material;
    private Integer amount;
}
