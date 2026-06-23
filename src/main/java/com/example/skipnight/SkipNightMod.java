package com.example.skipnight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Кладёт встроенный датапак "skipnight" в каждый мир (новый или уже существующий)
 * и автоматически включает его, чтобы не нужно было делать это руками.
 *
 * Сам датапак (data/skipnight/...) лежит зашитым внутри jar мода как
 * resources/skipnight_datapack.zip - Minecraft умеет читать датапаки прямо
 * из zip-файла, без распаковки.
 */
public class SkipNightMod implements ModInitializer {

    private static final String DATAPACK_RESOURCE = "/skipnight_datapack.zip";
    private static final String DATAPACK_FILE_NAME = "skipnight.zip";

    @Override
    public void onInitialize() {
        // Копируем датапак в папку datapacks мира до того, как сервер
        // окончательно прочитает список датапаков.
        ServerLifecycleEvents.SERVER_STARTING.register(this::installDatapack);

        // После старта сервера принудительно включаем датапак (если он ещё
        // не был включён) и сразу подгружаем его функции через /reload-эквивалент.
        ServerLifecycleEvents.SERVER_STARTED.register(this::ensureEnabled);
    }

    private void installDatapack(MinecraftServer server) {
        Path datapacksDir = server.getSavePath(WorldSavePath.DATAPACKS);
        try {
            Files.createDirectories(datapacksDir);
            Path target = datapacksDir.resolve(DATAPACK_FILE_NAME);

            try (InputStream in = SkipNightMod.class.getResourceAsStream(DATAPACK_RESOURCE)) {
                if (in == null) {
                    System.err.println("[SkipNight] Не найден " + DATAPACK_RESOURCE + " внутри jar мода!");
                    return;
                }
                // Перезаписываем при каждом старте - так обновление мода
                // автоматически обновит и сам датапак в старых мирах.
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[SkipNight] Датапак установлен: " + target);
            }
        } catch (IOException e) {
            System.err.println("[SkipNight] Не удалось установить датапак:");
            e.printStackTrace();
        }
    }

    private void ensureEnabled(MinecraftServer server) {
        server.execute(() -> {
            String command = "datapack enable \"file/" + DATAPACK_FILE_NAME + "\"";
            try {
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
            } catch (Exception e) {
                System.err.println("[SkipNight] Не удалось включить датапак автоматически: " + e.getMessage());
                System.err.println("[SkipNight] Включи вручную командой: /" + command);
            }
        });
    }
}
