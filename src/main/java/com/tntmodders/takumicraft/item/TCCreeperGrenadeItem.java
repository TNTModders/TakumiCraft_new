package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.entity.projectile.TCCreeperGrenade;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class TCCreeperGrenadeItem extends AbstractTCGrenadeItem {
    public TCCreeperGrenadeItem() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public String getRegistryName() {
        return "takumithrowgrenede";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Grenade";
    }

    @Override
    public String getJaJPName() {
        return "匠式手投爆弾";
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        TCCreeperGrenade grenade = new TCCreeperGrenade(position.x(), position.y(), position.z(), level);
        if (stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA).copyTag().contains("power")) {
            grenade.setPower(stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("power"));
        }
        grenade.setItem(stack);
        return grenade;
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
