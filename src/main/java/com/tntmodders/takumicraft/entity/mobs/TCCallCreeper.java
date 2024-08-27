package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.event.level.ExplosionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TCCallCreeper extends AbstractTCCreeper {
    private final EntityTypeTest<Entity, Creeper> typeTest = new EntityTypeTest<>() {
        @Nullable
        @Override
        public Creeper tryCast(Entity entity) {
            return entity instanceof Creeper ? (Creeper) entity : null;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Creeper.class;
        }
    };

    public TCCallCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 4;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.CALL;
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide()) {
            List<Creeper> list = this.level().getEntities(this.typeTest, this.getBoundingBox().inflate(this.isPowered() ? 2500 : 900), entity -> entity instanceof Creeper && entity.isAlive() && (!(entity instanceof AbstractTCCreeper creeper) || creeper.getContext().getRank().getLevel() < TCCreeperContext.EnumTakumiRank.HIGH.getLevel()));
            list.forEach(Creeper::ignite);
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().clear();
    }

    public static class TCCallCreeperContext implements TCCreeperContext<TCCallCreeper> {
        private static final String NAME = "callcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCCallCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "こしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Call of creeper, it makes destroying around.";
        }

        @Override
        public String getJaJPDesc() {
            return "この匠の呼び声、全ての破壊の始まりか、辺り一面爆発の音。";
        }

        @Override
        public String getEnUSName() {
            return "Call Creeper";
        }

        @Override
        public String getJaJPName() {
            return "呼匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xffff;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
