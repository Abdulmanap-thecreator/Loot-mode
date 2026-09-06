package com.lootmod.lootgenerator.init;

import com.lootmod.lootgenerator.LootMod;
import com.lootmod.lootgenerator.container.LootGeneratorContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, LootMod.MOD_ID);

    public static final RegistryObject<ContainerType<LootGeneratorContainer>> LOOT_GENERATOR_CONTAINER = CONTAINERS.register(
            "loot_generator_container",
            () -> IForgeContainerType.create(LootGeneratorContainer::new)
    );
}