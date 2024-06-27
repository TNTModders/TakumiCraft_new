package com.tntmodders.takumicraft.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.client.TCSearchTreeCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TCTakumiBookOutlineScreen extends EffectRenderingInventoryScreen<TCTakumiBookOutlineScreen.TakumiPickerMenu> {
    static final SimpleContainer CONTAINER = new SimpleContainer(45);
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.tryBuild("minecraft", "container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.tryBuild("minecraft", "container/creative_inventory/scroller_disabled");
    private static final ResourceLocation SEARCH_GUI_TEXTURES = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/book_search.png");
    private static final String GUI_CREATIVE_TAB_PREFIX = "textures/gui/container/creative_inventory/tab_";
    private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 9;
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
    private static final int TEXT_COLOR = 16777215;
    private static CreativeModeTab selectedTab = CreativeModeTabs.searchTab();
    private final Set<TagKey<Item>> visibleTags = new HashSet<>();
    private final List<net.minecraftforge.client.gui.CreativeTabsScreenPage> pages = new java.util.ArrayList<>();
    private float scrollOffs;
    private boolean scrolling;
    private EditBox searchBox;
    @Nullable
    private List<Slot> originalSlots;
    @Nullable
    private Slot destroyItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTextInput;
    private boolean hasClickedOutside;
    private net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage = new net.minecraftforge.client.gui.CreativeTabsScreenPage(new java.util.ArrayList<>());
    private int tick = 0;

    private int lastPage = 0;

    public TCTakumiBookOutlineScreen(Player p_259788_) {
        super(new TCTakumiBookOutlineScreen.TakumiPickerMenu(p_259788_), p_259788_.getInventory(), CommonComponents.EMPTY);
        p_259788_.containerMenu = this.menu;
        this.imageHeight = 136;
        this.imageWidth = 195;
    }

    public TCTakumiBookOutlineScreen(Player player, int lastPage) {
        this(player);
        this.lastPage = lastPage;
    }

    private boolean hasPermissions(Player p_259959_) {
        return true;
    }

    private void tryRefreshInvalidatedTabs(FeatureFlagSet p_259501_, boolean p_259713_, HolderLookup.Provider p_270898_) {
        if (CreativeModeTabs.tryRebuildTabContents(p_259501_, p_259713_, p_270898_)) {
            for (CreativeModeTab creativemodetab : CreativeModeTabs.allTabs()) {
                Collection<ItemStack> collection = creativemodetab.getDisplayItems();
                if (creativemodetab == selectedTab) {
                    if (creativemodetab.getType() == CreativeModeTab.Type.CATEGORY && collection.isEmpty()) {
                        this.selectTab(CreativeModeTabs.getDefaultTab());
                    } else {
                        this.refreshCurrentTabContents(collection);
                    }
                }
            }
        }

    }

    private void refreshCurrentTabContents(Collection<ItemStack> p_261591_) {
        int i = this.menu.getRowIndexForScroll(this.scrollOffs);
        this.menu.items.clear();
        if (selectedTab.hasSearchBar()) {
            this.refreshSearchResults();
        } else {
            this.menu.items.addAll(p_261591_);
        }

        this.scrollOffs = this.menu.getScrollForRowIndex(i);
        this.menu.scrollTo(this.scrollOffs);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.tick++;
        if (this.minecraft != null) {
            if (this.minecraft.player != null) {
                this.tryRefreshInvalidatedTabs(this.minecraft.player.connection.enabledFeatures(), this.hasPermissions(this.minecraft.player), this.minecraft.player.level().registryAccess());
            }
            //this.searchBox.tick();
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int p_98557_, int p_98558_, ClickType type) {
        if (type == ClickType.CLONE && this.minecraft.player.isCreative()) {
            if (this.menu.getCarried().isEmpty() && slot.hasItem()) {
                ItemStack itemstack9 = slot.getItem();
                this.minecraft.player.addItem(itemstack9.copyWithCount(itemstack9.getMaxStackSize()));
            }
        } else {
            if (slot != null && slot.getItem().getItem() instanceof TCSpawnEggItem eggItem) {
                TCTakumiBookScreen screen = new TCTakumiBookScreen();
                AbstractTCCreeper.TCCreeperContext context = eggItem.getContext();
                int index = 0;
                for (AbstractTCCreeper creeper : screen.creepers) {
                    if (Objects.equals(creeper.getContext().getRegistryName(), eggItem.getContext().getRegistryName())) {
                        index = screen.creepers.indexOf(creeper);
                    }
                }
                this.minecraft.setScreen(screen);
                screen.setPage(index);
            }
        }
    }

    private boolean isCreativeSlot(@Nullable Slot p_98554_) {
        return p_98554_ != null && p_98554_.container == CONTAINER;
    }

    @Override
    protected void init() {
        super.init();
        this.pages.clear();
        int tabIndex = 0;
        List<CreativeModeTab> currentPage = new java.util.ArrayList<>();
        currentPage.add(CreativeModeTabs.searchTab());
        this.pages.add(new net.minecraftforge.client.gui.CreativeTabsScreenPage(currentPage));
        this.currentPage = this.pages.getFirst();

        this.currentPage = this.pages.stream().filter(page -> page.getVisibleTabs().contains(selectedTab)).findFirst().orElse(this.currentPage);
        if (!this.currentPage.getVisibleTabs().contains(selectedTab)) {
            selectedTab = this.currentPage.getVisibleTabs().getFirst();
        }
        this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9,
                Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(false);
        this.searchBox.setTextColor(16777215);
        this.addWidget(this.searchBox);
        CreativeModeTab creativemodetab = selectedTab;
        selectedTab = CreativeModeTabs.searchTab();
        this.selectTab(creativemodetab);
        this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        this.listener = new CreativeInventoryListener(this.minecraft);
        this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
        if (!selectedTab.shouldDisplay()) {
            this.selectTab(CreativeModeTabs.getDefaultTab());
        }
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK,
                        p_98299_ -> this.minecraft.setScreen(new TCTakumiBookScreen(this.lastPage)))
                .bounds(this.width / 2 - 85, this.topPos + 110, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                p_98299_ -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 100, 206, 200, 20).build());
    }

    @Override
    public void resize(Minecraft p_98595_, int p_98596_, int p_98597_) {
        int i = this.menu.getRowIndexForScroll(this.scrollOffs);
        String s = this.searchBox.getValue();
        this.init(p_98595_, p_98596_, p_98597_);
        this.searchBox.setValue(s);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }

        this.scrollOffs = this.menu.getScrollForRowIndex(i);
        this.menu.scrollTo(this.scrollOffs);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        }

    }

    @Override
    public boolean charTyped(char p_98521_, int p_98522_) {
        if (this.ignoreTextInput) {
            return false;
        } else if (!selectedTab.hasSearchBar()) {
            return false;
        } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(p_98521_, p_98522_)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
        this.ignoreTextInput = false;
        if (!selectedTab.hasSearchBar()) {
            if (this.minecraft.options.keyChat.matches(p_98547_, p_98548_)) {
                this.ignoreTextInput = true;
                this.selectTab(CreativeModeTabs.searchTab());
                return true;
            } else {
                return super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
        } else {
            boolean flag = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
            boolean flag1 = InputConstants.getKey(p_98547_, p_98548_).getNumericKeyValue().isPresent();
            if (flag && flag1 && this.checkHotbarKeyPressed(p_98547_, p_98548_)) {
                this.ignoreTextInput = true;
                return true;
            } else {
                String s = this.searchBox.getValue();
                if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
                    if (!Objects.equals(s, this.searchBox.getValue())) {
                        this.refreshSearchResults();
                    }

                    return true;
                } else {
                    return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 || super.keyPressed(p_98547_, p_98548_, p_98549_);
                }
            }
        }
    }

    @Override
    public boolean keyReleased(int p_98612_, int p_98613_, int p_98614_) {
        this.ignoreTextInput = false;
        return super.keyReleased(p_98612_, p_98613_, p_98614_);
    }

    private void refreshSearchResults() {
        if (!selectedTab.hasSearchBar()) return;
        this.menu.items.clear();
        this.visibleTags.clear();
        String s = this.searchBox.getValue();
        if (s.isEmpty()) {
            TCEntityCore.ENTITY_CONTEXTS.forEach(context -> this.menu.items.add(new ItemStack(ForgeSpawnEggItem.fromEntityType(context.entityType()))));
        } else {
            if (TCSearchTreeCore.REGISTRY == null) {
                TCSearchTreeCore.register();
            }
            SearchTree<ItemStack> searchtree = this.minecraft.getConnection().searchTrees().getSearchTree(TCSearchTreeCore.CREEPER_NAMES);
            List<ItemStack> stacks = searchtree.search(s.toLowerCase(Locale.ROOT));
            this.menu.items.addAll(stacks);
        }

        this.scrollOffs = 0.0F;
        this.menu.scrollTo(0.0F);
    }

    private void updateVisibleTags(String p_98620_) {
        int i = p_98620_.indexOf(58);
        Predicate<ResourceLocation> predicate;
        if (i == -1) {
            predicate = p_98609_ -> p_98609_.getPath().contains(p_98620_);
        } else {
            String s = p_98620_.substring(0, i).trim();
            String s1 = p_98620_.substring(i + 1).trim();
            predicate = p_98606_ -> p_98606_.getNamespace().contains(s) && p_98606_.getPath().contains(s1);
        }

        BuiltInRegistries.ITEM.getTagNames().filter(p_205410_ -> predicate.test(p_205410_.location())).forEach(this.visibleTags::add);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int p_98617_, int p_98618_) {
        RenderSystem.disableBlend();
        Pair<Integer, Integer> slayall = TCClientUtils.checkSlayAllAdv();
        graphics.drawString(this.font, Component.translatable("takumicraft.takumibook.search"), 8, 6, 0, false);
    }

    @Override
    public boolean mouseClicked(double p_98531_, double p_98532_, int p_98533_) {
        if (p_98533_ == 0) {
            double d0 = p_98531_ - (double) this.leftPos;
            double d1 = p_98532_ - (double) this.topPos;

            for (CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
                if (this.checkTabClicked(creativemodetab, d0, d1)) {
                    return true;
                }
            }

            if (selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar(p_98531_, p_98532_)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }

        return super.mouseClicked(p_98531_, p_98532_, p_98533_);
    }

    @Override
    public boolean mouseReleased(double p_98622_, double p_98623_, int p_98624_) {
        if (p_98624_ == 0) {
            double d0 = p_98622_ - (double) this.leftPos;
            double d1 = p_98623_ - (double) this.topPos;
            this.scrolling = false;

            for (CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
                if (this.checkTabClicked(creativemodetab, d0, d1)) {
                    this.selectTab(creativemodetab);
                    return true;
                }
            }
        }

        return super.mouseReleased(p_98622_, p_98623_, p_98624_);
    }

    private boolean canScroll() {
        return selectedTab.canScroll() && this.menu.canScroll();
    }

    private void selectTab(CreativeModeTab p_98561_) {
        p_98561_ = CreativeModeTabs.searchTab();
        CreativeModeTab creativemodetab = selectedTab;
        selectedTab = p_98561_;
        slotColor = p_98561_.getSlotColor();
        this.quickCraftSlots.clear();
        this.menu.items.clear();
        this.clearDraggingState();
        if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
            HotbarManager hotbarmanager = this.minecraft.getHotbarManager();

            for (int i = 0; i < 9; ++i) {
                Hotbar hotbar = hotbarmanager.get(i);
                if (hotbar.isEmpty()) {
                    for (int j = 0; j < 9; ++j) {
                        if (j == i) {
                            ItemStack itemstack = new ItemStack(Items.PAPER);
                            itemstack.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);
                            Component component = this.minecraft.options.keyHotbarSlots[i].getTranslatedKeyMessage();
                            Component component1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            itemstack.set(DataComponents.ITEM_NAME, Component.translatable("inventory.hotbarInfo", component1, component));
                            this.menu.items.add(itemstack);
                        } else {
                            this.menu.items.add(ItemStack.EMPTY);
                        }
                    }
                } else {
                    this.menu.items.addAll(hotbar.load(this.minecraft.level.registryAccess()));
                }
            }
        } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
            this.menu.items.addAll(selectedTab.getDisplayItems());
        }

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            AbstractContainerMenu abstractcontainermenu = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf(this.menu.slots);
            }

            this.menu.slots.clear();

            for (int k = 0; k < abstractcontainermenu.slots.size(); ++k) {
                int l;
                int i1;
                if (k >= 5 && k < 9) {
                    int k1 = k - 5;
                    int i2 = k1 / 2;
                    int k2 = k1 % 2;
                    l = 54 + i2 * 54;
                    i1 = 6 + k2 * 27;
                } else if (k >= 0 && k < 5) {
                    l = -2000;
                    i1 = -2000;
                } else if (k == 45) {
                    l = 35;
                    i1 = 20;
                } else {
                    int j1 = k - 9;
                    int l1 = j1 % 9;
                    int j2 = j1 / 9;
                    l = 9 + l1 * 18;
                    if (k >= 36) {
                        i1 = 112;
                    } else {
                        i1 = 54 + j2 * 18;
                    }
                }

                Slot slot = new TCTakumiBookOutlineScreen.SlotWrapper(abstractcontainermenu.slots.get(k), k, l, i1);
                this.menu.slots.add(slot);
            }

            this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
            this.menu.slots.add(this.destroyItemSlot);
        } else if (creativemodetab.getType() == CreativeModeTab.Type.INVENTORY) {
            this.menu.slots.clear();
            this.menu.slots.addAll(this.originalSlots);
            this.originalSlots = null;
        }

        if (selectedTab.hasSearchBar()) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocused(true);
            if (creativemodetab != p_98561_) {
                this.searchBox.setValue("");
            }
            this.searchBox.setWidth(selectedTab.getSearchBarWidth());
            /*default width*/
            this.searchBox.setX(this.leftPos + 82 /*default left*/ + 89 - this.searchBox.getWidth());

            this.refreshSearchResults();
        } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocused(false);
            this.searchBox.setValue("");
        }

        this.scrollOffs = 0.0F;
        this.menu.scrollTo(0.0F);
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_299502_) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = this.menu.subtractInputFromScroll(this.scrollOffs, p_299502_);
            this.menu.scrollTo(this.scrollOffs);
            return true;
        }
    }

    @Override
    protected boolean hasClickedOutside(double p_98541_, double p_98542_, int p_98543_, int p_98544_, int p_98545_) {
        boolean flag = p_98541_ < (double) p_98543_ || p_98542_ < (double) p_98544_ || p_98541_ >= (double) (p_98543_ + this.imageWidth) || p_98542_ >= (double) (p_98544_ + this.imageHeight);
        this.hasClickedOutside = flag && !this.checkTabClicked(selectedTab, p_98541_, p_98542_);
        return this.hasClickedOutside;
    }

    protected boolean insideScrollbar(double p_98524_, double p_98525_) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        return p_98524_ >= (double) k && p_98525_ >= (double) l && p_98524_ < (double) i1 && p_98525_ < (double) j1;
    }

    @Override
    public boolean mouseDragged(double p_98535_, double p_98536_, int p_98537_, double p_98538_, double p_98539_) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            this.scrollOffs = ((float) p_98536_ - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs);
            return true;
        } else {
            return super.mouseDragged(p_98535_, p_98536_, p_98537_, p_98538_, p_98539_);
        }
    }

    @Override
    public void render(GuiGraphics p_98577_, int p_98578_, int p_98579_, float p_98580_) {
        this.renderBackground(p_98577_, p_98578_, p_98579_, p_98580_);
        super.render(p_98577_, p_98578_, p_98579_, p_98580_);

        for (CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
            if (this.checkTabHovering(p_98577_.pose(), creativemodetab, p_98578_, p_98579_)) {
                break;
            }
        }

        if (this.destroyItemSlot != null && selectedTab.getType() == CreativeModeTab.Type.INVENTORY && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, p_98578_, p_98579_)) {
            p_98577_.renderTooltip(this.font, TRASH_SLOT_TOOLTIP, p_98578_, p_98579_);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderTooltip(p_98577_, p_98578_, p_98579_);
    }

    @Override
    public void renderSlot(GuiGraphics graphics, Slot slot) {

        if (slot.getItem().getItem() instanceof TCSpawnEggItem item) {
            TCClientUtils.renderEntity(graphics.pose(), slot.x + (double) this.imageWidth / 25, slot.y + (double) this.imageHeight / 9, 7, this.tick / 100f, 0f, item.getContext().entityType(), true);
        } else {
            super.renderSlot(graphics, slot);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void renderTooltip(GuiGraphics p_98590_, int p_98592_, int p_98593_) {
        List<Component> components = new ArrayList<>();
        if (this.hoveredSlot != null) {
            ItemStack itemStack = this.hoveredSlot.getItem();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof TCSpawnEggItem egg) {
                AbstractTCCreeper.TCCreeperContext context = egg.getContext();
                boolean flg = TCClientUtils.checkSlayAdv(context.entityType());
                Component name = flg ? TCEntityUtils.getEntityName(context.entityType()) : TCEntityUtils.getUnknown();
                components.add(name);
                if (flg) {
                    components.add(MutableComponent.create(context.getRank().getRankName().getContents())
                            .withStyle(context.getRank().getLevel() > 2 ? ChatFormatting.DARK_PURPLE : ChatFormatting.GRAY));
                    components.add(MutableComponent.create(context.getElement().getElementName().getContents())
                            .withStyle(Style.EMPTY.withColor(context.getElement().getElementColor())));
                    if (context.getElement().getSubElementName() != CommonComponents.EMPTY) {
                        components.add(MutableComponent.create(context.getElement().getSubElementName().getContents()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
            p_98590_.renderTooltip(this.font, components, itemStack.getTooltipImage(), itemStack, p_98592_, p_98593_);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float p_98573_, int p_98574_, int p_98575_) {
        RenderSystem.setShaderTexture(0, SEARCH_GUI_TEXTURES);
        graphics.blit(SEARCH_GUI_TEXTURES, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render(graphics, p_98574_, p_98575_, p_98573_);
        int j = this.leftPos + 175;
        int k = this.topPos + 18;
        int i = k + 112;
        RenderSystem.setShaderTexture(0, SEARCH_GUI_TEXTURES);
        if (selectedTab.canScroll()) {
            ResourceLocation resourcelocation = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
            graphics.blitSprite(resourcelocation, j, k + (int) ((float) (i - k - 17) * this.scrollOffs), 12, 15);
        }
    }

    private int getTabX(CreativeModeTab p_260136_) {
        int i = currentPage.getColumn(p_260136_);
        int j = 27;
        int k = 27 * i;
        if (p_260136_.isAlignedRight()) {
            k = this.imageWidth - 27 * (7 - i) + 1;
        }

        return k;
    }

    private int getTabY(CreativeModeTab p_260181_) {
        int i = 0;
        if (currentPage.isTop(p_260181_)) {
            i -= 32;
        } else {
            i += this.imageHeight;
        }

        return i;
    }

    protected boolean checkTabClicked(CreativeModeTab p_98563_, double p_98564_, double p_98565_) {
        return false;
    }

    protected boolean checkTabHovering(PoseStack p_98585_, CreativeModeTab p_98586_, int p_98587_, int p_98588_) {
        return false;
    }

    protected void renderTabButton(PoseStack p_98582_, CreativeModeTab p_98583_) {
    }

    public boolean isInventoryOpen() {
        return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }

    public net.minecraftforge.client.gui.CreativeTabsScreenPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage) {
        this.currentPage = currentPage;
    }

    @OnlyIn(Dist.CLIENT)
    static class CustomCreativeSlot extends Slot {
        public CustomCreativeSlot(Container p_98633_, int p_98634_, int p_98635_, int p_98636_) {
            super(p_98633_, p_98634_, p_98635_, p_98636_);
        }

        @Override
        public boolean mayPickup(Player p_98638_) {
            ItemStack itemstack = this.getItem();
            return super.mayPickup(p_98638_) && !itemstack.isEmpty()
                    ? itemstack.isItemEnabled(p_98638_.level().enabledFeatures()) && !itemstack.has(DataComponents.CREATIVE_SLOT_LOCK)
                    : itemstack.isEmpty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TakumiPickerMenu extends AbstractContainerMenu {
        public final NonNullList<ItemStack> items = NonNullList.create();
        private final AbstractContainerMenu inventoryMenu;

        public TakumiPickerMenu(Player p_98641_) {
            super(null, 0);
            this.inventoryMenu = p_98641_.inventoryMenu;
            Inventory inventory = p_98641_.getInventory();

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new TCTakumiBookOutlineScreen.CustomCreativeSlot(TCTakumiBookOutlineScreen.CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }

            this.scrollTo(0.0F);
        }

        @Override
        public boolean stillValid(Player p_98645_) {
            return true;
        }

        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
        }

        protected int getRowIndexForScroll(float p_259664_) {
            return Math.max((int) ((double) (p_259664_ * (float) this.calculateRowCount()) + 0.5D), 0);
        }

        protected float getScrollForRowIndex(int p_259315_) {
            return Mth.clamp((float) p_259315_ / (float) this.calculateRowCount(), 0.0F, 1.0F);
        }

        protected float subtractInputFromScroll(float p_259841_, double p_260358_) {
            return Mth.clamp(p_259841_ - (float) (p_260358_ / (double) this.calculateRowCount()), 0.0F, 1.0F);
        }

        public void scrollTo(float p_98643_) {
            int i = this.getRowIndexForScroll(p_98643_);

            for (int j = 0; j < 5; ++j) {
                for (int k = 0; k < 9; ++k) {
                    int l = k + (j + i) * 9;
                    if (l >= 0 && l < this.items.size()) {
                        TCTakumiBookOutlineScreen.CONTAINER.setItem(k + j * 9, this.items.get(l));
                    } else {
                        TCTakumiBookOutlineScreen.CONTAINER.setItem(k + j * 9, ItemStack.EMPTY);
                    }
                }
            }

        }

        public boolean canScroll() {
            return this.items.size() > 45;
        }

        @Override
        public ItemStack quickMoveStack(Player p_98650_, int p_98651_) {
            if (p_98651_ >= this.slots.size() - 9 && p_98651_ < this.slots.size()) {
                Slot slot = this.slots.get(p_98651_);
                if (slot != null && slot.hasItem()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                }
            }

            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack p_98647_, Slot p_98648_) {
            return p_98648_.container != TCTakumiBookOutlineScreen.CONTAINER;
        }

        @Override
        public boolean canDragTo(Slot p_98653_) {
            return p_98653_.container != TCTakumiBookOutlineScreen.CONTAINER;
        }

        @Override
        public ItemStack getCarried() {
            return this.inventoryMenu.getCarried();
        }

        @Override
        public void setCarried(ItemStack p_169751_) {
            this.inventoryMenu.setCarried(p_169751_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class SlotWrapper extends Slot {
        final Slot target;

        public SlotWrapper(Slot p_98657_, int p_98658_, int p_98659_, int p_98660_) {
            super(p_98657_.container, p_98658_, p_98659_, p_98660_);
            this.target = p_98657_;
        }

        @Override
        public void onTake(Player p_169754_, ItemStack p_169755_) {
            this.target.onTake(p_169754_, p_169755_);
        }

        @Override
        public boolean mayPlace(ItemStack p_98670_) {
            return this.target.mayPlace(p_98670_);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void setByPlayer(ItemStack p_271008_) {
            this.target.setByPlayer(p_271008_);
        }

        @Override
        public void set(ItemStack p_98679_) {
            this.target.set(p_98679_);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack p_98675_) {
            return this.target.getMaxStackSize(p_98675_);
        }

        @Override
        @Nullable
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        @Override
        public ItemStack remove(int p_98663_) {
            return this.target.remove(p_98663_);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        @Override
        public boolean mayPickup(Player p_98665_) {
            return this.target.mayPickup(p_98665_);
        }

        @Override
        public int getSlotIndex() {
            return this.target.getSlotIndex();
        }

        @Override
        public boolean isSameInventory(Slot other) {
            return this.target.isSameInventory(other);
        }

        @Override
        public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
            this.target.setBackground(atlas, sprite);
            return this;
        }
    }
}