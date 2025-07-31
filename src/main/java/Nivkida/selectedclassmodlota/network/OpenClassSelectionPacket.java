package Nivkida.selectedclassmodlota.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import Nivkida.selectedclassmodlota.UI.ClassSelectScreen;

import java.util.function.Supplier;

public class OpenClassSelectionPacket {
    private final boolean fromReset;

    public OpenClassSelectionPacket(boolean fromReset) {
        this.fromReset = fromReset;
    }

    public static void encode(OpenClassSelectionPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.fromReset);
    }

    public static OpenClassSelectionPacket decode(FriendlyByteBuf buf) {
        return new OpenClassSelectionPacket(buf.readBoolean());
    }

    public static void handle(OpenClassSelectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Используем безопасный метод для работы с клиентской стороной
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Открываем экран в главном потоке рендеринга
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().setScreen(
                                new ClassSelectScreen(() -> {
                                    // Коллбэк при закрытии окна
                                    if (!msg.fromReset) {
                                        // Удаление предмета должно происходить на сервере!
                                        // Перенесено в ClassSelectPacket
                                    }
                                })
                        );
                    }
                });
            });
        });
        ctx.get().setPacketHandled(true);
    }
}