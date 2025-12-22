package com.mamiyaotaru.voxelmap.forge;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = "voxelmap")
public class VoxelmapForgeMod {

    private static IEventBus modEventBus;

    public VoxelmapForgeMod() {
        VoxelmapForgeMod.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        VoxelConstants.setEvents(new ForgeEvents());
        VoxelConstants.setPacketBridge(new ForgePacketBridge());
    }

    public static IEventBus getModEventBus() {
        return modEventBus;
    }
}
