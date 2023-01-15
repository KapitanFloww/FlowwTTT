package de.flowwindustries.flowwttt.items;

public class ItemJsonDefaults {

    public static final String DEFAULT_ITEMS = """
            [
              {
                "name": "Training Sword",
                "material": "WOODEN_SWORD",
                "amount": 1
              },
              {
                "name": "Blunt Sword",
                "material": "STONE_SWORD",
                "amount": 1
              },
              {
                "name": "Hunting Bow",
                "material": "BOW",
                "amount": 1
              },
              {
                "name": "Arrow Set",
                "material": "ARROW",
                "amount": 32
              }
            ]
            """;

    public static final String LEGENDARY_ITEMS = """
            [
              {
                "name": "Steel Sword",
                "material": "IRON_SWORD",
                "amount": 1
              },
              {
                "name": "Katana",
                "material": "IRON_SWORD",
                "amount": 1
              }
            ]
            """;

    private ItemJsonDefaults() {}
}
