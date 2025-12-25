package com.mamiyaotaru.voxelmap.gui.overridden;

public enum EnumOptionsMinimap {
    OLD_NORTH("options.minimap.oldNorth", false, true, false),
    ZOOM("option.minimapZoom", false, true, false),
    LOCATION("options.minimap.location", false, false, true),
    SIZE("options.minimap.size", false, false, true),
    MIN_ZOOM("options.worldmap.minZoom", true, false, false),
    MAX_ZOOM("options.worldmap.maxZoom", true, false, false),
    CACHE_SIZE("options.worldmap.cacheSize", true, false, false),
    MOVE_MAP_DOWN_WHILE_STATUS_EFFECT("options.minimap.moveMapBelowStatusEffectIcons", false, true, false),
    MOVE_SCOREBOARD_DOWN("options.minimap.moveScoreboardBelowMap", false, true, false);

    private final boolean isFloat;
    private final boolean isBoolean;
    private final boolean isList;
    private final String name;

    EnumOptionsMinimap(String name, boolean isFloat, boolean isBoolean, boolean isList) {
        this.name = name;
        this.isFloat = isFloat;
        this.isBoolean = isBoolean;
        this.isList = isList;
    }

    public boolean isFloat() {
        return this.isFloat;
    }

    public boolean isBoolean() {
        return this.isBoolean;
    }

    public boolean isList() {
        return this.isList;
    }

    public String getName() {
        return this.name;
    }
}
