package com.mamiyaotaru.voxelmap.forge;

import com.mamiyaotaru.voxelmap.ModApiBridge;
import net.minecraftforge.fml.ModList;

public class ForgeModApiBridge implements ModApiBridge {
    @Override
    public boolean isModEnabled(String modID) {
        return ModList.get().isLoaded(modID);
    }
}
