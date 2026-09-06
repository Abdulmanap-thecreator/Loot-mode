package com.lootmod.lootgenerator.network;

import com.lootmod.lootgenerator.tileentity.LootGeneratorChestTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateChancePacket {
    private final BlockPos pos;
    private final int slotIndex;
    private final int chance;

    public UpdateChancePacket(BlockPos pos, int slotIndex, int chance) {
        this.pos = pos;
        this.slotIndex = slotIndex;
        this.chance = chance;
    }

    public static void encode(UpdateChancePacket msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeInt(msg.slotIndex);
        buffer.writeInt(msg.chance);
    }

    public static UpdateChancePacket decode(PacketBuffer buffer) {
        return new UpdateChancePacket(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(UpdateChancePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null && player.isCreative()) {
                ServerWorld world = player.getLevel();
                if (world.isLoaded(msg.pos)) {
                    TileEntity te = world.getBlockEntity(msg.pos);
                    if (te instanceof LootGeneratorChestTileEntity) {
                        ((LootGeneratorChestTileEntity) te).setChance(msg.slotIndex, msg.chance);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}