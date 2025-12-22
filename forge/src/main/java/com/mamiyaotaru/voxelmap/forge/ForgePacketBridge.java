package com.mamiyaotaru.voxelmap.forge;

import com.mamiyaotaru.voxelmap.PacketBridge;
import com.mamiyaotaru.voxelmap.packets.WorldIdC2S;

public class ForgePacketBridge implements PacketBridge {
    @Override
    public void sendWorldIDPacket() {
        ForgeEvents.CHANNEL.sendToServer(new WorldIdC2S());
    }
}
