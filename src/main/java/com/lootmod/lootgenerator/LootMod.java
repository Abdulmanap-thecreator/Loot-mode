package com.lootmod.lootgenerator;

import com.lootmod.lootgenerator.client.gui.LootGeneratorScreen;
import com.lootmod.lootgenerator.init.ModBlocks;
import com.lootmod.lootgenerator.init.ModContainers;
import com.lootmod.lootgenerator.init.ModTileEntities;
import com.lootmod.lootgenerator.network.SimpleChannelNetwork;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LootMod.MOD_ID)
public class LootMod {
    public static final String MOD_ID = "lootmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup CREATIVE_TAB = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.LOOT_GENERATOR_CHEST.get());
        }
    };

    public LootMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModTileEntities.TILE_ENTITIES.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SimpleChannelNetwork::registerMessages);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ModContainers.LOOT_GENERATOR_CONTAINER.get(), LootGeneratorScreen::new);
        });
    }
}