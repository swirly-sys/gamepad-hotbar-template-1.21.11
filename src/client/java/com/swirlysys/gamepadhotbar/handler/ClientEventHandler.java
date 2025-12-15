package com.swirlysys.gamepadhotbar.handler;

import com.swirlysys.gamepadhotbar.GamepadHotbar;
import com.swirlysys.gamepadhotbar.GamepadHotbarClient;
import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.joml.Vector2i;

public class ClientEventHandler {
    private static final Identifier HOTBAR_0 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_0");
    private static final Identifier HOTBAR_1 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_1");
    private static final Identifier HOTBAR_2 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_2");
    private static final Identifier HOTBAR_3 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_3");
    private static final Identifier HOTBAR_4 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_4");
    private static final Identifier HOTBAR_5 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_5");
    private static final Identifier HOTBAR_6 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_6");
    private static final Identifier HOTBAR_7 = Identifier.fromNamespaceAndPath(GamepadHotbar.MOD_ID, "container/slot/hotbar_7");
    private static final Identifier HOTBAR_8 = Identifier.withDefaultNamespace("container/slot/sword");
    private static Identifier iterateSlotIcons(int index) {
        switch (index) {
            case 1 -> {return HOTBAR_1;}
            case 2 -> {return HOTBAR_2;}
            case 3 -> {return HOTBAR_3;}
            case 4 -> {return HOTBAR_4;}
            case 5 -> {return HOTBAR_5;}
            case 6 -> {return HOTBAR_6;}
            case 7 -> {return HOTBAR_7;}
            case 8 -> {return HOTBAR_8;}
            default -> {return HOTBAR_0;}
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = Minecraft.getInstance().player;
            if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse() || player == null) return;
            if (player.isSpectator()) return;
            int current = player.getInventory().getSelectedSlot();

            while (GamepadHotbarClient.getLeft().consumeClick())
                player.getInventory().setSelectedSlot(current == 0 ? 1 : 0);
            while (GamepadHotbarClient.getUp().consumeClick())
                player.getInventory().setSelectedSlot(current == 2 ? 3 : 2);
            while (GamepadHotbarClient.getRight().consumeClick())
                player.getInventory().setSelectedSlot(current == 5 ? 4 : 5);
            while (GamepadHotbarClient.getDown().consumeClick())
                player.getInventory().setSelectedSlot(current == 7 ? 6 : 7);
            while (GamepadHotbarClient.getWeapon().consumeClick()) player.getInventory().setSelectedSlot(8);
        });

        ScreenEvents.AFTER_INIT.register((client, container, scaledX, scaledY) -> {
            if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse()) return;
            ScreenEvents.afterBackground(container).register((screen, guiGfx, mouseX, mouseY, tickDelta) -> {

                if (screen instanceof AbstractContainerScreen<?> aScreen) {
                    if (aScreen.getMenu().getCarried().isEmpty()) return;

                    int adjuster = 0;
                    if (aScreen instanceof InventoryScreen) adjuster = 1;
                    if (aScreen instanceof CreativeModeInventoryScreen creativeScreen)
                        if (creativeScreen.isInventoryOpen()) adjuster = 2;

                    guiGfx.pose().pushMatrix();
                    guiGfx.pose().translate(aScreen.leftPos, aScreen.topPos);
                    int slotCount = aScreen.getMenu().slots.size();

                    for (int i = 0; i < 9; i++) {
                        Slot slot = aScreen.getMenu().getSlot(i + slotCount - 9 - adjuster);
                        if (slot.hasItem()) continue;

                        Identifier icon = iterateSlotIcons(i);
                        guiGfx.blitSprite(RenderPipelines.GUI_TEXTURED, icon, slot.x, slot.y, 16, 16);
                    }
                    guiGfx.pose().popMatrix();
                }
            });
        });
    }
}
