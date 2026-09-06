package com.lootmod.lootgenerator.container;

import com.lootmod.lootgenerator.init.ModBlocks;
import com.lootmod.lootgenerator.init.ModContainers;
import com.lootmod.lootgenerator.tileentity.LootGeneratorChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LootGeneratorContainer extends Container {
    private final IInventory container;
    private final IWorldPosCallable access;
    public final LootGeneratorChestTileEntity tileEntity;

    // Client-side constructor
    public LootGeneratorContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory, getTileEntity(playerInventory, extraData));
    }

    // Server-side constructor
    public LootGeneratorContainer(int windowId, PlayerInventory playerInventory, LootGeneratorChestTileEntity tileEntity) {
        super(ModContainers.LOOT_GENERATOR_CONTAINER.get(), windowId);
        this.tileEntity = tileEntity;
        this.container = tileEntity;
        this.access = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        checkContainerSize(container, 27);
        container.startOpen(playerInventory.player);

        // 3x9 Container Inventory Slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player Main Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + col * 18, 8 + col * 18, 103 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 161));
        }
    }

    private static LootGeneratorChestTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();
        World world = playerInventory.player.level;
        if (world.getBlockEntity(pos) instanceof LootGeneratorChestTileEntity) {
            return (LootGeneratorChestTileEntity) world.getBlockEntity(pos);
        }
        throw new IllegalStateException("Tile entity at " + pos + " is not a LootGeneratorChestTileEntity!");
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.LOOT_GENERATOR_CHEST.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 27) {
                if (!this.moveItemStackTo(stackInSlot, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}