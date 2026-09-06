package com.lootmod.lootgenerator.network;

import com.lootmod.lootgenerator.LootMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SimpleChannelNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LootMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void registerMessages() {
        INSTANCE.registerMessage(
                packetId++,
                LootGenPacket.class,
                LootGenPacket::encode,
                LootGenPacket::decode,
                LootGenPacket::handle
        );

        INSTANCE.registerMessage(
                packetId++,
                UpdateChancePacket.class,
                UpdateChancePacket::encode,
                UpdateChancePacket::decode,
                UpdateChancePacket::handle
        );
    }
}