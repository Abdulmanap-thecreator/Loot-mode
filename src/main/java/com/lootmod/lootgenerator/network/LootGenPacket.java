package com.lootmod.lootgenerator.network;

import com.lootmod.lootgenerator.tileentity.LootGeneratorChestTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class LootGenPacket {
    private final BlockPos pos;

    public LootGenPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(LootGenPacket msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.pos);
    }

    public static LootGenPacket decode(PacketBuffer buffer) {
        return new LootGenPacket(buffer.readBlockPos());
    }

    public static void handle(LootGenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null && player.isCreative()) {
                ServerWorld world = player.getLevel();
                if (world.isLoaded(msg.pos)) {
                    TileEntity te = world.getBlockEntity(msg.pos);
                    if (te instanceof LootGeneratorChestTileEntity) {
                        ((LootGeneratorChestTileEntity) te).startGeneration(world);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}