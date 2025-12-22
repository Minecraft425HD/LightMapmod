package com.mamiyaotaru.voxelmap.gui;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
// 1.20.1: Input event system changed
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class GuiMinimapControls extends GuiScreenMinimap {
    private final Screen parentScreen;
    protected String screenTitle = "Controls";
    private GuiButtonRowListKeys keymapList;

    public GuiMinimapControls(Screen parent) {
        this.parentScreen = parent;
        this.setParentScreen(this.parentScreen);
    }

    public void init() {
        this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), button -> VoxelConstants.getMinecraft().setScreen(this.parentScreen)).bounds(this.getWidth() / 2 - 100, this.getHeight() - 28, 200, 20).build());
        this.screenTitle = I18n.get("key.category.voxelmap.controls.title");

        this.keymapList = new GuiButtonRowListKeys(this);
        this.addRenderableWidget(this.keymapList);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 1.20.1: Input event system changed
        if (this.keymapList.keyEditing()) {
            return this.keymapList.keyPressed(keyCode, scanCode, modifiers);
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.getFont(), I18n.get("controls.minimap.unbind1"), this.getWidth() / 2, this.getHeight() - 64, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.getFont(), "§e" + I18n.get("controls.minimap.unbind2"), this.getWidth() / 2, this.getHeight() - 48, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.getFont(), this.screenTitle, this.getWidth() / 2, 20, 0xFFFFFFFF);
    }
}