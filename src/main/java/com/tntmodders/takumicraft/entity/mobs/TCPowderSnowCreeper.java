package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCPowderSnowCreeper extends AbstractTCCreeper {

    public TCPowderSnowCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 10;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.POWDER_SNOW;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {
            if (!event.getLevel().getBlockState(pos).isAir()) {
                event.getLevel().setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
            }
        });
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().forEach(entity -> entity.setTicksFrozen(140 + (this.isPowered() ? 200 : 100)));
        event.getAffectedEntities().clear();
    }

    public static class TCPowderSnowCreeperContext implements TCCreeperContext<TCPowderSnowCreeper> {
        private static final String NAME = "powdersnowcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCPowderSnowCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "こなゆきたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Powder, powder, fallin' snow.";
        }

        @Override
        public String getJaJPDesc() {
            return "こなああああああああああゆきいいいいいいいいいい";
        }

        @Override
        public String getEnUSName() {
            return "Powder Snow Creeper";
        }

        @Override
        public String getJaJPName() {
            return "粉雪匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xcceeff;
        }

        @Override
        public int getSecondaryColor() {
            return 0x88aa99;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WATER_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.SNOWBALL;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1, 8);
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS);
            list.add(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
            return list;
        }
    }
}
