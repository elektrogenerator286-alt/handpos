package com.example.handpos;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class HandPosScreen extends Screen {
	private boolean offHand;

	public HandPosScreen() { super(Text.literal("Hand Pos")); }

	private HandConfig.Hand cur() {
		return offHand ? HandConfig.INSTANCE.off : HandConfig.INSTANCE.main;
	}

	private static final class Slider extends SliderWidget {
		private final String name;
		private final float min, max;
		private final Consumer<Float> setter;

		Slider(int x, int y, int w, String name, float min, float max, float value, Consumer<Float> setter) {
			super(x, y, w, 20, Text.empty(), (value - min) / (max - min));
			this.name = name; this.min = min; this.max = max; this.setter = setter;
			updateMessage();
		}

		private float val() { return (float) (min + (max - min) * this.value); }

		@Override protected void updateMessage() {
			setMessage(Text.literal(name + ": " + String.format("%.2f", val())));
		}

		@Override protected void applyValue() { setter.accept(val()); }
	}

	@Override
	protected void init() {
		clearChildren();
		HandConfig.Hand h = cur();
		int w = 200, x = width / 2 - w / 2, y = height / 2 - 100;
		addDrawableChild(ButtonWidget.builder(
				Text.literal(offHand ? "Рука: ВТОРАЯ (нажми — основная)" : "Рука: ОСНОВНАЯ (нажми — вторая)"),
				b -> { offHand = !offHand; init(); }).dimensions(x, y, w, 20).build());
		String[] names = {"Сдвиг X", "Сдвиг Y", "Сдвиг Z", "Поворот X", "Поворот Y", "Поворот Z", "Размер"};
		float[] mins = {-1, -1, -1, -180, -180, -180, 0.05f};
		float[] maxs = {1, 1, 1, 180, 180, 180, 2f};
		float[] vals = {h.x, h.y, h.z, h.rx, h.ry, h.rz, h.scale};
		for (int i = 0; i < 7; i++) {
			final int idx = i;
			addDrawableChild(new Slider(x, y + 24 * (i + 1), w, names[i], mins[i], maxs[i], vals[i], v -> {
				HandConfig.Hand hh = cur();
				switch (idx) {
					case 0 -> hh.x = v; case 1 -> hh.y = v; case 2 -> hh.z = v;
					case 3 -> hh.rx = v; case 4 -> hh.ry = v; case 5 -> hh.rz = v;
					default -> hh.scale = v;
				}
			}));
		}
		addDrawableChild(ButtonWidget.builder(Text.literal("Сбросить эту руку"), b -> {
			HandConfig.Hand n = new HandConfig.Hand();
			if (offHand) HandConfig.INSTANCE.off = n; else HandConfig.INSTANCE.main = n;
			init();
		}).dimensions(x, y + 24 * 8, w / 2 - 2, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
				.dimensions(x + w / 2 + 2, y + 24 * 8, w / 2 - 2, 20).build());
	}

	@Override
	public void removed() { HandConfig.save(); }

	@Override
	public boolean shouldPause() { return false; }
}
