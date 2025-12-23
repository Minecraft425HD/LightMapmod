package com.mamiyaotaru.voxelmap.gui.overridden;

import com.mamiyaotaru.voxelmap.MapSettingsManager;
import com.mamiyaotaru.voxelmap.VoxelConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
// TODO: 1.20.1 Port - KeyEvent doesn't exist, using primitive parameters instead
// import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GuiScreenMinimap extends Screen {
    protected GuiScreenMinimap() { this (Component.literal("")); }

    protected GuiScreenMinimap(Component title) {
        super (title);
    }

    @Override
    public void removed() { MapSettingsManager.instance.saveAll(); }

    public void renderTooltip(GuiGraphics drawContext, Component text, int x, int y) {
        if (!(text != null && text.getString() != null && !text.getString().isEmpty())) {
            return;
        }

//        ClientTooltipComponent clientTooltipComponent = ClientTooltipComponent.create(text.getVisualOrderText());
//        drawContext.renderTooltip(VoxelConstants.getMinecraft().font, List.of(clientTooltipComponent), x, y, DefaultTooltipPositioner.INSTANCE, null);

        // 1.20.1: setTooltipForNextFrame() doesn't exist, use renderTooltip directly
        Tooltip tooltip = Tooltip.create(text);
        drawContext.renderTooltip(this.font, tooltip.toCharSequence(VoxelConstants.getMinecraft()), x, y);
    }

    // 1.20.1: Public accessor for protected font field
    public Font getFont() { return this.font; }

    @Override
    public List<? extends GuiEventListener> children() { return super.children(); }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    private Screen parentScreen;

    protected void setParentScreen(Object parent) {
        if (parent instanceof Screen) {
            parentScreen = (Screen) parent;
        }
    }

    // 1.20.1: Input event system changed - keyPressed takes primitive parameters instead of KeyEvent
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && parentScreen != null) {
            VoxelConstants.getMinecraft().setScreen(parentScreen);

            return false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}