package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.world.level.material.TCHotspringFluid;
import com.tntmodders.takumicraft.world.level.material.TCHotspringFluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TCFluidCore {
    public static final DeferredRegister<FluidType> FLUID_TYPES_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, TakumiCraftCore.MODID);
    public static final DeferredRegister<Fluid> FLUIDS_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.FLUID.key(), TakumiCraftCore.MODID);

    public static final RegistryObject<FluidType> HOTSPRING_TYPE = FLUID_TYPES_SERIALIZERS.register("hotspring", TCHotspringFluidType::new);
    public static final RegistryObject<Fluid> HOTSPRING = FLUIDS_SERIALIZERS.register("hotspring", TCHotspringFluid.Source::new);
    public static final RegistryObject<Fluid> FLOWING_HOTSPRING = FLUIDS_SERIALIZERS.register("flowing_hotspring", TCHotspringFluid.Flowing::new);

    public static final TagKey<Fluid> HOTSPRINGS = TagKey.create(Registries.FLUID, ResourceLocation.tryBuild(TakumiCraftCore.MODID, "hotsprings"));
}
