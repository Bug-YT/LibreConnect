package com.libreconnect.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import com.libreconnect.config.ConfigManager;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LibreConnectScreen extends Screen {

    private final Screen parent;

    private final int margin = 5;
    private int panelX, panelY, panelWidth, panelHeight;

    private float sidebarAnim = 1.0f;
    private boolean sidebarTargetOpen = true;
    private final int sidebarWidth = 120;

    private int scrollOffset = 0;
    private int maxScroll = 0;

    private enum Category {
        COSMETICS,
        SETTINGS,
        DISCORD
    }

    private Category currentCategory = Category.COSMETICS;

    private final List<ToggleEntry> toggles = new ArrayList<>();

    public LibreConnectScreen(Screen parent) {
        super(Component.literal("LibreConnect"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        panelX = margin;
        panelY = margin;
        panelWidth = this.width - margin * 2;
        panelHeight = this.height - margin * 2;

        int topPadding = 10;

        // Close Button
        this.addRenderableWidget(Button.builder(
                Component.literal("✖").withStyle(ChatFormatting.RED),
                b -> this.onClose()
        ).bounds(panelX + panelWidth - 25, panelY + topPadding, 20, 20).build());

        // Sidebar Toggle
        this.addRenderableWidget(Button.builder(
                Component.literal("≡"),
                b -> sidebarTargetOpen = !sidebarTargetOpen
        ).bounds(panelX + 5, panelY + topPadding, 20, 20).build());

        // Sidebar Buttons
        int startY = panelY + 40;
        int spacing = 24;

        this.addRenderableWidget(Button.builder(Component.literal("Cosmetics"),
                b -> { currentCategory = Category.COSMETICS; init(); })
                .bounds(panelX + 5, startY, sidebarWidth - 10, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Settings"),
                b -> { currentCategory = Category.SETTINGS; init(); })
                .bounds(panelX + 5, startY + spacing, sidebarWidth - 10, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Discord"),
                b -> { currentCategory = Category.DISCORD; init(); })
                .bounds(panelX + 5, startY + spacing * 2, sidebarWidth - 10, 20).build());
    }

    @Override
    public void render(@NonNull GuiGraphics context, int mouseX, int mouseY, float delta) {

        toggles.clear();

        // Animation
        float speed = 0.15f;
        sidebarAnim += sidebarTargetOpen ? speed : -speed;
        sidebarAnim = Math.max(0f, Math.min(1f, sidebarAnim));

        int currentSidebarWidth = (int)(sidebarWidth * sidebarAnim);

        // Background
        context.fill(0, 0, width, height, 0xFF2B2B2B);

        // Panel
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E1E1E);

        // Sidebar
        if (currentSidebarWidth > 0) {
            context.fill(panelX, panelY, panelX + currentSidebarWidth, panelY + panelHeight, 0xFF181818);
        }

        // Title
        context.drawString(this.font, this.title, panelX + 30, panelY + 15, 0xFFFFFF);

        // Content Area
        int contentX = panelX + currentSidebarWidth + 10;
        int contentY = panelY + 40;
        int contentWidth = panelWidth - currentSidebarWidth - 20;
        int contentHeight = panelHeight - 50;

        context.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeight);

        int y = contentY - scrollOffset;

        if (currentCategory == Category.COSMETICS) {
            y = drawToggle(context, contentX, y, "Wings",
                    () -> ConfigManager.data.wingsEnabled,
                    v -> ConfigManager.data.wingsEnabled = v);

            y = drawToggle(context, contentX, y, "Halo",
                    () -> ConfigManager.data.haloEnabled,
                    v -> ConfigManager.data.haloEnabled = v);
        }

        maxScroll = Math.max(0, y - (contentY + contentHeight));

        context.disableScissor();

        super.render(context, mouseX, mouseY, delta);
    }

    private int drawToggle(GuiGraphics context, int x, int y, String label,
                           Supplier<Boolean> getter,
                           Consumer<Boolean> setter) {

        int width = 200;

        boolean state = getter.get();

        context.drawString(this.font, label, x + 5, y + 6, 0xFFFFFF);

        int switchX = x + width - 50;
        int switchY = y + 3;

        int bgColor = state ? 0xFF4CAF50 : 0xFF555555;
        context.fill(switchX, switchY, switchX + 40, switchY + 14, bgColor);

        int knobX = state ? switchX + 25 : switchX + 2;
        context.fill(knobX, switchY + 1, knobX + 12, switchY + 13, 0xFFFFFFFF);

        toggles.add(new ToggleEntry(switchX, switchY, 40, 14, getter, setter));

        return y + 30;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (ToggleEntry t : toggles) {
                if (t.isHovered(event.x(), event.y())) {
                    boolean newState = !t.getter.get();
                    t.setter.accept(newState);
                    ConfigManager.save();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 10;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        return true;
    }

    private static class ToggleEntry {
        int x, y, w, h;
        Supplier<Boolean> getter;
        Consumer<Boolean> setter;

        ToggleEntry(int x, int y, int w, int h,
                    Supplier<Boolean> getter,
                    Consumer<Boolean> setter) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.getter = getter;
            this.setter = setter;
        }

        boolean isHovered(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}