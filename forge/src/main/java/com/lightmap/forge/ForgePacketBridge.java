package com.lightmap.forge;

import com.lightmap.PacketBridge;
import com.lightmap.packets.WorldIdC2S;

public class ForgePacketBridge implements PacketBridge {
    @Override
    public void sendWorldIDPacket() {
        ForgeEvents.CHANNEL.sendToServer(new WorldIdC2S());
    }
}
