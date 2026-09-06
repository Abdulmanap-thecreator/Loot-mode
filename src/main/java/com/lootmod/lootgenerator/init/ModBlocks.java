package com.lootmod.lootgenerator.init;

import com.lootmod.lootgenerator.LootMod;
import com.lootmod.lootgenerator.block.LootGeneratorChestBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LootMod.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LootMod.MOD_ID);

    public static final RegistryObject<Block> LOOT_GENERATOR_CHEST = BLOCKS.register("loot_generator_chest",
            () -> new LootGeneratorChestBlock(AbstractBlock.Properties.of(Material.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<Item> LOOT_GENERATOR_CHEST_ITEM = ITEMS.register("loot_generator_chest",
            () -> new BlockItem(LOOT_GENERATOR_CHEST.get(), new Item.Properties().tab(LootMod.CREATIVE_TAB)));
}