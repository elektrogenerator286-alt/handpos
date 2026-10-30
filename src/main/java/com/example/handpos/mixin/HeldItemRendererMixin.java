package com.example.handpos.mixin;

import com.example.handpos.HandConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
	private void handpos$before(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
			float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
			OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
		matrices.push();
		HandConfig.Hand h = hand == Hand.MAIN_HAND ? HandConfig.INSTANCE.main : HandConfig.INSTANCE.off;
		Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		float side = arm == Arm.RIGHT ? 1f : -1f;
		matrices.translate(h.x * side, h.y, h.z);
		// масштаб и поворот вокруг примерного положения руки на экране
		matrices.translate(0.6 * side, -0.5, -0.8);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h.rx));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h.ry * side));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h.rz * side));
		matrices.scale(h.scale, h.scale, h.scale);
		matrices.translate(-0.6 * side, 0.5, 0.8);
	}

	@Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
	private void handpos$after(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
			float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
			OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
		matrices.pop();
	}
}
