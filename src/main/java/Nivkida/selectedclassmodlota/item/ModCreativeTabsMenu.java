package Nivkida.selectedclassmodlota.item;

import Nivkida.selectedclassmodlota.Selectedclassmodlota;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabsMenu {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Selectedclassmodlota.MODID);

    public static final RegistryObject<CreativeModeTab> CLASS_SELECTION_TAB = CREATIVE_MODE_TABS.register("class_selection_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CLASS_SELECTER.get()))
                    .title(Component.translatable("creativetab.selectedclassmodlota.class_selection"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.CLASS_SELECTER.get());
                        pOutput.accept(ModItems.CLASS_RESET.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}