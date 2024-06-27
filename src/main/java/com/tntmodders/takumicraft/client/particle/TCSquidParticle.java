package com.tntmodders.takumicraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TCSquidParticle extends SimpleAnimatedParticle {
    public TCSquidParticle(ClientLevel p_172325_, double p_172326_, double p_172327_, double p_172328_, double p_172329_, double p_172330_, double p_172331_, int p_172332_, SpriteSet p_172333_) {
        super(p_172325_, p_172326_, p_172327_, p_172328_, p_172333_, 0.0F);
        this.friction = 0.92F;
        this.quadSize = 0.5F;
        this.setAlpha(1.0F);
        this.setColor((float) FastColor.ARGB32.red(p_172332_), (float) FastColor.ARGB32.green(p_172332_), (float) FastColor.ARGB32.blue(p_172332_));
        this.lifetime = (int) ((double) (this.quadSize * 12.0F) / (Math.random() * (double) 0.8F + (double) 0.2F)) * 25;
        this.setSpriteFromAge(p_172333_);
        this.hasPhysics = false;
        this.xd = p_172329_;
        this.yd = p_172330_;
        this.zd = p_172331_;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            if (this.age > this.lifetime / 2) {
                this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
            }

            if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
                this.yd -= 0.0074F;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class GlowInkProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public GlowInkProvider(SpriteSet p_172336_) {
            this.sprites = p_172336_;
        }

        @Override
        public Particle createParticle(SimpleParticleType p_172347_, ClientLevel p_172348_, double p_172349_, double p_172350_, double p_172351_, double p_172352_, double p_172353_, double p_172354_) {
            return new TCSquidParticle(p_172348_, p_172349_, p_172350_, p_172351_, p_172352_, p_172353_, p_172354_, FastColor.ARGB32.color(255, 204, 31, 102), this.sprites);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TCSquidParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public TCSquidParticleProvider(SpriteSet p_107991_) {
            this.sprites = p_107991_;
        }

        @Override
        public Particle createParticle(SimpleParticleType p_108002_, ClientLevel p_108003_, double p_108004_, double p_108005_, double p_108006_, double p_108007_, double p_108008_, double p_108009_) {
            return new TCSquidParticle(p_108003_, p_108004_, p_108005_, p_108006_, p_108007_, p_108008_, p_108009_, FastColor.ARGB32.color(255, 255, 255, 255), this.sprites);
        }
    }
}
