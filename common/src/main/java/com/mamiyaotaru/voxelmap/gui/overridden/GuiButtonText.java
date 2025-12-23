package com.mamiyaotaru.voxelmap.gui.overridden;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
// TODO: 1.20.1 Port - Input event classes don't exist, using primitive parameters instead
// import net.minecraft.client.input.CharacterEvent;
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

// TODO: 1.20.1 Port - Button.Plain doesn't exist, extending Button directly
public class GuiButtonText extends Button {
    private boolean editing;
    private final EditBox textField;

    public GuiButtonText(Font fontRenderer, int x, int y, int width, int height, Component message, OnPress onPress) {
        super (x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textField = new EditBox(fontRenderer, x + 1, y + 1, width - 2, height - 2, null);
    }


    @Override
    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        if (editing) {
            textField.render(drawContext, mouseX, mouseY, delta);
            return;
        }
        super.renderWidget(drawContext, mouseX, mouseY, delta);
    }

    // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean pressed = super.mouseClicked(mouseX, mouseY, button);
        this.setEditing(pressed);
        return pressed;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        if (editing) {
            this.setFocused(true);
        }

        textField.setFocused(editing);
    }

    // 1.20.1: Input event system changed - keyPressed uses primitive parameters
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!(editing)) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode != 257 && keyCode != 335 && keyCode != 258) {
            return textField.keyPressed(keyCode, scanCode, modifiers);
        }

        setEditing(false);
        return false;
    }


    // 1.20.1: Input event system changed - charTyped uses primitive parameters
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!(editing)) {
            return super.charTyped(codePoint, modifiers);
        }
        if (codePoint != '\r') {
            return textField.charTyped(codePoint, modifiers);
        }

        setEditing(false);
        return false;
    }

    public boolean isEditing() { return editing; }

    public void setText(String text) { textField.setValue(text); }

    public String getText() { return textField.getValue(); }
}