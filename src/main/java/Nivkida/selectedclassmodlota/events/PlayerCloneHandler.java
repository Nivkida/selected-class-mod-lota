package Nivkida.selectedclassmodlota.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "selectedclassmodlota")
public class PlayerCloneHandler {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        CompoundTag originalData = original.getPersistentData();

        // Копируем данные класса при возрождении
        if (originalData.contains("selected_class")) {
            newPlayer.getPersistentData().putString("selected_class",
                    originalData.getString("selected_class"));
        }

        // Копируем флаг выдачи предметов
        if (originalData.contains("class_items_given_once")) {
            newPlayer.getPersistentData().putBoolean("class_items_given_once",
                    originalData.getBoolean("class_items_given_once"));
        }
    }
}