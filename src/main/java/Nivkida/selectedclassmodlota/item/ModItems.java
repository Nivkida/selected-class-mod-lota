package Nivkida.selectedclassmodlota.item;

import Nivkida.selectedclassmodlota.Selectedclassmodlota;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Selectedclassmodlota.MODID);

    public static final RegistryObject<Item> CLASS_SELECTER = ITEMS.register("class_selecter",
            () -> new ClassSelectItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.selectedclassmodlota.class_selecter"));
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });

    public static final RegistryObject<Item> CLASS_RESET = ITEMS.register("class_reset",
            () -> new ClassResetItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.selectedclassmodlota.class_reset"));
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}