package com.wsadqert.fixitem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FixCommandConfig {
	private static final Gson GSON = new Gson();
	private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("fix_commands.json");
	private static boolean enabled = true;

	public static void load() {
		if (Files.exists(CONFIG_PATH)) {
			try {
				String json = Files.readString(CONFIG_PATH);
				JsonObject obj = GSON.fromJson(json, JsonObject.class);
				enabled = obj.has("enabled") && obj.get("enabled").getAsBoolean();
			} catch (IOException e) {
				enabled = true;
			}
		} else {
			save(); // default
		}
	}

	public static void save() {
		JsonObject obj = new JsonObject();
		obj.addProperty("enabled", enabled);
		try {
			Files.writeString(CONFIG_PATH, GSON.toJson(obj));
		} catch (IOException ignored) {}
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean val) {
		enabled = val;
		save();
	}
}
