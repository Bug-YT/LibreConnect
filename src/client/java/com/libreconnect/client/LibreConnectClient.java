package com.libreconnect.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import com.libreconnect.LibreConnect;
import com.libreconnect.client.screen.LibreConnectScreen;
import com.libreconnect.config.ConfigManager;

public class LibreConnectClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // MAIN MENU
            if (screen instanceof TitleScreen) {
                int x = screen.width / 2 + 104;
                int y = screen.height / 4 + 96;
                Component buttonText = Component.literal("⚡ ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(Component.translatable("libreconnect.menu_button")
                    .withStyle(style -> style.withBold(false).withItalic(false).withUnderlined(false).withObfuscated(false).withStrikethrough(false))
                    .withStyle(ChatFormatting.WHITE));
                    
                Screens.getButtons(screen).add(Button.builder(
                    buttonText,
                    button -> {
                        LibreConnect.LOGGER.info("Opening LibreConnect GUI...");
                        client.setScreen(new LibreConnectScreen(screen));
                    }
                ).bounds(x, y, 98, 20).build());
            }
            
            // PAUSE MENU
            if (screen instanceof PauseScreen) {
                int x = screen.width / 2 + 104;
                int y = screen.height / 4 + 80;
                
                Component buttonText = Component.literal("⚡ ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(Component.translatable("libreconnect.menu_button")
                    .withStyle(style -> style.withBold(false).withItalic(false).withUnderlined(false).withObfuscated(false).withStrikethrough(false))
                    .withStyle(ChatFormatting.WHITE));
                    
                Screens.getButtons(screen).add(Button.builder(
                    buttonText,
                    button -> {
                        LibreConnect.LOGGER.info("Opening LibreConnect GUI...");
                        client.setScreen(new LibreConnectScreen(screen));
                    }
                ).bounds(x, y, 98, 20).build());
            }
        });

        
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}
