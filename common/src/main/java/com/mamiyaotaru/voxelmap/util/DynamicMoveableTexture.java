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

            // 1.20.1 Port: Use NativeImage pixel operations instead of direct memory access
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();

            if (offset > 0) {
                // Shift right: copy pixels from left to right
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width - offset; x++) {
                        int pixel = this.getPixels().getPixelRGBA(x + offset, y);
                        this.getPixels().setPixelRGBA(x, y, pixel);
                    }
                }
            } else if (offset < 0) {
                // Shift left: copy pixels from right to left
                int absOffset = -offset;
                for (int y = 0; y < height; y++) {
                    for (int x = width - 1; x >= absOffset; x--) {
                        int pixel = this.getPixels().getPixelRGBA(x - absOffset, y);
                        this.getPixels().setPixelRGBA(x, y, pixel);
                    }
                }
            }
        }
    }

    public void moveY(int offset) {
        synchronized (this.bufferLock) {
            if (offset == 0) return;

            // 1.20.1 Port: Use NativeImage pixel operations instead of direct memory access
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();

            if (offset > 0) {
                // Shift down: copy pixels from top to bottom
                for (int y = 0; y < height - offset; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = this.getPixels().getPixelRGBA(x, y + offset);
                        this.getPixels().setPixelRGBA(x, y, pixel);
                    }
                }
            } else if (offset < 0) {
                // Shift up: copy pixels from bottom to top
                int absOffset = -offset;
                for (int y = height - 1; y >= absOffset; y--) {
                    for (int x = 0; x < width; x++) {
                        int pixel = this.getPixels().getPixelRGBA(x, y - absOffset);
                        this.getPixels().setPixelRGBA(x, y, pixel);
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
