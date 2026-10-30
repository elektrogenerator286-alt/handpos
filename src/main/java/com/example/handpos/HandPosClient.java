package com.example.handpos;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class HandPosClient implements ClientModInitializer {
	public static final String MOD_ID = "handpos";
	private static final KeyBinding.Category CATEGORY =
			KeyBinding.Category.create(Identifier.of(MOD_ID, "controls"));
	private static KeyBinding menuKey;

	@Override
	public void onInitializeClient() {
		menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.handpos.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));
		ClientTickEvents.END_CLIENT_TICK.register(c -> {
			while (menuKey.wasPressed()) {
				if (c.currentScreen == null && c.player != null) c.setScreen(new HandPosScreen());
			}
		});
	}
}
