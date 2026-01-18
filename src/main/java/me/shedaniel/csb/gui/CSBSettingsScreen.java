package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSBConfig;
import me.shedaniel.csb.utils.ConfigCache;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

import java.io.FileNotFoundException;

import static me.shedaniel.csb.CSB.openSettingsGUI;
import static me.shedaniel.csb.CSBConfig.*;

public class CSBSettingsScreen extends Screen {
    
    private final Screen parent;
    private ConfigCache configCache;
    
    public CSBSettingsScreen(Screen parent) {
        this.parent = parent;
    }
    
    @Override
    protected void keyPressed(char chr, int key) {
        if (chr == 1) {
            removed();
        }
        super.keyPressed(chr, key);
    }
    
    @Override
    public void init() {
        this.configCache = new ConfigCache(enabled, red, green, blue, alpha, thickness, blinkAlpha, blinkSpeed, disableDepthBuffer, rainbow, adjustBoundingBoxByLinkedBlocks);
        this.buttons.clear();
        // left
        addButton(new CSBSliderWidget(1, 4, this.height / 2 - 62, getRed()));
        addButton(new CSBSliderWidget(2, 4, this.height / 2 - 38, getGreen()));
        addButton(new CSBSliderWidget(3, 4, this.height / 2 - 14, getBlue()));
        addButton(new CSBSliderWidget(4, 4, this.height / 2 + 10, getAlpha()));
        addButton(new CSBSliderWidget(5, 4, this.height / 2 + 34, getThickness() / 7.0F));
        
        // right
        addButton(new CSBSliderWidget(7, this.width - 154, this.height / 2 - 14, getBlinkAlpha()));
        addButton(new CSBSliderWidget(8, this.width - 154, this.height / 2 + 10, getBlinkSpeed()));
        addButton(new ButtonWidget(11, this.width - 154, this.height / 2 - 38, 150, 20, "Chroma: " + ((usingRainbow()) ? "ON" : "OFF")));
        addButton(new ButtonWidget(12, this.width - 154, this.height / 2 + 34, 150, 20, "Link Blocks: " + ((isAdjustBoundingBoxByLinkedBlocks()) ? "ON" : "OFF")));
        //addButton(new ButtonWidget(this.width - 154, this.height / 2 + 34, 150, 20, new LiteralText("Link Blocks: " + (isAdjustBoundingBoxByLinkedBlocks() ? "ON" : "OFF")), widget -> {
        //    setAdjustBoundingBoxByLinkedBlocks(!isAdjustBoundingBoxByLinkedBlocks());
        //    widget.setMessage(new LiteralText("Link Blocks: " + (isAdjustBoundingBoxByLinkedBlocks() ? "ON" : "OFF")));
        //}));
        
        //below
        addButton(new ButtonWidget(13, this.width / 2 - 100, this.height - 48, 95, 20, "Enabled: " + (isEnabled() ? "True" : "False")));
        //addButton(new ButtonWidget(this.width / 2 - 100, this.height - 48, 95, 20, new LiteralText("Enabled: " + (isEnabled() ? "True" : "False")), widget -> {
        //    setEnabled(!isEnabled());
        //    widget.setMessage(new LiteralText("Enabled: " + (isEnabled() ? "True" : "False")));
        //}));
        addButton(new ButtonWidget(20, this.width / 2 - 5, this.height - 48, 95, 20, "Save"));
        //addButton(new ButtonWidget(this.width / 2 + 5, this.height - 48, 95, 20, new LiteralText("Save"), widget -> {
        //    try {
        //        saveConfig();
        //        configCache = new ConfigCache(CSBConfig.enabled, red, green, blue, alpha, thickness, blinkAlpha, blinkSpeed, disableDepthBuffer, rainbow, adjustBoundingBoxByLinkedBlocks);
        //    } catch (FileNotFoundException e) {
        //        e.printStackTrace();
        //    }
        //    client.openScreen(parent);
        //}));
        addButton(new ButtonWidget(21, this.width / 2 - 100, this.height - 24, 95, 20, "CSB Defaults"));
        //addButton(new ButtonWidget(this.width / 2 - 100, this.height - 24, 95, 20, new LiteralText("CSB defaults"), widget -> {
        //    try {
        //        reset(false);
        //        saveConfig();
        //        configCache = new ConfigCache(CSBConfig.enabled, red, green, blue, alpha, thickness, blinkAlpha, blinkSpeed, disableDepthBuffer, rainbow, adjustBoundingBoxByLinkedBlocks);
        //    } catch (FileNotFoundException e) {
        //        e.printStackTrace();
        //    }
        //    openSettingsGUI(client, parent);
        //}));
        addButton(new ButtonWidget(22, this.width / 2 + 5, this.height - 24, 95, 20, "MC defaults"));
        //addButton(new ButtonWidget(this.width / 2 + 5, this.height - 24, 95, 20, new LiteralText("MC defaults"), widget -> {
        //    try {
        //        reset(true);
        //        saveConfig();
        //        configCache = new ConfigCache(CSBConfig.enabled, red, green, blue, alpha, thickness, blinkAlpha, blinkSpeed, disableDepthBuffer, rainbow, adjustBoundingBoxByLinkedBlocks);
        //    } catch (FileNotFoundException e) {
        //        e.printStackTrace();
        //    }
        //    openSettingsGUI(client, parent);
        //}));
    }
    
    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if (this.minecraft.world == null) {
            this.drawBackgroundTexture(0);
        }
        fillGradient(0, 0, this.width, 48 - 4, -1072689136, -804253680); // top
        fillGradient(0, this.height / 2 - 67, 158, this.height / 2 + 59, -1072689136, -804253680); // left
        fillGradient(this.width - 158, this.height / 2 - 43, this.width, this.height / 2 + 59, -1072689136, -804253680); // right
        fillGradient(0, this.height - 48 - 4, this.width, this.height, -1072689136, -804253680); // bottom
        
        drawCenteredString(this.textRenderer, "Custom Selection Box", this.width / 2, (this.height - (this.height + 4 - 48)) / 2 - 4, 16777215);
        
        super.render(mouseX, mouseY, tickDelta);
    }
    
    @Override
    public boolean shouldPauseGame() {
        return false;
    }
    
    @Override
    public void removed() {
        configCache.save();
        this.configCache = new ConfigCache(enabled, red, green, blue, alpha, thickness, blinkAlpha, blinkSpeed, disableDepthBuffer, rainbow, adjustBoundingBoxByLinkedBlocks);
    }
    
}