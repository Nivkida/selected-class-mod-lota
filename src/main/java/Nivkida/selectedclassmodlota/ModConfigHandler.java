package Nivkida.selectedclassmodlota;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModConfigHandler {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ModConfig CONFIG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CONFIG = new ModConfig(builder);
        COMMON_SPEC = builder.build();
    }

    public static class ModConfig {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> knightItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> tankItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> berserkItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> samuraiItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> assasinItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> archerItems;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> wizardItems;

        public ModConfig(ForgeConfigSpec.Builder builder) {
            builder.push("class_items");

            knightItems = builder.comment("Items for knight").defineListAllowEmpty("knight", List.of("minecraft:iron_sword", "minecraft:bread, 16"), o -> true);
            tankItems = builder.comment("Items for tank").defineListAllowEmpty("tank", List.of("minecraft:iron_chestplate"), o -> true);
            berserkItems = builder.comment("Items for berserk").defineListAllowEmpty("berserk", List.of("minecraft:stone_axe"), o -> true);
            samuraiItems = builder.comment("Items for samurai").defineListAllowEmpty("samurai", List.of("minecraft:iron_sword"), o -> true);
            assasinItems = builder.comment("Items for assasin").defineListAllowEmpty("assasin", List.of("minecraft:stone_sword"), o -> true);
            archerItems = builder.comment("Items for archer").defineListAllowEmpty("archer", List.of("minecraft:bow", "minecraft:arrow, 64"), o -> true);
            wizardItems = builder.comment("Items for wizard").defineListAllowEmpty("wizard", List.of("minecraft:stick"), o -> true);

            builder.pop();
        }

        public Map<String, List<? extends String>> getClassItems() {
            Map<String, List<? extends String>> map = new HashMap<>();
            map.put("knight", knightItems.get());
            map.put("tank", tankItems.get());
            map.put("berserk", berserkItems.get());
            map.put("samurai", samuraiItems.get());
            map.put("assasin", assasinItems.get());
            map.put("archer", archerItems.get());
            map.put("wizard", wizardItems.get());
            return map;
        }
    }
}

