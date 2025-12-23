package com.mamiyaotaru.voxelmap.gui;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.DimensionManager;
// TODO: 1.20.1 Port - CursorTypes package changed or doesn't exist
// import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
// TODO: 1.20.1 Port - MouseButtonEvent doesn't exist, using primitive parameters instead
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

class GuiSlotDimensions extends AbstractSelectionList<GuiSlotDimensions.DimensionItem> {
    private static final Component APPLIES = Component.translatable("minimap.waypoints.dimension.applies");
    private static final Component NOT_APPLIES = Component.translatable("minimap.waypoints.dimension.notApplies");
    private static final ResourceLocation CONFIRM = new ResourceLocation("textures/gui/sprites/container/beacon/confirm.png");
    private static final ResourceLocation CANCEL = new ResourceLocation("textures/gui/sprites/container/beacon/cancel.png");

    private final GuiAddWaypoint parentGui;
    private final ArrayList<DimensionItem> dimensions;

    protected long lastClicked;
    public boolean doubleClicked;

    GuiSlotDimensions(GuiAddWaypoint par1GuiWaypoints) {
        super(VoxelConstants.getMinecraft(), 101, 64, par1GuiWaypoints.getHeight() / 6 + 90, 64, 18);
        this.parentGui = par1GuiWaypoints;
        // TODO: 1.20.1 Port - setX() doesn't exist in AbstractSelectionList
        // this.setX(this.parentGui.getWidth() / 2);
        DimensionManager dimensionManager = VoxelConstants.getVoxelMapInstance().getDimensionManager();
        this.dimensions = new ArrayList<>();
        DimensionItem first = null;

        for (DimensionContainer dim : dimensionManager.getDimensions()) {
            DimensionItem item = new DimensionItem(this.parentGui, dim);
            this.dimensions.add(item);
            if (dim.equals(this.parentGui.waypoint.dimensions.first())) {
                first = item;
            }
        }

        this.dimensions.forEach(this::addEntry);
        if (first != null) {
            // TODO: 1.20.1 Port - scrollToEntry() doesn't exist in AbstractSelectionList
            // this.scrollToEntry(first);
        }

    }

    @Override
    public int getRowWidth() {
        return 100;
    }

    @Override
    public void setSelected(DimensionItem entry) {
        super.setSelected(entry);
        if (this.getSelected() instanceof DimensionItem) {
            GameNarrator narratorManager = new GameNarrator(VoxelConstants.getMinecraft());
            narratorManager.sayNow(Component.translatable("narrator.select", (this.getSelected()).dim.name));
        }

        this.parentGui.setSelectedDimension(entry.dim);
    }

    // FIXME 1.21.9
    // @Override
    // protected boolean isSelectedItem(int index) {
    // return this.dimensions.get(index).dim.equals(this.parentGui.selectedDimension);
    // }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


    // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.doubleClicked = System.currentTimeMillis() - this.lastClicked < 250L;
        this.lastClicked = System.currentTimeMillis();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public class DimensionItem extends AbstractSelectionList.Entry<DimensionItem> {
        private final GuiAddWaypoint parentGui;
        private final DimensionContainer dim;

        protected DimensionItem(GuiAddWaypoint waypointScreen, DimensionContainer dim) {
            this.parentGui = waypointScreen;
            this.dim = dim;
        }

        @Override
        public void render(GuiGraphics drawContext, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            drawContext.drawCenteredString(((net.minecraft.client.gui.screens.Screen)this.parentGui).font, this.dim.getDisplayName(), this.parentGui.getWidth() / 2 + GuiSlotDimensions.this.width / 2, top + 3, 0xFFFFFFFF);
            byte padding = 4;
            byte iconWidth = 18;
            int x = this.parentGui.getWidth() / 2;
            int listWidth = GuiSlotDimensions.this.width;
            if (mouseX >= x + padding && mouseY >= top && mouseX <= x + listWidth + padding && mouseY <= top + GuiSlotDimensions.this.itemHeight) {
                Component tooltip;
                if (!this.parentGui.popupOpen() && mouseX >= x + listWidth - iconWidth - padding && mouseX <= x + listWidth) {
                    // TODO: 1.20.1 Port - CursorTypes.POINTING_HAND doesn't exist or has different API
                    // drawContext.requestCursor(CursorTypes.POINTING_HAND);
                    tooltip = this.parentGui.waypoint.dimensions.contains(this.dim) ? APPLIES : NOT_APPLIES;
                } else {
                    tooltip = null;
                }

                GuiAddWaypoint.setTooltip(this.parentGui, tooltip);
            }

            // show check mark / cross
            // 2 int: x,y screen
            // 2 float: u,v start texture (in pixels - see last 2 int)
            // 2 int: height, width on screen
            // 2 int: height, width full texture in pixels

            drawContext.blit(this.parentGui.waypoint.dimensions.contains(this.dim) ? CONFIRM : CANCEL, x + listWidth - iconWidth, top - 3, 0, 0, 18, 18, 18, 18);
        }

        // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseY < GuiSlotDimensions.this.getY() || mouseY > GuiSlotDimensions.this.getBottom()) {
                return false;
            }

            GuiSlotDimensions.this.setSelected(this);
            byte iconWidth = 18;
            int rightEdge = GuiSlotDimensions.this.getX() + GuiSlotDimensions.this.getWidth();
            boolean inRange = mouseX >= (rightEdge - iconWidth) && mouseX <= rightEdge;
            if (inRange) {
                this.parentGui.toggleDimensionSelected();
            }

            return true;
        }
    }
}
