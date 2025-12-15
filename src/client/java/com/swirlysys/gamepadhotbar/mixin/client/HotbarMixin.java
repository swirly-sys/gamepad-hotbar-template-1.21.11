package com.swirlysys.gamepadhotbar.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.swirlysys.gamepadhotbar.GamepadHotbar;
import com.swirlysys.gamepadhotbar.GamepadHotbarClient;
import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import com.swirlysys.gamepadhotbar.util.HotbarPos;
import com.swirlysys.gamepadhotbar.util.HotbarScale;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarMixin {
    @Unique
    private static final Identifier HOTBAR_SPRITE = Identifier.withDefaultNamespace("hud/hotbar");
    @Unique
    private static final Identifier HOTBAR_SELECTION_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_selection");
    @Unique
    private static final Identifier HOTBAR_OFFHAND_LEFT_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
    @Unique
    private static final Identifier HOTBAR_OFFHAND_RIGHT_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_right");
    @Unique
    private static final Identifier TAP_HOTBAR_LEFT = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "hud/tap_hotbar_left");
    @Unique
    private static final Identifier TAP_HOTBAR_UP = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "hud/tap_hotbar_up");
    @Unique
    private static final Identifier TAP_HOTBAR_RIGHT = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "hud/tap_hotbar_right");
    @Unique
    private static final Identifier TAP_HOTBAR_DOWN = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "hud/tap_hotbar_down");
    @Unique
    private static final Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace(
            "hud/hotbar_attack_indicator_background"
    );
    @Unique
    private static final Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = Identifier.withDefaultNamespace(
            "hud/hotbar_attack_indicator_progress"
    );
    @Unique
    private static Vector2i iteratePos(int index, int scale, int arm) {
        Vector2i vec = new Vector2i(0, 0);
        switch (index) {
            case 1 -> {return vec.add(-41 + scale, -31);}
            case 2 -> {return vec.add(-20 + scale, -41);}
            case 3 -> {return vec.add(scale, -41);}
            case 4 -> {return vec.add(21 + scale, -31);}
            case 5 -> {return vec.add(21 + scale, -11);}
            case 6 -> {return vec.add(scale, 0);}
            case 7 -> {return vec.add(-20 + scale, 0);}
            case 8 -> {return vec.add(4 - arm - scale, -20);}
            default -> {return vec.add(-41 + scale, -11);}
        }
    }
    @Unique
    private static void renderSlot(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = stack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(x + 8, y + 12);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F);
                guiGraphics.pose().translate(-(x + 8), -(y + 12));
            }

            guiGraphics.renderItem(player, stack, x, y, seed);
            if (f > 0.0F) {
                guiGraphics.pose().popMatrix();
            }

            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
        }
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderItemHotbar(GuiGraphics guiGfx, DeltaTracker parTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse() || mc.options.hideGui) return;

        Entity entity = mc.getCameraEntity();
        if (entity instanceof Player player) {
            // Vanilla hotbar is not rendered
            ci.cancel();

            ItemStack offHand = player.getOffhandItem();
            HumanoidArm offArm = player.getMainArm().getOpposite();

            // Base variables
            int screenLeft = 43;
            int screenRight = guiGfx.guiWidth() - screenLeft;
            int flipVar1 = 1;
            int flipVar2 = 0;
            int scaleVar = GamepadHotbarClientConfig.PAD_X.get();

            // Configuration adjustments
            if (GamepadHotbarClientConfig.MIRROR_MODE.getAsBoolean()) {
                screenRight = screenLeft;
                screenLeft = guiGfx.guiWidth() - screenRight;
                scaleVar *= -1;
                flipVar1 *= -1;
                flipVar2 = 100;
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE2) {
                screenLeft = (guiGfx.guiWidth() / 2) - (140 * flipVar1);
                screenRight = (guiGfx.guiWidth() / 2) + (140 * flipVar1);
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE3) {
                screenLeft = (guiGfx.guiWidth() / 2) - (50 * flipVar1);
                screenRight = (guiGfx.guiWidth() / 2) + (50 * flipVar1);
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE4) {
                screenLeft = 43 + flipVar2;
                screenRight = 143 - flipVar2;
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE5) {
                screenLeft = guiGfx.guiWidth() - 143 + flipVar2;
                screenRight = guiGfx.guiWidth() - 43 - flipVar2;
            }
            int baseY = GamepadHotbarClientConfig.POS_Y.get() == HotbarPos.TOP ? 64 + GamepadHotbarClientConfig.PAD_Y.get() : guiGfx.guiHeight() - GamepadHotbarClientConfig.PAD_Y.get();

            // Adjustment variables
            int uWidth;
            int uPos;
            int xPos;
            int yPos;
            int slotY = baseY - 22;
            int selectY = slotY - 1;
            int itemY = slotY + 3;
            int baseX;
            int armVar = offArm == HumanoidArm.RIGHT ? 28 : 0;

            // Hotbar slots 1-9
            // Extra blitSprite calls are made here to fill out each slot pair with the outline portion of the hotbar texture
            for (int i = 0; i < 5; i++) {
                switch (i) {
                    case 1 -> {
                        //  X X
                        // .   .
                        // .   .         .
                        //  . .
                        uWidth = 40;
                        uPos = 41;
                        xPos = -20 + scaleVar;
                        yPos = -41;
                        baseX = screenLeft;
                        guiGfx.pose().rotateAbout((float) Math.toRadians(90.0F), baseX, slotY);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 2 -> {
                        //  . .
                        // .   X
                        // .   X         .
                        //  . .
                        uWidth = 40;
                        uPos = 81;
                        xPos = -30;
                        yPos = -42 - scaleVar;
                        baseX = screenLeft;
                        guiGfx.pose().rotateAbout((float) Math.toRadians(90.0F), baseX, slotY);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 3 -> {
                        //  . .
                        // .   .
                        // .   .         .
                        //  X X
                        uWidth = 40;
                        uPos = 121;
                        xPos = -20 + scaleVar;
                        yPos = 0;
                        baseX = screenLeft;
                        guiGfx.pose().rotateAbout((float) Math.toRadians(-90.0F), baseX, slotY);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 4 -> {
                        //  . .
                        // .   .
                        // .   .         X
                        //  . .
                        uWidth = 21;
                        uPos = 161;
                        xPos = 4 - armVar - scaleVar;
                        yPos = -20;
                        baseX = screenRight;
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                    }
                    default -> {
                        //  . .
                        // X   .
                        // X   .         .
                        //  . .
                        uPos = 0;
                        uWidth = 41;
                        xPos = -11;
                        yPos = -42 + scaleVar;
                        baseX = screenLeft;
                        guiGfx.pose().rotateAbout((float) Math.toRadians(-90.0F), baseX, slotY);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                }
                guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22,
                        uPos, 0, baseX + xPos, slotY + yPos, uWidth, 22
                );
            }

            // Selected hotbar slot
            int selected = player.getInventory().getSelectedSlot();
            if (selected >= 0 && selected < 8) {
                baseX = screenLeft;
            } else baseX = screenRight;

            int selectX = baseX - 2;
            xPos = iteratePos(selected, scaleVar, armVar).x;
            yPos = iteratePos(selected, scaleVar, armVar).y;
            guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_SPRITE, selectX + xPos, selectY + yPos, 24, 23);

            // Off-hand hotbar slot
            if (!offHand.isEmpty()) {
                if (offArm == HumanoidArm.LEFT) {
                    guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, screenRight + 4 - 29 - scaleVar, slotY - 21, 29, 24);
                } else {
                    guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_RIGHT_SPRITE, screenRight + 4 - armVar + 20 - scaleVar, slotY - 21, 29, 24);
                }
            }

            // Custom tap button indicator
            Identifier tap = null;
            int tapOffset = 8;
            int color = ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F);
            if (GamepadHotbarClient.getLeft().isDown() || GamepadHotbarClient.getRight().isDown() || GamepadHotbarClient.getUp().isDown() || GamepadHotbarClient.getDown().isDown()) {
                color = ARGB.colorFromFloat(1.0F, 1.0F, 0.8F, 0.0F);
            }
            if (selected == 0 || selected == 1) {
                tap = TAP_HOTBAR_LEFT;
                tapOffset = 17;
            }
            if (selected == 2 || selected == 3) {
                tap = TAP_HOTBAR_UP;
            }
            if (selected == 4 || selected == 5) {
                tap = TAP_HOTBAR_RIGHT;
                tapOffset = -1;
            }
            if (selected == 6 || selected == 7) {
                tap = TAP_HOTBAR_DOWN;
            }
            if (selected >= 0 && selected < 8) {
                guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, tap, baseX - tapOffset + scaleVar, slotY - 18, 16, 16, color);
            }

            // Items
            int itemX;
            int l = 1;
            for (int j = 0; j < 9; j++) {
                xPos = iteratePos(j, scaleVar, armVar).x;
                yPos = iteratePos(j, scaleVar, armVar).y;
                if (j < 8) {
                    itemX = screenLeft + 2;
                } else itemX = screenRight + 2;
                renderSlot(guiGfx, itemX + xPos, itemY + yPos, parTick, player, player.getInventory().getItem(j), l++);
            }

            // Off-hand slot
            if (!offHand.isEmpty()) {
                itemX = screenRight + 2;
                renderSlot(guiGfx, itemX - 24 + armVar - scaleVar, itemY - 20, parTick, player, offHand, l);
            }

            // Hotbar attack indicator
            if (mc.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                float f = player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = baseY - 62;
                    int k2 = screenRight + 5 - armVar - scaleVar;

                    int l1 = (int)(f * 19.0F);
                    guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }
            }
        }
    }
}