package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.entity.mobs.TCLlamaCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TCLlamaCreeperSpit extends LlamaSpit {
    public static final EntityType<TCLlamaCreeperSpit> LLAMA_SPIT = EntityType.Builder.<TCLlamaCreeperSpit>of(TCLlamaCreeperSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(TCEntityUtils.TCEntityId("llamacreeperspit"));

    public TCLlamaCreeperSpit(EntityType<? extends TCLlamaCreeperSpit> p_37224_, Level p_37225_) {
        super(p_37224_, p_37225_);
    }

    public TCLlamaCreeperSpit(Level p_37235_, TCLlamaCreeper p_37236_) {
        this(LLAMA_SPIT, p_37235_);
        this.setOwner(p_37236_);
        this.setPos(
                p_37236_.getX() - (double) (p_37236_.getBbWidth() + 1.0F) * 0.5 * (double) Mth.sin(p_37236_.yBodyRot * (float) (Math.PI / 180.0)),
                p_37236_.getEyeY() - 0.1F,
                p_37236_.getZ() + (double) (p_37236_.getBbWidth() + 1.0F) * 0.5 * (double) Mth.cos(p_37236_.yBodyRot * (float) (Math.PI / 180.0))
        );
    }

    @Override
    protected void onHitEntity(EntityHitResult p_37241_) {
        this.onHitExplode(p_37241_);
        super.onHitEntity(p_37241_);
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37239_) {
        this.onHitExplode(p_37239_);
        super.onHitBlock(p_37239_);
    }

    private void onHitExplode(HitResult result) {
        if (!this.level().isClientSide()) {
            TCExplosionUtils.createExplosion(this.level(), this, result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), 2f, false);
        }
    }
}
