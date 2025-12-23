package com.mamiyaotaru.voxelmap.gui;

import com.mamiyaotaru.voxelmap.MapSettingsManager;
import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
// TODO: 1.20.1 Port - Input event classes don't exist, using primitive parameters instead
// import net.minecraft.client.input.KeyEvent;
// import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class GuiButtonRowListKeys extends AbstractSelectionList<GuiButtonRowListKeys.RowItem> {
    private final MapSettingsManager options;
    private final GuiMinimapControls parentGui;
    private final ArrayList<RowItem> rowItems = new ArrayList<>();
    private KeyMapping keyForEdit;
    private final HashMap<KeyMapping, Component> duplicateKeys = new HashMap<>();

    public GuiButtonRowListKeys(GuiMinimapControls parentScreen) {
        super(VoxelConstants.getMinecraft(), parentScreen.getWidth(), parentScreen.getHeight(), 40, parentScreen.getHeight() - 114, 20);
        this.parentGui = parentScreen;
        this.options = VoxelConstants.getVoxelMapInstance().getMapOptions();
        for (int i = 0; i < this.options.keyBindings.length; ++i) {
            int ii = i;
            this.rowItems.add(new RowItem(this.parentGui,
                    new Button.Builder(Component.empty(), button -> this.keyForEdit = this.options.keyBindings[ii]).bounds(0, 0, 75, 20).build(),
                    new Button.Builder(Component.translatable("controls.reset"), button -> this.resetKeyMapping(ii)).bounds(0, 0, 50, 20).build(),
                    this.options.keyBindings[i]));
        }
        this.rowItems.sort(Comparator.comparing(entry -> entry.keyMapping));
        this.checkDuplicateKeys();
        this.rowItems.forEach(this::addEntry);
    }

    public boolean keyEditing() {
        return this.keyForEdit != null;
    }

    private void resetKeyMapping(int index) {
        KeyMapping key = this.options.keyBindings[index];
        this.options.setKeyBinding(key, key.getDefaultKey());
        this.checkDuplicateKeys();
        KeyMapping.resetMapping();
    }

    // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.keyEditing()) {
            this.options.setKeyBinding(this.keyForEdit, InputConstants.Type.MOUSE.getOrCreate(button));
            this.keyForEdit = null;
            this.checkDuplicateKeys();
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    // 1.20.1: Input event system changed - keyPressed uses primitive parameters
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.keyEditing()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                boolean isMenuKey = this.keyForEdit.same(this.options.keyBindMenu);
                if (!isMenuKey) {
                    this.options.setKeyBinding(this.keyForEdit, InputConstants.UNKNOWN);
                }
            } else {
                // 1.20.1: InputConstants.getKey takes keyCode and scanCode
                this.options.setKeyBinding(this.keyForEdit, InputConstants.getKey(keyCode, scanCode));
            }
            this.keyForEdit = null;
            this.checkDuplicateKeys();
            KeyMapping.resetMapping();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void checkDuplicateKeys() {
        this.duplicateKeys.clear();
        for (KeyMapping key : this.options.keyBindings) {
            if (!key.isUnbound()) {
                KeyMapping[] duplicates = Arrays.stream(minecraft.options.keyMappings).filter(compare -> key != compare && key.same(compare)).toArray(KeyMapping[]::new);
                if (duplicates.length > 0) {
                    boolean bl = false;
                    MutableComponent details = Component.empty();
                    for (KeyMapping duplicate : duplicates) {
                        if (bl) {
                            details.append(", ");
                        }
                        bl = true;
                        details.append(Component.translatable(duplicate.getName()));
                    }

                    this.duplicateKeys.put(key, Component.translatable("controls.keybinds.duplicateKeybinds", details));
                }
            }
        }
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        // Empty implementation for 1.20.1
    }

    public class RowItem extends AbstractSelectionList.Entry<RowItem> {
        private final GuiMinimapControls parentGui;
        private final Button button;
        private final Button buttonReset;
        private final KeyMapping keyMapping;

        protected RowItem(GuiMinimapControls parentScreen, Button button, Button buttonReset, KeyMapping keyMapping) {
            this.parentGui = parentScreen;
            this.button = button;
            this.buttonReset = buttonReset;
            this.keyMapping = keyMapping;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (this.button != null && this.buttonReset != null) {
                guiGraphics.drawString(this.parentGui.getFont(), Component.translatable(this.keyMapping.getName()), left + 5, top + 5, 0xFFFFFFFF);

                Component tooltip = null;

                MutableComponent keyText = this.keyMapping.getTranslatedKeyMessage().copy();
                if (GuiButtonRowListKeys.this.keyForEdit != null && GuiButtonRowListKeys.this.keyForEdit == this.keyMapping) {
                    keyText = Component.empty()
                            .append(Component.literal("> ").withStyle(ChatFormatting.YELLOW))
                            .append(keyText.copy().withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(" <").withStyle(ChatFormatting.YELLOW));

                } else if (GuiButtonRowListKeys.this.duplicateKeys.containsKey(this.keyMapping)) {
                    keyText = Component.empty()
                            .append(Component.literal("[ ").withStyle(ChatFormatting.YELLOW))
                            .append(keyText.copy().withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(" ]").withStyle(ChatFormatting.YELLOW));

                    tooltip = GuiButtonRowListKeys.this.duplicateKeys.get(this.keyMapping);
                }

                if (tooltip != null) {
                    this.button.setTooltip(Tooltip.create(tooltip));
                }

                this.button.setMessage(keyText);
                this.button.setX(left + width - 135);
                this.button.setY(top);
                this.button.render(guiGraphics, mouseX, mouseY, tickDelta);

                this.buttonReset.active = !this.keyMapping.isDefault();
                this.buttonReset.setX(left + width - 55);
                this.buttonReset.setY(top);
                this.buttonReset.render(guiGraphics, mouseX, mouseY, tickDelta);
            }
        }

        // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            GuiButtonRowListKeys.this.setSelected(this);
            boolean clicked = false;
            if (this.button != null && this.button.mouseClicked(mouseX, mouseY, button)) {
                clicked = true;
            } else if (this.buttonReset != null && this.buttonReset.mouseClicked(mouseX, mouseY, button)) {
                clicked = true;
            }
            return clicked;
        }
    }

}