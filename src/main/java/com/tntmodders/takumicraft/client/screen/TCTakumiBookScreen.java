package com.tntmodders.takumicraft.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class TCTakumiBookScreen extends Screen {
    public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
    public static final int PAGE_TEXT_X_OFFSET = 36;
    public static final int PAGE_TEXT_Y_OFFSET = 30;
    public static final TCTakumiBookScreen.BookAccess EMPTY_ACCESS = new TCTakumiBookScreen.BookAccess() {
        @Override
        public int getPageCount() {
            return TCEntityCore.ENTITY_CONTEXTS.size();
        }

        @Override
        public FormattedText getPageRaw(int p_98306_) {
            return FormattedText.EMPTY;
        }
    };
    protected static final int TEXT_WIDTH = 114;
    protected static final int TEXT_HEIGHT = 128;
    protected static final int IMAGE_WIDTH = 192;
    protected static final int IMAGE_HEIGHT = 192;
    private static final ResourceLocation BOOK_GUI_TEXTURES = ResourceLocation.tryBuild("minecraft", "textures/gui/book.png");
    private static final ResourceLocation BOOK_GUI_TEXTURES_BOSS =
            ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/book_boss.png");
    private static int currentPage;
    public final NonNullList<AbstractTCCreeper> creepers = NonNullList.create();
    private final boolean playTurnSound;
    private TCTakumiBookScreen.BookAccess bookAccess;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
    private int cachedPage = -1;
    private PageButton forwardButton;
    private PageButton backButton;

    private int tick = 0;

    public TCTakumiBookScreen() {
        super(GameNarrator.NO_TITLE);
        this.bookAccess = EMPTY_ACCESS;
        this.playTurnSound = false;
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
            if (context.entityType().create(Minecraft.getInstance().level) instanceof AbstractTCCreeper creeper) {
                creepers.add(creeper);
            }
        });
    }

    public TCTakumiBookScreen(int page) {
        this();
        currentPage = page;
    }

    static List<String> loadPages(CompoundTag p_169695_) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        loadPages(p_169695_, builder::add);
        return builder.build();
    }

    public static void loadPages(CompoundTag p_169697_, Consumer<String> p_169698_) {
        ListTag listtag = p_169697_.getList("pages", 8).copy();
        IntFunction<String> intfunction;
        if (Minecraft.getInstance().isTextFilteringEnabled() && p_169697_.contains("filtered_pages", 10)) {
            CompoundTag compoundtag = p_169697_.getCompound("filtered_pages");
            intfunction = p_169702_ -> {
                String s = String.valueOf(p_169702_);
                return compoundtag.contains(s) ? compoundtag.getString(s) : listtag.getString(p_169702_);
            };
        } else {
            intfunction = listtag::getString;
        }

        for (int i = 0; i < listtag.size(); ++i) {
            p_169698_.accept(intfunction.apply(i));
        }

    }

    public void setBookAccess(TCTakumiBookScreen.BookAccess p_98289_) {
        this.bookAccess = p_98289_;
        currentPage = Mth.clamp(currentPage, 0, p_98289_.getPageCount());
        this.updateButtonVisibility();
        this.cachedPage = -1;
    }

    public boolean setPage(int p_98276_) {
        int i = Mth.clamp(p_98276_, 0, this.bookAccess.getPageCount() - 1);
        if (i != currentPage) {
            currentPage = i;
            this.updateButtonVisibility();
            this.cachedPage = -1;
            return true;
        } else {
            return false;
        }
    }

    protected boolean forcePage(int p_98295_) {
        return this.setPage(p_98295_);
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(
                        Component.translatable("takumicraft.takumibook.search"),
                        p_98299_ -> this.minecraft.setScreen(new TCTakumiBookOutlineScreen(Minecraft.getInstance().player, currentPage)))
                .bounds((this.width - IMAGE_WIDTH) / 2 + 40, 140, 105, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                p_98299_ -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 100, 206, 200, 20).build());
    }

    protected void createPageControlButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.forwardButton = this.addRenderableWidget(new PageButton(i + 116, 159, true, p_98297_ -> this.pageForward(), this.playTurnSound));
        this.backButton = this.addRenderableWidget(new PageButton(i + 43, 159, false, p_98287_ -> this.pageBack(), this.playTurnSound));
        this.updateButtonVisibility();
    }

    private int getNumPages() {
        return this.bookAccess.getPageCount();
    }

    protected void pageBack() {
        if (currentPage > 0) {
            --currentPage;
        }

        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (currentPage < this.getNumPages() - 1) {
            ++currentPage;
        }

        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = currentPage < this.getNumPages() - 1;
        this.backButton.visible = currentPage > 0;
    }

    @Override
    public boolean keyPressed(int p_98278_, int p_98279_, int p_98280_) {
        if (super.keyPressed(p_98278_, p_98279_, p_98280_)) {
            return true;
        } else {
            switch (p_98278_) {
                case 266 -> {
                    this.backButton.onPress();
                    return true;
                }
                case 267 -> {
                    this.forwardButton.onPress();
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics p_301081_, int p_297765_, int p_300192_, float p_297977_) {
        super.renderBackground(p_301081_, p_297765_, p_300192_, p_297977_);
        p_301081_.blit(BOOK_GUI_TEXTURES, (this.width - 192) / 2, 2, 0, 0, 192, 192);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float p_98285_) {
        this.renderBackground(graphics, mouseX, mouseY, p_98285_);
        super.render(graphics, mouseX, mouseY, p_98285_);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BOOK_GUI_TEXTURES);
        int i = (this.width - 192) / 2;
        int j = 2;
        AbstractTCCreeper.TCCreeperContext<? extends AbstractTCCreeper> context = TCEntityCore.ENTITY_CONTEXTS.get(currentPage);
        this.tick++;
        TCClientUtils.renderEntity(graphics.pose(), i + 51, j + 80, 30, this.tick / 100f, 0, context.entityType(), false);
        boolean flg = TCClientUtils.checkSlayAdv(context.entityType());
        if (this.cachedPage != currentPage) {
            FormattedText formattedtext = Component.translatable(flg ? context.entityType().getDescriptionId() + ".desc" : "???");
            this.cachedPageComponents = this.font.split(formattedtext, 114);
        }
        this.cachedPage = currentPage;
        int k = Math.min(128 / 9, this.cachedPageComponents.size());
        for (int l = 0; l < k; ++l) {
            FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
            graphics.drawString(this.font, formattedcharsequence, i + 40, 100 + l * 9, 0, false);
        }

        Component name = flg ? TCEntityUtils.getEntityName(context.entityType()) : TCEntityUtils.getUnknown();
        graphics.drawString(this.font, name, i + 80, 34, 0, false);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation location;
        if (flg) {
            location = context.getElement().getIcon();
        } else {
            location = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/underfound.png");
        }
        RenderSystem.setShaderTexture(0, location);
        graphics.blit(location, i, 2, 0, 0, 192, 192);
        if (flg) {
            if (context.getElement().isDest()) {
                location = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/dest.png");
                RenderSystem.setShaderTexture(0, location);
                graphics.blit(location, i, 2, 0, 0, 192, 192);
            }
            if (context.getElement().isMagic()) {
                location = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/magic.png");
                RenderSystem.setShaderTexture(0, location);
                graphics.blit(location, i, 2, 0, 0, 192, 192);
            }
        }
        if (context.showRead()) {
            Component read = Component.translatable(flg ? TCEntityUtils.getEntityLangCode(context.entityType(), ".read") : "???");
            graphics.drawString(this.font, read, i + 70, 25, 0, false);
        }
        graphics.drawString(this.font, Component.literal(currentPage + 1 + "/" + this.getNumPages()), i + 120, 15, 0, false);
    }

    @Override
    public boolean mouseClicked(double p_98272_, double p_98273_, int p_98274_) {
        if (p_98274_ == 0) {
            Style style = this.getClickedComponentStyleAt(p_98272_, p_98273_);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }
        return super.mouseClicked(p_98272_, p_98273_, p_98274_);
    }

    @Override
    public boolean handleComponentClicked(Style p_98293_) {
        ClickEvent clickevent = p_98293_.getClickEvent();
        if (clickevent == null) {
            return false;
        } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String s = clickevent.getValue();

            try {
                int i = Integer.parseInt(s) - 1;
                return this.forcePage(i);
            } catch (Exception exception) {
                return false;
            }
        } else {
            boolean flag = super.handleComponentClicked(p_98293_);
            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.closeScreen();
            }

            return flag;
        }
    }

    protected void closeScreen() {
        this.minecraft.setScreen(null);
    }

    @Nullable
    public Style getClickedComponentStyleAt(double p_98269_, double p_98270_) {
        if (this.cachedPageComponents.isEmpty()) {
            return null;
        } else {
            int i = Mth.floor(p_98269_ - (double) ((this.width - 192) / 2) - 36.0D);
            int j = Mth.floor(p_98270_ - 2.0D - 30.0D);
            if (i >= 0 && j >= 0) {
                int k = Math.min(128 / 9, this.cachedPageComponents.size());
                if (i <= 114 && j < 9 * k + k) {
                    int l = j / 9;
                    if (l >= 0 && l < this.cachedPageComponents.size()) {
                        FormattedCharSequence formattedcharsequence = this.cachedPageComponents.get(l);
                        return this.minecraft.font.getSplitter().componentStyleAtWidth(formattedcharsequence, i);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface BookAccess {

        int getPageCount();

        FormattedText getPageRaw(int p_98307_);

        default FormattedText getPage(int p_98311_) {
            return p_98311_ >= 0 && p_98311_ < this.getPageCount() ? this.getPageRaw(p_98311_) : FormattedText.EMPTY;
        }
    }
}