package Nivkida.selectedclassmodlota.events;

import Nivkida.selectedclassmodlota.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG;
import static Nivkida.selectedclassmodlota.Selectedclassmodlota.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class PlayerJoinHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)  // запускаемся после всех остальных
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;           // строго на сервере

        CompoundTag root = player.getPersistentData();
        // если нет раздела PERSISTED_NBT_TAG — создаём
        if (!root.contains(PERSISTED_NBT_TAG)) {
            root.put(PERSISTED_NBT_TAG, new CompoundTag());
        }
        CompoundTag persist = root.getCompound(PERSISTED_NBT_TAG);

        // если ещё не давали
        if (!persist.getBoolean("class_select_item_given")) {
            persist.putBoolean("class_select_item_given", true);

            ItemStack selector = new ItemStack(ModItems.CLASS_SELECTER.get());

            // 1) пробуем положить в пустую ячейку хотбара (0–8)
            for (int slot = 0; slot < 9; slot++) {
                if (player.getInventory().getItem(slot).isEmpty()) {
                    player.getInventory().setItem(slot, selector);
                    return;
                }
            }

            // 2) иначе — ищем в остальной части инвентаря
            for (int slot = 9; slot < player.getInventory().getContainerSize(); slot++) {
                if (player.getInventory().getItem(slot).isEmpty()) {
                    player.getInventory().setItem(slot, selector);
                    return;
                }
            }

            // 3) если вообще нигде нет места — дропаем предмет прямо перед игроком
            player.drop(selector, false, true);
        }
    }
}


