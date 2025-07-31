package Nivkida.selectedclassmodlota.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("selectedclassmodlota", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // Регистрируем оба пакета

        INSTANCE.registerMessage(packetId++,
                ClassSelectPacket.class,
                ClassSelectPacket::encode,
                ClassSelectPacket::decode,
                ClassSelectPacket::handle
        );

        INSTANCE.registerMessage(packetId++,
                OpenClassSelectionPacket.class,
                OpenClassSelectionPacket::encode,
                OpenClassSelectionPacket::decode,
                OpenClassSelectionPacket::handle
        );
    }
}
