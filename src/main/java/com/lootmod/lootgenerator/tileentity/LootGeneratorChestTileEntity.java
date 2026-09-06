package com.lootmod.lootgenerator.tileentity;

import com.lootmod.lootgenerator.container.LootGeneratorContainer;
import com.lootmod.lootgenerator.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LootGeneratorChestTileEntity extends LockableLootTileEntity implements INamedContainerProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private final int[] itemChances = new int[27];

    public LootGeneratorChestTileEntity() {
        super(ModTileEntities.LOOT_GENERATOR_CHEST_TE.get());
        for (int i = 0; i < 27; i++) {
            itemChances[i] = 100; // Default 100% spawn rate
        }
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.lootmod.loot_generator_chest");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new LootGeneratorContainer(id, playerInventory, this);
    }

    public int getChance(int index) {
        if (index >= 0 && index < itemChances.length) {
            return itemChances[index];
        }
        return 0;
    }

    public void setChance(int index, int value) {
        if (index >= 0 && index < itemChances.length) {
            itemChances[index] = Math.max(0, Math.min(100, value));
            setChanged();
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAll(nbt, this.items);
        if (nbt.contains("ItemChances")) {
            int[] savedChances = nbt.getIntArray("ItemChances");
            System.arraycopy(savedChances, 0, this.itemChances, 0, Math.min(savedChances.length, this.itemChances.length));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAll(nbt, this.items);
        nbt.putIntArray("ItemChances", this.itemChances);
        return nbt;
    }

    /**
     * Procedural Surface Placement algorithm for 50x50 area
     */
    public void startGeneration(ServerWorld world) {
        Random rand = world.getRandom();
        BlockPos origin = this.getBlockPos();

        int maxAttempts = 30;
        BlockPos targetPos = null;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Pick random offsets between -25 and +25 (50x50 perimeter)
            int offsetX = rand.nextInt(51) - 25;
            int offsetZ = rand.nextInt(51) - 25;

            int checkX = origin.getX() + offsetX;
            int checkZ = origin.getZ() + offsetZ;

            // Get surface height
            int surfaceY = world.getHeight(Heightmap.Type.WORLD_SURFACE, checkX, checkZ);
            BlockPos candidatePos = new BlockPos(checkX, surfaceY, checkZ);
            BlockPos groundPos = candidatePos.below();

            BlockState groundState = world.getBlockState(groundPos);
            BlockState candidateState = world.getBlockState(candidatePos);

            // Validation logic: Solid floor below, replace-able or air at target position, no liquid
            boolean isValidGround = groundState.getMaterial().isSolid() && groundState.getFluidState().isEmpty();
            boolean isValidSpace = (candidateState.isAir() || candidateState.getMaterial().isReplaceable()) && candidateState.getFluidState().isEmpty();

            if (isValidGround && isValidSpace) {
                targetPos = candidatePos;
                break;
            }
        }

        // Fallback to origin's immediate surface if no valid random block was found in attempts
        if (targetPos == null) {
            int surfY = world.getHeight(Heightmap.Type.WORLD_SURFACE, origin.getX(), origin.getZ());
            targetPos = new BlockPos(origin.getX(), surfY, origin.getZ());
        }

        // Place standard Vanilla Chest
        world.setBlock(targetPos, Blocks.CHEST.defaultBlockState(), 3);

        TileEntity targetTE = world.getBlockEntity(targetPos);
        if (targetTE instanceof ChestTileEntity) {
            ChestTileEntity chestTE = (ChestTileEntity) targetTE;

            List<Integer> availableSlots = new ArrayList<>();
            for (int i = 0; i < 27; i++) {
                availableSlots.add(i);
            }
            Collections.shuffle(availableSlots, rand);

            int chestSlotPointer = 0;

            // Roll loot probabilities from template
            for (int slot = 0; slot < 27; slot++) {
                ItemStack templateStack = this.getItem(slot);
                if (!templateStack.isEmpty()) {
                    int chance = this.getChance(slot);
                    int roll = rand.nextInt(100) + 1; // 1 to 100

                    if (roll <= chance) {
                        if (chestSlotPointer < availableSlots.size()) {
                            int targetSlot = availableSlots.get(chestSlotPointer++);
                            // Safely copy ItemStack with full NBT preservation (weapons/TACZ guns retain attachments)
                            chestTE.setItem(targetSlot, templateStack.copy());
                        }
                    }
                }
            }
            chestTE.setChanged();
        }
    }
}