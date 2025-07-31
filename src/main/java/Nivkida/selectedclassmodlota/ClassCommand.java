package Nivkida.selectedclassmodlota;

import Nivkida.selectedclassmodlota.item.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

import static net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG;
import static Nivkida.selectedclassmodlota.Selectedclassmodlota.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ClassCommand {
    private ClassCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("classcommand")
                        .then(Commands.literal("resetclass")
                                .requires(src -> src.hasPermission(4))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> resetClass(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets")
                                        ))
                                )
                        ));
    }

    private static int resetClass(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int successCount = 0;

        for (ServerPlayer player : targets) {
            CompoundTag root = player.getPersistentData();

            if (!root.contains("selected_class")) {
                source.sendFailure(Component.translatable(
                        "command.selectedclassmodlota.resetclass.failure",
                        player.getDisplayName()
                ));
                continue;
            }

            // Удаляем только данные о текущем классе
            root.remove("selected_class");

            // Выдаём селектор
            giveSelectorItem(player);

            // Сообщение источнику команды
            source.sendSuccess(() ->
                            Component.translatable("command.selectedclassmodlota.resetclass.success")
                                    .append(" ")
                                    .append(player.getDisplayName()),
                    false
            );

            // Сообщение игроку
            player.sendSystemMessage(Component.translatable("message.selectedclassmodlota.class_reset"));
            successCount++;
        }

        return successCount;
    }

    private static CompoundTag getPersistTag(CompoundTag root) {
        if (!root.contains(PERSISTED_NBT_TAG)) {
            root.put(PERSISTED_NBT_TAG, new CompoundTag());
        }
        return root.getCompound(PERSISTED_NBT_TAG);
    }

    private static void giveSelectorItem(ServerPlayer player) {
        ItemStack selector = new ItemStack(ModItems.CLASS_SELECTER.get());
        if (!player.getInventory().add(selector)) {
            player.drop(selector, false);
        }
    }
}