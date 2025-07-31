package Nivkida.selectedclassmodlota.network;

import Nivkida.selectedclassmodlota.ModConfigHandler;
import Nivkida.selectedclassmodlota.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ClassSelectPacket {
    private final String selectedClass;

    public ClassSelectPacket(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public static void encode(ClassSelectPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.selectedClass);
    }

    public static ClassSelectPacket decode(FriendlyByteBuf buf) {
        return new ClassSelectPacket(buf.readUtf());
    }

    public static void handle(ClassSelectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.level().isClientSide()) return;

            CompoundTag data = player.getPersistentData();

            // Проверяем, выдавались ли уже предметы для любого класса
            boolean itemsGiven = data.getBoolean("class_items_given_once");

            // Локализованное название класса
            Component translatedName = Component.translatable("class.selectedclassmodlota." + msg.selectedClass);

            // Обновляем класс
            data.putString("selected_class", msg.selectedClass);

            // Удаление предмета выбора (только если это не сброс класса)
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.getItem() == ModItems.CLASS_SELECTER.get()) {
                mainHandItem.shrink(1); // Уменьшаем количество на 1
            }

            // Выдача предметов (только если еще не выдавались)
            if (!itemsGiven) {
                List<? extends String> items = ModConfigHandler.CONFIG.getClassItems().get(msg.selectedClass);
                if (items != null) {
                    for (String itemStr : items) {
                        String[] parts = itemStr.split(",");
                        ResourceLocation loc = ResourceLocation.tryParse(parts[0]);
                        if (loc == null || !ForgeRegistries.ITEMS.containsKey(loc))
                            continue;

                        int count = 1;
                        if (parts.length > 1) {
                            try {
                                count = Integer.parseInt(parts[1].trim());
                            } catch (NumberFormatException e) {
                                // обработка ошибки
                            }
                        }

                        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(loc), count);

                        CompoundTag tag = stack.getOrCreateTag();
                        tag.putBoolean("given_by_class", true);
                        tag.putString("class", msg.selectedClass);

                        // Локализация лора
                        CompoundTag displayTag = new CompoundTag();
                        ListTag loreList = new ListTag();

                        loreList.add(StringTag.valueOf(Component.Serializer.toJson(
                                Component.translatable("tooltip.selectedclassmodlota.given_by_class")
                                        .append(": ")
                                        .append(translatedName)
                        )));

                        displayTag.put("Lore", loreList);
                        tag.put("display", displayTag);
                        stack.setTag(tag);

                        player.getInventory().add(stack);
                    }
                }

                // ПОМЕЧАЕМ НАВСЕГДА, ЧТО ПРЕДМЕТЫ БЫЛИ ВЫДАНЫ
                data.putBoolean("class_items_given_once", true);
            }

            // Отправляем сообщение игроку
            if (itemsGiven) {
                player.sendSystemMessage(
                        Component.translatable("message.selectedclassmodlota.class_selected")
                                .append(" ")
                                .append(translatedName)
                );
            } else {
                player.sendSystemMessage(
                        Component.translatable("message.selectedclassmodlota.class_selected_with_items")
                                .append(" ")
                                .append(translatedName)
                );
            }
        });
        context.setPacketHandled(true);
    }
}