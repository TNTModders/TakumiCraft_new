package com.tntmodders.takumicraft.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.client.TCSearchTreeCore;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TCTakumiBookOutlineScreen extends EffectRenderingInventoryScreen<TCTakumiBookOutlineScreen.TakumiPickerMenu> {
    static final SimpleContainer CONTAINER = new SimpleContainer(45);
    private static final ResourceLocation SEARCH_GUI_TEXTURES = new ResourceLocation(TakumiCraftCore.MODID, "textures/book/book_search.png");
    private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 9;
    private static final int TAB_WIDTH = 28;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
    private static final int TEXT_COLOR = 16777215;
    private static final int tabPage = 0;
    private static CreativeModeTab selectedTab = CreativeModeTabs.SEARCH;
    private final Set<TagKey<Item>> visibleTags = new HashSet<>();
    private final int maxPages = 0;
    private final int searchBoxX = 10;
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
    private int tick;

    public TCTakumiBookOutlineScreen(Player player) {
        super(new TakumiPickerMenu(player), player.getInventory(), CommonComponents.EMPTY);
        player.containerMenu = this.menu;
        this.passEvents = true;
        this.imageHeight = 136;
        this.imageWidth = 198;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private boolean isAllowedTab(CreativeModeTab tab) {
        return tab == CreativeModeTabs.SEARCH;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        } else if (this.searchBox != null) {
            this.searchBox.tick();
        }
        this.tick++;
    }

    @Override
    protected void slotClicked(@Nullable Slot p_98556_, int p_98557_, int p_98558_, ClickType p_98559_) {
        if (this.isCreativeSlot(p_98556_)) {
            this.searchBox.moveCursorToEnd();
            this.searchBox.setHighlightPos(0);
        }
    }

    private boolean isCreativeSlot(@Nullable Slot p_98554_) {
        return p_98554_ != null && p_98554_.container == CONTAINER;
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new EditBox(this.font, this.leftPos + this.searchBoxX, this.topPos + 114, 80, 9, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(false);
        this.searchBox.setTextColor(16777215);
        this.addWidget(this.searchBox);
        this.selectTab(CreativeModeTabs.SEARCH);
        this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        this.listener = new CreativeInventoryListener(this.minecraft);
        this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
        this.createMenuControls();
    }

    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                (p_98299_) -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 100, this.height / 2 + 70, 200,
                20).build());
    }

    @Override
    public void resize(Minecraft p_98595_, int p_98596_, int p_98597_) {
        String s = this.searchBox.getValue();
        this.init(p_98595_, p_98596_, p_98597_);
        this.searchBox.setValue(s);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
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
                this.selectTab(CreativeModeTabs.SEARCH);
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
        (this.menu).items.clear();
        this.visibleTags.clear();
        CreativeModeTab tab = selectedTab;
        if (tab.hasSearchBar() && tab != CreativeModeTabs.SEARCH) {
            menu.items.addAll(selectedTab.getDisplayItems());
            if (!this.searchBox.getValue().isEmpty()) {
                String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
                java.util.Iterator<ItemStack> itr = menu.items.iterator();
                while (itr.hasNext()) {
                    ItemStack stack = itr.next();
                    boolean matches = false;
                    for (Component line : stack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)) {
                        if (ChatFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) itr.remove();
                }
            }
            this.scrollOffs = 0.0F;
            menu.scrollTo(0.0F);
            return;
        }

        String s = this.searchBox.getValue();
        if (s.isEmpty()) {
            for (Item item : ForgeRegistries.ITEMS) {
                if (TCCreativeModeTabCore.TAB_EGGS.contains(item.getDefaultInstance())) {
                    this.menu.items.add(item.getDefaultInstance());
                }
            }
        } else {
            SearchTree<ItemStack> searchtree;
            searchtree = this.minecraft.getSearchTree(TCSearchTreeCore.CREEPER_NAMES);
            List<ItemStack> stacks = searchtree.search(s.toLowerCase(Locale.ROOT));
            stacks.removeIf(itemStack -> !TCCreativeModeTabCore.TAB_EGGS.contains(itemStack));
            (this.menu).items.addAll(stacks);
        }

        this.scrollOffs = 0.0F;
        this.menu.scrollTo(0.0F);
    }

    private void updateVisibleTags(String p_98620_) {
        int i = p_98620_.indexOf(58);
        Predicate<ResourceLocation> predicate;
        if (i == -1) {
            predicate = (p_98609_) -> p_98609_.getPath().contains(p_98620_);
        } else {
            String s = p_98620_.substring(0, i).trim();
            String s1 = p_98620_.substring(i + 1).trim();
            predicate = (p_98606_) -> p_98606_.getNamespace().contains(s) && p_98606_.getPath().contains(s1);
        }

        ForgeRegistries.ITEMS.tags().getTagNames().filter((p_205410_) -> predicate.test(p_205410_.location())).forEach(this.visibleTags::add);
    }

    @Override
    protected void renderLabels(PoseStack p_98616_, int p_98617_, int p_98618_) {
        CreativeModeTab creativemodetab = selectedTab;
        if (creativemodetab != null && isAllowedTab(creativemodetab) && creativemodetab.showTitle()) {
            RenderSystem.disableBlend();
            this.font.draw(p_98616_, Component.translatable("takumicraft.takumibook.search"), 8.0F, 6.0F, creativemodetab.getLabelColor());
            Pair<Integer, Integer> pair = TCEntityUtils.checkSlayAllAdv();
            this.font.draw(p_98616_, Component.translatable("takumicraft.takumibook.comp").append(pair.getFirst() + "/" + pair.getSecond()),
                    115.0f, 6.0f, creativemodetab.getLabelColor());
        }
    }

    @Override
    public boolean mouseClicked(double p_98531_, double p_98532_, int p_98533_) {
        if (p_98533_ == 0) {
            if (selectedTab != CreativeModeTabs.INVENTORY && this.insideScrollbar(p_98531_, p_98532_)) {
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

            for (CreativeModeTab creativemodetab : CreativeModeTabs.allTabs()) {
                if (creativemodetab != null /*&& this.checkTabClicked(creativemodetab, d0, d1)*/) {
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
        CreativeModeTab creativemodetab = selectedTab;
        selectedTab = p_98561_;
        slotColor = p_98561_.getSlotColor();
        this.quickCraftSlots.clear();
        (this.menu).items.clear();
        this.clearDraggingState();
        if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
            HotbarManager hotbarmanager = this.minecraft.getHotbarManager();

            for (int i = 0; i < 9; ++i) {
                Hotbar hotbar = hotbarmanager.get(i);
                if (hotbar.isEmpty()) {
                    for (int j = 0; j < 9; ++j) {
                        if (j == i) {
                            ItemStack itemstack = new ItemStack(Items.PAPER);
                            itemstack.getOrCreateTagElement("CustomCreativeLock");
                            Component component = this.minecraft.options.keyHotbarSlots[i].getTranslatedKeyMessage();
                            Component component1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            itemstack.setHoverName(Component.translatable("inventory.hotbarInfo", component1, component));
                            (this.menu).items.add(itemstack);
                        } else {
                            (this.menu).items.add(ItemStack.EMPTY);
                        }
                    }
                } else {
                    (this.menu).items.addAll(hotbar);
                }
            }
        } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
            (this.menu).items.addAll(selectedTab.getDisplayItems());
        }

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            AbstractContainerMenu abstractcontainermenu = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf((this.menu).slots);
            }

            (this.menu).slots.clear();

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

                Slot slot = new SlotWrapper(abstractcontainermenu.slots.get(k), k, l, i1);
                (this.menu).slots.add(slot);
            }

            this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
            (this.menu).slots.add(this.destroyItemSlot);
        } else if (creativemodetab.getType() == CreativeModeTab.Type.INVENTORY) {
            (this.menu).slots.clear();
            (this.menu).slots.addAll(this.originalSlots);
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
            this.searchBox.setX(this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth());

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
    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
        if (!this.canScroll()) {
            return false;
        } else {
            int i = ((this.menu).items.size() + 9 - 1) / 9 - 5;
            this.scrollOffs = (float) ((double) this.scrollOffs - p_98529_ / (double) i);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs);
            return true;
        }
    }

    @Override
    protected boolean hasClickedOutside(double p_98541_, double p_98542_, int p_98543_, int p_98544_, int p_98545_) {
        boolean flag = p_98541_ < (double) p_98543_ || p_98542_ < (double) p_98544_ || p_98541_ >= (double) (p_98543_ + this.imageWidth) || p_98542_ >= (double) (p_98544_ + this.imageHeight);
        this.hasClickedOutside = flag /*&& !this.checkTabClicked(selectedTab, p_98541_, p_98542_)*/;
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
    public void render(PoseStack p_98577_, int p_98578_, int p_98579_, float p_98580_) {
        this.renderBackground(p_98577_);
        super.render(p_98577_, p_98578_, p_98579_, p_98580_);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderTooltip(p_98577_, p_98578_, p_98579_);
    }

    @Override
    public void renderSlot(PoseStack poseStack, Slot slot) {
        if (slot.getItem().getItem() instanceof TCSpawnEggItem item) {
            TCEntityUtils.renderEntity(slot.x + 7.5, slot.y + 15, 7, this.tick / 100f, 0f, item.getContext().entityType());
        } else {
            super.renderSlot(poseStack, slot);
        }
    }

    @Override
    protected void renderTooltip(PoseStack p_98590_, ItemStack stack, int p_98592_, int p_98593_) {
        if (stack.getItem() instanceof TCSpawnEggItem eggItem) {
            List<Component> components = new ArrayList<>();
            if (TCEntityUtils.checkSlayAdv(eggItem.getContext().entityType())) {
                components.add(TCEntityUtils.getEntityName(eggItem.getType(stack.getOrCreateTag())).copy().withStyle(Style.EMPTY.withBold(true)));
                components.add(eggItem.getContext().getRank().getRankName().copy().withStyle(Style.EMPTY.withColor(0x888888).withItalic(true)));
                components.add(eggItem.getContext().getElement().getElementName().copy().setStyle(Style.EMPTY.withColor(eggItem.getContext().getElement().getElementColor())));
                if (eggItem.getContext().getElement().getSubElementName() != CommonComponents.EMPTY) {
                    components.add(eggItem.getContext().getElement().getSubElementName());
                }
            } else {
                components = List.of(Component.translatable("???"));
            }
            super.renderTooltip(p_98590_, components, stack.getTooltipImage(), p_98592_, p_98593_);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float p_98573_, int p_98574_, int p_98575_) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        CreativeModeTab creativemodetab = selectedTab;

        int start = tabPage * 10;
        int end = Math.min(CreativeModeTabs.allTabs().size(), 12);
        if (tabPage != 0) start += 2;

        for (int idx = start; idx < end; idx++) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            CreativeModeTab creativemodetab1 = CreativeModeTabs.allTabs().get(idx);
            if (creativemodetab1 != null && creativemodetab1 != selectedTab) {
                RenderSystem.setShaderTexture(0, SEARCH_GUI_TEXTURES);
                this.renderTabButton(poseStack, creativemodetab1);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.setShaderTexture(0, SEARCH_GUI_TEXTURES);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render(poseStack, p_98574_, p_98575_, p_98573_);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos + 175;
        int j = this.topPos + 18;
        int k = j + 112;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SEARCH_GUI_TEXTURES);
        if (creativemodetab.canScroll()) {
            blit(poseStack, i, j + (int) ((float) (k - j - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        }

        this.renderTabButton(poseStack, creativemodetab);
    }

    protected boolean checkTabHovering(PoseStack p_98585_, CreativeModeTab p_98586_, int p_98587_, int p_98588_) {
        return false;
    }

    protected void renderTabButton(PoseStack p_98582_, CreativeModeTab tab) {
    }


    @OnlyIn(Dist.CLIENT)
    static class CustomCreativeSlot extends Slot {
        public CustomCreativeSlot(Container p_98633_, int p_98634_, int p_98635_, int p_98636_) {
            super(p_98633_, p_98634_, p_98635_, p_98636_);
        }

        @Override
        public boolean mayPickup(Player p_98638_) {
            if (super.mayPickup(p_98638_) && this.hasItem()) {
                return this.getItem().getTagElement("CustomCreativeLock") == null;
            } else {
                return !this.hasItem();
            }
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
                    this.addSlot(new CustomCreativeSlot(CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }

            this.scrollTo(0.0F);
        }

        @Override
        public boolean stillValid(Player p_98645_) {
            return true;
        }

        public void scrollTo(float p_98643_) {
            int i = (this.items.size() + 9 - 1) / 9 - 5;
            int j = (int) ((double) (p_98643_ * (float) i) + 0.5D);
            if (j < 0) {
                j = 0;
            }

            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int i1 = l + (k + j) * 9;
                    if (i1 >= 0 && i1 < this.items.size()) {
                        CONTAINER.setItem(l + k * 9, this.items.get(i1));
                    } else {
                        CONTAINER.setItem(l + k * 9, ItemStack.EMPTY);
                    }
                }
            }

        }

        public boolean canScroll() {
            return this.items.size() > 45;
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
            if (slot.getItem().getItem() instanceof TCSpawnEggItem item) {
                TCTakumiBookScreen screen = new TCTakumiBookScreen();
                Minecraft.getInstance().setScreen(screen);
                screen.setPage(TCEntityCore.ENTITY_CONTEXTS.indexOf(item.getContext()));
            }
            return false;
        }

        @Override
        public boolean canDragTo(Slot p_98653_) {
            return false;
        }

        @Override
        public ItemStack getCarried() {
            return this.inventoryMenu.getCarried();
        }

        @Override
        public void setCarried(ItemStack p_169751_) {
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
