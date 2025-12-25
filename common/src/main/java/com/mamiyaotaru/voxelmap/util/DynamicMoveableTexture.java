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

            // 1.20.1 Port: Use bulk memory copy for performance
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();
            long pointer = this.getPixels().pixels;

            if (pointer == 0L) return;

            int bytesPerPixel = 4; // RGBA
            int rowBytes = width * bytesPerPixel;

            if (offset > 0) {
                // Shift right: copy pixels from right to left to avoid overlap
                for (int y = 0; y < height; y++) {
                    long srcAddr = pointer + (y * width + offset) * bytesPerPixel;
                    long dstAddr = pointer + (y * width) * bytesPerPixel;
                    int copyBytes = (width - offset) * bytesPerPixel;
                    MemoryUtil.memCopy(srcAddr, dstAddr, copyBytes);
                }
            } else if (offset < 0) {
                // Shift left: copy pixels from left to right to avoid overlap
                int absOffset = -offset;
                for (int y = 0; y < height; y++) {
                    long srcAddr = pointer + (y * width) * bytesPerPixel;
                    long dstAddr = pointer + (y * width + absOffset) * bytesPerPixel;
                    int copyBytes = (width - absOffset) * bytesPerPixel;
                    MemoryUtil.memCopy(srcAddr, dstAddr, copyBytes);
                }
            }
        }
    }

    public void moveY(int offset) {
        synchronized (this.bufferLock) {
            if (offset == 0) return;

            // 1.20.1 Port: Use bulk memory copy for performance
            int width = this.getPixels().getWidth();
            int height = this.getPixels().getHeight();
            long pointer = this.getPixels().pixels;

            if (pointer == 0L) return;

            int bytesPerPixel = 4; // RGBA
            int rowBytes = width * bytesPerPixel;

            if (offset > 0) {
                // Shift down: copy rows from bottom to top to avoid overlap
                for (int y = 0; y < height - offset; y++) {
                    long srcAddr = pointer + (y + offset) * rowBytes;
                    long dstAddr = pointer + y * rowBytes;
                    MemoryUtil.memCopy(srcAddr, dstAddr, rowBytes);
                }
            } else if (offset < 0) {
                // Shift up: copy rows from top to bottom to avoid overlap
                int absOffset = -offset;
                for (int y = height - 1; y >= absOffset; y--) {
                    long srcAddr = pointer + (y - absOffset) * rowBytes;
                    long dstAddr = pointer + y * rowBytes;
                    MemoryUtil.memCopy(srcAddr, dstAddr, rowBytes);
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
