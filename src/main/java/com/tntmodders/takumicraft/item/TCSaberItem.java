package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class TCSaberItem extends AbstractTCWeaponItem {
    public TCSaberItem() {
        super(AbstractTCCreeper.TCCreeperContext.EnumTakumiElement.NORMAL, 3, -2.4f, new Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public float getDamage() {
        return 20f;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity enemy, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            attacker.level().explode(enemy, enemy.getX(), enemy.getY(), enemy.getZ(), 0f, Level.ExplosionInteraction.NONE);
        }
        return super.hurtEnemy(stack, enemy, attacker);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new TCBEWLRenderer();
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!p_41404_.isEnchanted()) {
            p_41404_.enchant(TCEnchantmentCore.ANTI_POWERED, 1);
        }
    }

    @Override
    public String getEnUSName() {
        return "Takumi Saber";
    }

    @Override
    public String getJaJPName() {
        return "匠式極光剣";
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SP;
    }
}
