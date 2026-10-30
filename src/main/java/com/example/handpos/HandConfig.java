package com.example.handpos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Настройки двух рук. Хранятся в config/handpos.json. */
public final class HandConfig {
	public static final class Hand {
		public float x, y, z;          // сдвиг (блоки)
		public float rx, ry, rz;       // поворот (градусы)
		public float scale = 1f;       // размер
	}

	public Hand main = new Hand();
	public Hand off = new Hand();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("handpos.json");
	public static HandConfig INSTANCE = load();

	private static HandConfig load() {
		try {
			if (Files.exists(FILE)) {
				HandConfig c = GSON.fromJson(Files.readString(FILE), HandConfig.class);
				if (c != null && c.main != null && c.off != null) return c;
			}
		} catch (Exception ignored) { }
		return new HandConfig();
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(INSTANCE));
		} catch (Exception ignored) { }
	}
}
