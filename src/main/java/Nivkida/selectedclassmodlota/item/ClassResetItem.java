package Nivkida.selectedclassmodlota.item;

import Nivkida.selectedclassmodlota.network.ModNetwork;
import Nivkida.selectedclassmodlota.network.OpenClassSelectionPacket;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ClassResetItem extends Item {

    public ClassResetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }

        // Проверяем, выбран ли класс
        if (!hasSelectedClass(serverPlayer)) {
            player.sendSystemMessage(Component.translatable("message.selectedclassmodlota.no_class_selected"));
            return InteractionResultHolder.fail(stack);
        }

        // Выполняем сброс класса
        resetPlayerClass(serverPlayer);

        // Выполняем команды сброса других систем
        executeResetCommands(serverPlayer);

        // Уменьшаем количество предметов
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // Отправляем пакет для открытия экрана выбора класса
        if (!level.isClientSide) {
            ModNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new OpenClassSelectionPacket(true)
            );
        }

        return InteractionResultHolder.success(stack);
    }

    private boolean hasSelectedClass(ServerPlayer player) {
        return player.getPersistentData().contains("selected_class");
    }

    private void resetPlayerClass(ServerPlayer player) {
        player.getPersistentData().remove("selected_class");
        player.sendSystemMessage(Component.translatable("message.selectedclassmodlota.class_reset_and_select"));
    }

    private void executeResetCommands(ServerPlayer player) {
        String playerName = player.getGameProfile().getName();
        executeServerCommand(player, "classcommand resetclass " + playerName);
        executeServerCommand(player, "skilltree reset " + playerName);
        executeServerCommand(player, "epicfight skill clear " + playerName);
    }

    private void executeServerCommand(ServerPlayer player, String command) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        CommandSourceStack source = new CommandSourceStack(
                CommandSource.NULL,
                player.position(),
                player.getRotationVector(),
                (ServerLevel) player.level(),
                4,
                player.getName().getString(),
                player.getDisplayName(),
                server,
                player
        );

        server.getCommands().performCommand(
                server.getCommands().getDispatcher().parse(
                        new StringReader(command),
                        source
                ),
                command
        );
    }
}