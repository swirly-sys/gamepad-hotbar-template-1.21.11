package com.swirlysys.gamepadhotbar.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContextualBarRenderer.class)
public interface ContextualBarRendererMixin {
    @Inject(method = "top", at = @At("HEAD"), cancellable = true)
    private void beforeTop(Window window, CallbackInfoReturnable<Integer> cir) {
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isTrue() && GamepadHotbarClientConfig.LOWER_STATUS.isTrue())
            cir.setReturnValue(window.getGuiScaledHeight() - 5);
    }
    @ModifyVariable(method = "renderExperienceLevel", at = @At("STORE"), ordinal = 2)
    private static int onRenderXpLevel(int var10000) {
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isTrue() && GamepadHotbarClientConfig.LOWER_STATUS.isTrue())
            return var10000 + 24;
        return var10000;
    }
}
