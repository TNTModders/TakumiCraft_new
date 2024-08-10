package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TCColorCreeper extends AbstractTCCreeper {

    private final List<String> colorList = new ArrayList<>();

    public TCColorCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        Arrays.stream(DyeColor.values()).forEach(c -> colorList.add(c.getName().toLowerCase()));
        this.explosionRadius = 6;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.COLOR;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {
            BlockState state = this.level().getBlockState(pos);
            if (!state.isAir()) {
                String colorName = "";
                String namespace = ForgeRegistries.BLOCKS.getKey(state.getBlock()).getNamespace();
                String path = ForgeRegistries.BLOCKS.getKey(state.getBlock()).getPath();
                for (String color : colorList) {
                    if (path.contains(color)) {
                        colorName = path.replace(color, "@").replace("light_", "").replace("dark_", "");
                        break;
                    }
                }
                if (!colorName.isEmpty()) {
                    List<Block> blocks = new ArrayList<>();
                    final String base = colorName;
                    colorList.forEach(s -> {
                        String name = base.replace("@", s);
                        blocks.add(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, name)));
                    });

                    if (!blocks.isEmpty()) {
                        if (!(state.getBlock() instanceof BedBlock)) {
                            BlockEntity blockEntity = this.level().getBlockEntity(pos);
                            BlockState newState = blocks.get(this.random.nextInt(blocks.size())).withPropertiesOf(state);
                            this.level().setBlockAndUpdate(pos, newState);
                            if (blockEntity != null) {
                                blockEntity.setBlockState(state);
                                this.level().setBlockEntity(blockEntity);
                            }
                        }
                    }
                }
            }
        });
        event.getAffectedBlocks().clear();

        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof Painting painting) {
                List<Holder<PaintingVariant>> list = new ArrayList<>();
                this.level().registryAccess().registryOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
                if (!list.isEmpty()) {
                    list.removeIf(holder -> {
                        painting.setVariant(holder);
                        return !painting.survives();
                    });
                    if (!list.isEmpty()) {
                        int i = list.stream().mapToInt(holder -> holder.value().area()).max().orElse(0);
                        list.removeIf(holder -> holder.value().area() < i);
                        Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, this.getRandom());
                        optional.ifPresent(painting::setVariant);
                    }
                }
            }
        });
        event.getAffectedEntities().clear();
    }

    public static class TCColorCreeperContext implements TCCreeperContext<TCColorCreeper> {
        private static final String NAME = "colorcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCColorCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "いろたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Changes color of blocks. Colorful world for you!";
        }

        @Override
        public String getJaJPDesc() {
            return "ブロックの色や形を変える厄介者。せっかくの絵画も台無しに……";
        }

        @Override
        public String getEnUSName() {
            return "Color Creeper";
        }

        @Override
        public String getJaJPName() {
            return "色匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x66ff66;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
