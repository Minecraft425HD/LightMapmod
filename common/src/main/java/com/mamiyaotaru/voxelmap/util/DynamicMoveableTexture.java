package com.mamiyaotaru.voxelmap.util;

// GlTexture doesn't exist in 1.20.1
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.system.MemoryUtil;

public class DynamicMoveableTexture extends DynamicTexture {
    private final Object bufferLock = new Object();

    public DynamicMoveableTexture(int width, int height, boolean clear) {
        super(width, height, clear);
    }

    public int getWidth() {
        return this.getPixels().getWidth();
    }

    public int getHeight() {
        return this.getPixels().getHeight();
    }

    public int getIndex() {
        return this.getId();
    }

    public void moveX(int offset) {
        synchronized (this.bufferLock) {
            if (offset == 0) return;

            // 1.20.1 Port: Use row-based buffer for better performance than pixel-by-pixel
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();
            int[] rowBuffer = new int[width];

            if (offset > 0) {
                // Shift right: copy each row
                for (int y = 0; y < height; y++) {
                    // Read the row into buffer
                    for (int x = 0; x < width; x++) {
                        rowBuffer[x] = this.getPixels().getPixelRGBA(x, y);
                    }
                    // Write shifted row back
                    for (int x = 0; x < width - offset; x++) {
                        this.getPixels().setPixelRGBA(x, y, rowBuffer[x + offset]);
                    }
                }
            } else if (offset < 0) {
                // Shift left: copy each row
                int absOffset = -offset;
                for (int y = 0; y < height; y++) {
                    // Read the row into buffer
                    for (int x = 0; x < width; x++) {
                        rowBuffer[x] = this.getPixels().getPixelRGBA(x, y);
                    }
                    // Write shifted row back
                    for (int x = absOffset; x < width; x++) {
                        this.getPixels().setPixelRGBA(x, y, rowBuffer[x - absOffset]);
                    }
                }
            }
        }
    }

    public void moveY(int offset) {
        synchronized (this.bufferLock) {
            if (offset == 0) return;

            // 1.20.1 Port: Use row-based buffer for better performance than pixel-by-pixel
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();
            int[] rowBuffer = new int[width];

            if (offset > 0) {
                // Shift down: copy rows from top to bottom
                for (int y = 0; y < height - offset; y++) {
                    // Read source row into buffer
                    for (int x = 0; x < width; x++) {
                        rowBuffer[x] = this.getPixels().getPixelRGBA(x, y + offset);
                    }
                    // Write to destination row
                    for (int x = 0; x < width; x++) {
                        this.getPixels().setPixelRGBA(x, y, rowBuffer[x]);
                    }
                }
            } else if (offset < 0) {
                // Shift up: copy rows from bottom to top
                int absOffset = -offset;
                for (int y = height - 1; y >= absOffset; y--) {
                    // Read source row into buffer
                    for (int x = 0; x < width; x++) {
                        rowBuffer[x] = this.getPixels().getPixelRGBA(x, y - absOffset);
                    }
                    // Write to destination row
                    for (int x = 0; x < width; x++) {
                        this.getPixels().setPixelRGBA(x, y, rowBuffer[x]);
                    }
                }
            }
        }
    }

    public void setRGB(int x, int y, int color24) {
        int alpha = color24 >> 24 & 0xFF;
        byte a = -1;
        byte r = (byte) ((color24 & 0xFF) * alpha / 255);
        byte g = (byte) ((color24 >> 8 & 0xFF) * alpha / 255);
        byte b = (byte) ((color24 >> 16 & 0xFF) * alpha / 255);
        int color = (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
        this.getPixels().setPixelRGBA(x, y, color);
    }
}
