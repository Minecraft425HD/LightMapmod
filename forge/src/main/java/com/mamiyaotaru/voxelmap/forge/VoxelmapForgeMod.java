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
        // Initialize events immediately to register all event listeners during mod loading
        VoxelConstants.getEvents().initEvents(VoxelConstants.getVoxelMapInstance());
    }

    public static IEventBus getModEventBus() {
        return modEventBus;
    }
}
