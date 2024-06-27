package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TCBlastPoweredContext extends AbstractTCEnchantmentContext {
    private final Enchantment enchantment;

    public TCBlastPoweredContext(BootstrapContext<Enchantment> bootstrap) {
        super(bootstrap);
        this.enchantment = Enchantment.enchantment(Enchantment.definition(bootstrap.lookup(Registries.ITEM).getOrThrow(TCItemCore.BLAST_POWERED), 8, 1, Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4, EquipmentSlotGroup.MAINHAND)).withEffect(
                EnchantmentEffectComponents.POST_ATTACK,
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.ATTACKER,
                new ExplodeEffect(
                        false,
                        Optional.empty(),
                        Optional.of(LevelBasedValue.lookup(List.of(3F, 3.7F, 4.4F), LevelBasedValue.perLevel(2.3F, 0.7F))),
                        bootstrap.lookup(Registries.BLOCK).get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()),
                        Vec3.ZERO,
                        LevelBasedValue.constant(3.5F),
                        false,
                        Level.ExplosionInteraction.TRIGGER,
                        ParticleTypes.EXPLOSION,
                        ParticleTypes.EXPLOSION,
                        SoundEvents.WIND_CHARGE_BURST
                ),
                LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.DIRECT_ATTACKER,
                        EntityPredicate.Builder.entity()
                                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                .moving(MovementPredicate.fallDistance(MinMaxBounds.Doubles.atLeast(1.5)))
                )
        ).build(ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName()));
    }

    @Override
    public String getRegistryName() {
        return "blast_powered";
    }

    @Override
    public String getEnUSName() {
        return "Blast Powered";
    }

    @Override
    public String getJaJPName() {
        return "巨匠跳躍";
    }

    @Override
    public Enchantment getEnchantment() {
        return enchantment;
    }
}
