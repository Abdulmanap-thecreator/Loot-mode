package com.lootmod.lootgenerator.init;

import com.lootmod.lootgenerator.LootMod;
import com.lootmod.lootgenerator.tileentity.LootGeneratorChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, LootMod.MOD_ID);

    public static final RegistryObject<TileEntityType<LootGeneratorChestTileEntity>> LOOT_GENERATOR_CHEST_TE = TILE_ENTITIES.register(
            "loot_generator_chest_te",
            () -> TileEntityType.Builder.of(LootGeneratorChestTileEntity::new, ModBlocks.LOOT_GENERATOR_CHEST.get()).build(null)
    );
}