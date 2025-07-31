package Nivkida.selectedclassmodlota;

import Nivkida.selectedclassmodlota.events.PlayerJoinHandler;
import Nivkida.selectedclassmodlota.item.ModCreativeTabsMenu;
import Nivkida.selectedclassmodlota.item.ModItems;
import Nivkida.selectedclassmodlota.network.ModNetwork;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Selectedclassmodlota.MODID)
public class Selectedclassmodlota {
    public static final String MODID = "selectedclassmodlota";

    // Config specs
    private static final ForgeConfigSpec SPEC_GENERAL;
    private static final ForgeConfigSpec SPEC_RESET;
    public static final ForgeConfigSpec.IntValue MAX_SELF_RESETS;

    static {
        // GENERAL config (general.toml)
        ForgeConfigSpec.Builder generalBuilder = new ForgeConfigSpec.Builder();
        generalBuilder.push("general");
        // TODO: add general settings here, e.g.:
        // generalBuilder.comment("Enable feature X").define("enableX", true);
        generalBuilder.pop();
        SPEC_GENERAL = generalBuilder.build();

        // RESET config (reset.toml)
        ForgeConfigSpec.Builder resetBuilder = new ForgeConfigSpec.Builder();
        resetBuilder.push("reset");
        MAX_SELF_RESETS = resetBuilder
                .comment("Сколько раз игрок может сбросить свой класс через /classcommand resetclassself")
                .defineInRange("maxSelfResets", 1, 0, Integer.MAX_VALUE);
        resetBuilder.pop();
        SPEC_RESET = resetBuilder.build();
    }

    public Selectedclassmodlota() {
        // Регистрация конфигов (остаётся без изменений)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC_GENERAL, MODID + "/general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC_RESET, MODID + "/reset.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.COMMON_SPEC, MODID + "/items.toml");

        ModNetwork.register();

        // Регистрация предметов и вкладок
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeTabsMenu.register(modBus);
        ModItems.register(modBus);

        // Регистрация ивентов
        MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());

        // Регистрация сети теперь в CommonSetup
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // TODO: add server startup logic if needed
    }
}