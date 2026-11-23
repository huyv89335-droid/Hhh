package com.autobuilder;

import com.autobuilder.client.AutoBuilderScreen;
import com.autobuilder.client.BuildController;
import com.autobuilder.client.WebhookNotifier;
import com.autobuilder.config.AutoBuilderConfig;
import com.autobuilder.schematic.BundledSchematics;
import com.autobuilder.schematic.Schematic;
import com.autobuilder.schematic.SchematicLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Điểm vào duy nhất của mod (client-only).
 *
 * Không có ServerModInitializer. Fabric Loader sẽ không tải class này khi
 * chạy server dedicated.
 */
public final class AutoBuilderClient implements ClientModInitializer {
    private static AutoBuilderConfig config = new AutoBuilderConfig();
    private static final BuildController CONTROLLER = new BuildController();

    private static List<Schematic> schematics = List.of();
    private static List<String> loadWarnings = List.of();
    private static Path schematicsDirectory = Path.of("schematics");
    private static Schematic selectedSchematic;

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // BƯỚC 1 — Đăng ký phím TRƯỚC khi đọc config/schematic.
        // Nếu để sau, một lỗi I/O hoặc NBT có thể chặn đăng ký phím và menu
        // sẽ không bao giờ xuất hiện trong Controls.
        KeyBinding.Category category = KeyBinding.Category.create(AutoBuilder.id("main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autobuilder.open",
                GLFW.GLFW_KEY_K,
                category
        ));
        AutoBuilder.LOGGER.info("AutoBuilder menu key registered: GLFW {} (category {})",
                GLFW.GLFW_KEY_K, category);

        // BƯỚC 2 — Lệnh dự phòng, hữu ích khi phím bị mod khác chiếm
        // hoặc bàn phím ảo trên điện thoại không gửi phím K.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("autobuilder")
                    .executes(context -> openMenuFromCommand()));
            dispatcher.register(ClientCommandManager.literal("ab")
                    .executes(context -> openMenuFromCommand()));
        });

        // BƯỚC 3 — Nạp dữ liệu, mỗi phần đều được cô lập.
        try {
            config = AutoBuilderConfig.load();
        } catch (Exception exception) {
            AutoBuilder.LOGGER.error("Config load failed; using defaults. Menu remains available.", exception);
            config = new AutoBuilderConfig();
        }

        try {
            reloadSchematics();
        } catch (Exception exception) {
            AutoBuilder.LOGGER.error("Schematic scan failed; menu remains available.", exception);
            schematics = List.of();
            loadWarnings = List.of("Quét bản vẽ lỗi: " + exception.getMessage());
            selectedSchematic = null;
            CONTROLLER.setSchematic(null);
        }

        // BƯỚC 4 — Tick: mở menu + chạy controller.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                AutoBuilder.LOGGER.info("AutoBuilder open key pressed.");
                if (client.currentScreen == null) {
                    client.setScreen(new AutoBuilderScreen());
                }
            }
            CONTROLLER.tick(client);
        });

        // BƯỚC 5 — Xác nhận mod đã nạp ngay khi vào thế giới.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(
                            "AutoBuilder đã tải · nhấn K hoặc gõ /autobuilder để mở menu"
                    ), false);
                }
            });
            AutoBuilder.LOGGER.info("AutoBuilder joined world; press K or run /autobuilder.");
        });

        AutoBuilder.LOGGER.info(
                "AutoBuilder v2.0.0 initialized: {} schematic(s) ({} bundled), config at {}",
                schematics.size(),
                BundledSchematics.all().size(),
                AutoBuilderConfig.configPath()
        );
    }

    private static int openMenuFromCommand() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(new AutoBuilderScreen()));
        return 1;
    }

    /**
     * Quét lại thư mục schematic. Gọi được từ GUI mà không cần thoát game.
     * Bước 2: bản vẽ có sẵn trong JAR luôn đứng đầu danh sách — menu không bao giờ trống.
     */
    public static void reloadSchematics() {
        SchematicLoader.LoadResult result = SchematicLoader.loadAll();
        List<Schematic> merged = new ArrayList<>();
        merged.addAll(BundledSchematics.all());
        merged.addAll(result.schematics());
        schematics = List.copyOf(merged);
        loadWarnings = result.warnings();
        schematicsDirectory = result.directory();

        Schematic match = null;
        if (!config.selectedSchematic.isBlank()) {
            for (Schematic candidate : schematics) {
                if (candidate.fileName().equals(config.selectedSchematic)) {
                    match = candidate;
                    break;
                }
            }
        }

        // Lần đầu mở mod: chọn sẵn tệp đầu tiên để người mới không gặp màn hình trống.
        if (match == null && !schematics.isEmpty()) {
            match = schematics.getFirst();
        }

        selectedSchematic = match;
        if (match != null) {
            config.selectedSchematic = match.fileName();
            CONTROLLER.setSchematic(match);
        } else {
            config.selectedSchematic = "";
            CONTROLLER.setSchematic(null);
        }

        for (String warning : loadWarnings) {
            AutoBuilder.LOGGER.warn("Schematic warning: {}", warning);
        }
    }

    public static List<Schematic> schematics() {
        return schematics;
    }

    public static List<String> loadWarnings() {
        return loadWarnings;
    }

    public static Path schematicsDirectory() {
        return schematicsDirectory;
    }

    public static Schematic selectedSchematic() {
        return selectedSchematic;
    }

    public static AutoBuilderConfig config() {
        return config;
    }

    public static BuildController controller() {
        return CONTROLLER;
    }

    public static void selectSchematic(Schematic schematic) {
        selectedSchematic = schematic;
        if (schematic != null) {
            config.selectedSchematic = schematic.fileName();
            CONTROLLER.setSchematic(schematic);
        } else {
            config.selectedSchematic = "";
            CONTROLLER.setSchematic(null);
        }
    }

    /**
     * Gửi webhook nếu người dùng đã cấu hình URL. Bỏ qua khi URL trống.
     */
    public static void webhook(String message) {
        WebhookNotifier.send(config.webhookUrl, message);
    }
}