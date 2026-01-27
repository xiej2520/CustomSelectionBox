package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSB;
import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.io.FileNotFoundException;

import static me.shedaniel.csb.gui.CSBSettingsScreen.ButtonId.*;

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
        this.configCache = new ConfigCache();
        this.buttons.clear();
        // left
        addButton(new ButtonWidget(RAINBOW.id, 4, height / 2 - 86, 150, 20, "Rainbow: " + ((CSBConfig.isRainbow()) ? "ON" : "OFF")));
        addButton(new CSBSliderWidget(RED.id, 4, height / 2 - 62, CSBConfig.getRed()));
        addButton(new CSBSliderWidget(GREEN.id, 4, height / 2 - 38, CSBConfig.getGreen()));
        addButton(new CSBSliderWidget(BLUE.id, 4, height / 2 - 14, CSBConfig.getBlue()));
        addButton(new CSBSliderWidget(ALPHA.id, 4, height / 2 + 10, CSBConfig.getAlpha()));
        addButton(new CSBSliderWidget(THICKNESS.id, 4, height / 2 + 34, CSBConfig.getThickness() / 7.0F));

        // right
        addButton(new ButtonWidget(BREAK_ANIMATION.id, width - 154, height / 2 - 86, 150, 20, "Break Animation: " + CSBConfig.getBreakAnimation()));
        addButton(new CSBSliderWidget(BLINK_ALPHA.id, width - 154, height / 2 - 62, CSBConfig.getBlinkAlpha()));
        addButton(new CSBSliderWidget(BLINK_SPEED.id, width - 154, height / 2 - 38, CSBConfig.getBlinkSpeed()));
        addButton(new ButtonWidget(LINK_BLOCKS.id, width - 154, height / 2 - 14, 150, 20, "Link Blocks: " + ((CSBConfig.isLinkBlocks()) ? "ON" : "OFF")));
        addButton(new ButtonWidget(SHOW_HIDDEN.id, width - 154, height / 2 + 10, 150, 20, "Show Hidden: " + ((CSBConfig.isShowHidden()) ? "ON" : "OFF")));
        addButton(new ButtonWidget(ENTITY_ENABLED.id, width - 154, height / 2 + 32, 150, 20, "Enable for Entities: " + (CSBConfig.isEntityEnabled() ? "ON" : "OFF")));

        // below
        addButton(new ButtonWidget(ENABLED.id, width / 2 - 100, height - 48, 95, 20, "Enabled: " + CSBConfig.isEnabled()));
        addButton(new ButtonWidget(CSB_DEFAULTS.id, width / 2 - 100, height - 24, 95, 20, "CSB Defaults"));

        addButton(new ButtonWidget(SAVE.id, width / 2 - 5, height - 48, 95, 20, "Save"));
        addButton(new ButtonWidget(MC_DEFAULTS.id, width / 2 - 5, height - 24, 95, 20, "MC defaults"));
    }

    @Override
    public void buttonClicked(ButtonWidget button) {
        if (button.id == ENABLED.id) {
            CSBConfig.setEnabled(!CSBConfig.isEnabled());
            button.message = "Enabled: " + CSBConfig.isEnabled();
        } else if (button.id == SAVE.id) {
            try {
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save csb config");
                e.printStackTrace();
            }
            this.minecraft.openScreen(parent);
        } else if (button.id == CSB_DEFAULTS.id) {
            try {
                CSBConfig.reset(false);
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save default csb config");
                e.printStackTrace();
            }
            CSB.openSettingsGUI(minecraft, parent);
        } else if (button.id == MC_DEFAULTS.id) {
            try {
                CSBConfig.reset(true);
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save mc default csb config");
                e.printStackTrace();
            }
            CSB.openSettingsGUI(minecraft, parent);
        } else if (button.id == BREAK_ANIMATION.id) {
            CSBConfig.BreakAnimation[] bas = CSBConfig.BreakAnimation.values();
            int next = (CSBConfig.getBreakAnimation().ordinal() + 1) % bas.length;
            CSBConfig.setBreakAnimation(bas[next]);
            button.message = "Break Animation: " + CSBConfig.getBreakAnimation();
        } else if (button.id == RAINBOW.id) {
            CSBConfig.setIsRainbow(!CSBConfig.isRainbow());
            button.message = "Rainbow: " + (CSBConfig.isRainbow() ? "ON" : "OFF");
        } else if (button.id == LINK_BLOCKS.id) {
            CSBConfig.setLinkBlocks(!CSBConfig.isLinkBlocks());
            button.message = "Link Blocks: " + ((CSBConfig.isLinkBlocks()) ? "ON" : "OFF");
        } else if (button.id == SHOW_HIDDEN.id) {
            CSBConfig.setShowHidden(!CSBConfig.isShowHidden());
            button.message = "Show Hidden: " + ((CSBConfig.isShowHidden()) ? "ON" : "OFF");
        } else if (button.id == ENTITY_ENABLED.id) {
            CSBConfig.setEntityEnabled(!CSBConfig.isEntityEnabled());
            button.message = "Enable for Entities: " + ((CSBConfig.isEntityEnabled()) ? "ON" : "OFF");
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if (this.minecraft.world == null) {
            this.drawBackgroundTexture(0);
        }
        fillGradient(0, 0, this.width, 48 - 4, -1072689136, -804253680); // top
        fillGradient(0, this.height / 2 - 91, 158, this.height / 2 + 59, -1072689136, -804253680); // left
        fillGradient(this.width - 158, this.height / 2 - 91, this.width, this.height / 2 + 59, -1072689136, -804253680); // right
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
        this.configCache.revertConfig();
    }


    static class ConfigCache {

        private final boolean enabled;
        private final boolean entityEnabled;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final float thickness;
        private final float blinkAlpha;
        private final float blinkSpeed;
        private final boolean rainbow;
        private final boolean linkBlocks;
        private final boolean showHidden;
        private final CSBConfig.BreakAnimation breakAnimation;

        public ConfigCache() {
            this.enabled = CSBConfig.isEnabled();
            this.entityEnabled = CSBConfig.isEntityEnabled();
            this.red = CSBConfig.getRed();
            this.green = CSBConfig.getGreen();
            this.blue = CSBConfig.getBlue();
            this.alpha = CSBConfig.getAlpha();
            this.thickness = CSBConfig.getThickness();
            this.blinkAlpha = CSBConfig.getBlinkAlpha();
            this.blinkSpeed = CSBConfig.getBlinkSpeed();
            this.rainbow = CSBConfig.isRainbow();
            this.linkBlocks = CSBConfig.isLinkBlocks();
            this.showHidden = CSBConfig.isShowHidden();
            this.breakAnimation = CSBConfig.getBreakAnimation();
        }

        public void revertConfig() {
            CSBConfig.enabled = this.enabled;
            CSBConfig.entityEnabled = this.entityEnabled;
            CSBConfig.red = this.red;
            CSBConfig.green = this.green;
            CSBConfig.blue = this.blue;
            CSBConfig.alpha = this.alpha;
            CSBConfig.thickness = this.thickness;
            CSBConfig.blinkAlpha = this.blinkAlpha;
            CSBConfig.blinkSpeed = this.blinkSpeed;
            CSBConfig.rainbow = this.rainbow;
            CSBConfig.linkBlocks = this.linkBlocks;
            CSBConfig.showHidden = this.showHidden;
            CSBConfig.breakAnimation = this.breakAnimation;
        }
    }

    enum ButtonId {
        RED(1),
        GREEN(2),
        BLUE(3),
        ALPHA(4),
        THICKNESS(5),
        RAINBOW(6),
        BLINK_ALPHA(7),
        BLINK_SPEED(8),
        BREAK_ANIMATION(9),
        LINK_BLOCKS(10),
        SHOW_HIDDEN(11),
        ENTITY_ENABLED(12),
        ENABLED(13),
        SAVE(14),
        CSB_DEFAULTS(15),
        MC_DEFAULTS(16);

        final int id;

        ButtonId(int id) {
            this.id = id;
        }
    }
}