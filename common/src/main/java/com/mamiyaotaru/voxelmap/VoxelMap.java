package com.mamiyaotaru.voxelmap;

import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import com.mamiyaotaru.voxelmap.persistent.PersistentMapSettingsManager;
import com.mamiyaotaru.voxelmap.persistent.ThreadManager;
import com.mamiyaotaru.voxelmap.util.BiomeRepository;
import com.mamiyaotaru.voxelmap.util.DimensionManager;
import com.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import com.mamiyaotaru.voxelmap.util.MapUtils;
import com.mamiyaotaru.voxelmap.util.TextUtils;
import com.mamiyaotaru.voxelmap.util.WorldUpdateListener;
import java.util.ArrayDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;

public class VoxelMap implements PreparableReloadListener {
    public static MapSettingsManager mapOptions;
    private PersistentMapSettingsManager persistentMapOptions;
    private Map map;
    private PersistentMap persistentMap;
    private SettingsAndLightingChangeNotifier settingsAndLightingChangeNotifier;
    private WorldUpdateListener worldUpdateListener;
    private ColorManager colorManager;
    private DimensionManager dimensionManager;
    private ClientLevel world;
    private static String passMessage;
    private ArrayDeque<Runnable> runOnWorldSet = new ArrayDeque<>();
    private String worldSeed = "";
    VoxelMap() {}

    public void lateInit(boolean showUnderMenus, boolean isFair) {
        mapOptions = new MapSettingsManager();
        mapOptions.showUnderMenus = showUnderMenus;
        this.persistentMapOptions = new PersistentMapSettingsManager();
        mapOptions.addSecondaryOptionsManager(this.persistentMapOptions);
        BiomeRepository.loadBiomeColors();
        this.colorManager = new ColorManager();
        this.dimensionManager = new DimensionManager();
        this.persistentMap = new PersistentMap();
        mapOptions.loadAll();

        // Event listeners are now registered separately during mod construction
        this.map = new Map();
        this.settingsAndLightingChangeNotifier = new SettingsAndLightingChangeNotifier();
        this.worldUpdateListener = new WorldUpdateListener();
        this.worldUpdateListener.addListener(this.map);
        this.worldUpdateListener.addListener(this.persistentMap);
        ReloadableResourceManager resourceManager = (ReloadableResourceManager) VoxelConstants.getMinecraft().getResourceManager();
        resourceManager.registerReloadListener(this);
        this.apply(resourceManager);
    }

    // TODO: 1.20.1 Port - SharedState doesn't exist in 1.20.1, ResourceManager passed directly
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, net.minecraft.util.profiling.ProfilerFiller preparationsProfiler, net.minecraft.util.profiling.ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return preparationBarrier.wait((Object) Unit.INSTANCE).thenRunAsync(() -> this.apply(resourceManager), gameExecutor);
    }

    private void apply(ResourceManager resourceManager) {
        this.colorManager.onResourceManagerReload(resourceManager);
    }

    public void onTickInGame(GuiGraphics guiGraphics) {
        if (this.map != null) {
            this.map.onTickInGame(guiGraphics);
        }
        if (passMessage != null) {
            VoxelConstants.getMinecraft().gui.getChat().addMessage(Component.literal(passMessage));
            passMessage = null;
        }
    }

    public void onTick() {
        ClientLevel newWorld = GameVariableAccessShim.getWorld();
        if (this.world != newWorld) {
            this.world = newWorld;
            this.persistentMap.newWorld(this.world);
            if (this.world != null) {
                MapUtils.reset();
                VoxelConstants.getPacketBridge().sendWorldIDPacket();
                this.map.newWorld(this.world);
                while (!runOnWorldSet.isEmpty()) {
                    runOnWorldSet.removeFirst().run();
                }
            }
        }

        VoxelConstants.tick();
        this.persistentMap.onTick();
    }

    public static void checkPermissionMessages(Component message) {
        String msg = TextUtils.asFormattedString(message);
        msg = msg.replaceAll("§r", "");

        if (msg.contains("§3 §6 §3 §6 §3 §6 §d")) {
            mapOptions.cavesAllowed = false;
            VoxelConstants.getLogger().info("Server disabled cavemapping.");
        }

        if (msg.contains("§3 §6 §3 §6 §3 §6 §f")) {
            mapOptions.cavesAllowed = true;
            VoxelConstants.getLogger().info("Server enabled cavemapping.");
        }
    }

    public MapSettingsManager getMapOptions() {
        return mapOptions;
    }

    public PersistentMapSettingsManager getPersistentMapOptions() {
        return this.persistentMapOptions;
    }

    public Map getMap() {
        return this.map;
    }

    public SettingsAndLightingChangeNotifier getSettingsAndLightingChangeNotifier() {
        return this.settingsAndLightingChangeNotifier;
    }

    public ColorManager getColorManager() {
        return this.colorManager;
    }

    public DimensionManager getDimensionManager() {
        return this.dimensionManager;
    }

    public PersistentMap getPersistentMap() {
        return this.persistentMap;
    }

    public void setPermissions(boolean hasCavemodePermission) {
        mapOptions.cavesAllowed = hasCavemodePermission;
    }

    public void sendPlayerMessageOnMainThread(String s) {
        passMessage = s;
    }

    public WorldUpdateListener getWorldUpdateListener() {
        return this.worldUpdateListener;
    }

    public void clearServerSettings() {
        mapOptions.cavesAllowed = true;
        mapOptions.serverTeleportCommand = null;
        mapOptions.worldmapAllowed = true;
        mapOptions.minimapAllowed = true;
    }

    public void onPlayInit() {
        // registries are ready, but no world
    }

    public void onJoinServer() {
        // No-op after radar removal
    }

    public void onDisconnect() {
        clearServerSettings();
    }

    public void onConfigurationInit() {
        clearServerSettings();
    }

    public void onClientStopping() {
        VoxelConstants.onShutDown();
        ThreadManager.flushSaveQueue();
    }

    /**
     * Gets the current world name for caching purposes.
     * Returns the singleplayer world name or multiplayer server name.
     */
    public String getCurrentWorldName() {
        if (VoxelConstants.isSinglePlayer()) {
            return VoxelConstants.getIntegratedServer()
                    .map(server -> server.getWorldData().getLevelName())
                    .filter(name -> name != null && !name.isBlank())
                    .orElse("Singleplayer World");
        } else {
            ServerData info = VoxelConstants.getMinecraft().getCurrentServer();
            if (info != null && info.name != null && !info.name.isBlank()) {
                return info.name;
            }
            if (VoxelConstants.isRealmServer()) {
                return "Realms";
            }
            return "Multiplayer Server";
        }
    }

    /**
     * Placeholder for subworld name functionality that was removed.
     * Always returns empty string since waypoint/subworld system was removed.
     */
    public void newSubWorldName(String name, boolean fromServer) {
        // No-op after waypoint system removal
    }

    /**
     * Gets the world seed used for slime chunk calculation.
     * For singleplayer, automatically retrieves from server.
     */
    public String getWorldSeed() {
        if (VoxelConstants.isSinglePlayer()) {
            return VoxelConstants.getIntegratedServer()
                    .map(server -> String.valueOf(server.getWorldData().worldGenOptions().seed()))
                    .orElse("");
        }
        return this.worldSeed;
    }

    /**
     * Sets the world seed for multiplayer slime chunk calculation.
     */
    public void setWorldSeed(String seed) {
        this.worldSeed = seed != null ? seed : "";
    }
}
