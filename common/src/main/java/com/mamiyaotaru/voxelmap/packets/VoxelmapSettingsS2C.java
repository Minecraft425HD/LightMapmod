package com.mamiyaotaru.voxelmap.packets;

import com.google.gson.Gson;
import com.mamiyaotaru.voxelmap.VoxelConstants;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record VoxelmapSettingsS2C(String settingsJson) {
    public static final ResourceLocation PACKET_ID = new ResourceLocation("voxelmap", "settings");

    public VoxelmapSettingsS2C(FriendlyByteBuf buf) {
        this(parse(buf));
    }

    private static String parse(FriendlyByteBuf buf) {
        buf.readByte(); // ignore
        return buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(0);
        buf.writeUtf(settingsJson);
    }


    public static void parsePacket(VoxelmapSettingsS2C packet) {
        @SuppressWarnings("unchecked")
        Map<String, Object> settings = new Gson().fromJson(packet.settingsJson(), Map.class);
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            String setting = entry.getKey();
            Object value = entry.getValue();
            switch (setting) {
                case "worldName" -> {
                    if (value instanceof String worldName) {
                        Minecraft.getInstance().execute(() -> {
                            VoxelConstants.getLogger().info("Received world name from settings: " + worldName);
                            VoxelConstants.getVoxelMapInstance().newSubWorldName(worldName, true);
                        });
                    } else {
                        VoxelConstants.getLogger().warn("Invalid world name: " + value);
                    }
                }
                case "minimapAllowed" -> VoxelConstants.getVoxelMapInstance().getMapOptions().minimapAllowed = (Boolean) value;
                case "worldmapAllowed" -> VoxelConstants.getVoxelMapInstance().getMapOptions().worldmapAllowed = (Boolean) value;
                case "teleportCommand" -> VoxelConstants.getVoxelMapInstance().getMapOptions().serverTeleportCommand = (String) value;
                default -> VoxelConstants.getLogger().warn("Unknown configuration option " + setting);
            }
        }
    }
}
