package com.lightmap.gui.overridden;

// TODO: 1.20.1 Port - MouseButtonEvent doesn't exist, using primitive parameters instead
// import net.minecraft.client.input.MouseButtonEvent;

public interface IPopupGuiScreen {
    boolean overPopup(int mouseX, int mouseY);

    boolean popupOpen();

    void popupAction(Popup popup, int action);

    // 1.20.1: Input event system changed - mouseClicked uses primitive parameters
    boolean mouseClicked(double mouseX, double mouseY, int button);
}