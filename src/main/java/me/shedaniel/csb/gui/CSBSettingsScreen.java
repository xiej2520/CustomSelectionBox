package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSB;
import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.io.FileNotFoundException;

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
        addButton(new CSBSliderWidget(1, 4, this.height / 2 - 62, CSBConfig.getRed()));
        addButton(new CSBSliderWidget(2, 4, this.height / 2 - 38, CSBConfig.getGreen()));
        addButton(new CSBSliderWidget(3, 4, this.height / 2 - 14, CSBConfig.getBlue()));
        addButton(new CSBSliderWidget(4, 4, this.height / 2 + 10, CSBConfig.getAlpha()));
        addButton(new CSBSliderWidget(5, 4, this.height / 2 + 34, CSBConfig.getThickness() / 7.0F));
        addButton(new ButtonWidget(11, 4, this.height / 2 - 86, 150, 20, "Rainbow: " + ((CSBConfig.isRainbow()) ? "ON" : "OFF")));

        // right
        addButton(new CSBSliderWidget(7, this.width - 154, this.height / 2 - 44, CSBConfig.getBlinkAlpha()));
        addButton(new CSBSliderWidget(8, this.width - 154, this.height / 2 - 20, CSBConfig.getBlinkSpeed()));
        addButton(new ButtonWidget(10, this.width - 154, this.height / 2 - 68, 150, 20, "Break Animation: " + CSBConfig.getBreakAnimation()));
        addButton(new ButtonWidget(12, this.width - 154, this.height / 2 + 4, 150, 20, "Link Blocks: " + ((CSBConfig.isLinkBlocks()) ? "ON" : "OFF")));
        addButton(new ButtonWidget(13, this.width - 154, this.height / 2 + 28, 150, 20, "Show Hidden: " + ((CSBConfig.isShowHidden()) ? "ON" : "OFF")));

        // below
        addButton(new ButtonWidget(14, this.width / 2 - 100, this.height - 48, 95, 20, "Enabled: " + CSBConfig.isEnabled()));
        addButton(new ButtonWidget(20, this.width / 2 - 5, this.height - 48, 95, 20, "Save"));
        addButton(new ButtonWidget(21, this.width / 2 - 100, this.height - 24, 95, 20, "CSB Defaults"));
        addButton(new ButtonWidget(22, this.width / 2 - 5, this.height - 24, 95, 20, "MC defaults"));
    }

    @Override
    public void buttonClicked(ButtonWidget button) {
        if (button.id == 14) {
            CSBConfig.setEnabled(!CSBConfig.isEnabled());
            button.message = "Enabled: " + CSBConfig.isEnabled();
        } else if (button.id == 20) {
            try {
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save csb config");
                e.printStackTrace();
            }
            this.minecraft.openScreen(parent);
        } else if (button.id == 21) {
            try {
                CSBConfig.reset(false);
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save default csb config");
                e.printStackTrace();
            }
            CSB.openSettingsGUI(minecraft, parent);
        } else if (button.id == 22) {
            try {
                CSBConfig.reset(true);
                CSBConfig.saveConfig();
                this.configCache = new ConfigCache();
            } catch (FileNotFoundException e) {
                System.out.println("Failed to save mc default csb config");
                e.printStackTrace();
            }
            CSB.openSettingsGUI(minecraft, parent);
        } else if (button.id == 10) {
            CSBConfig.BreakAnimation[] bas = CSBConfig.BreakAnimation.values();
            int next = (CSBConfig.getBreakAnimation().ordinal() + 1) % bas.length;
            CSBConfig.setBreakAnimation(bas[next]);
            button.message = "Break Animation: " + CSBConfig.getBreakAnimation();
        } else if (button.id == 11) {
            CSBConfig.setIsRainbow(!CSBConfig.isRainbow());
            button.message = "Rainbow: " + (CSBConfig.isRainbow() ? "ON" : "OFF");
        } else if (button.id == 12) {
            CSBConfig.setLinkBlocks(!CSBConfig.isLinkBlocks());
            button.message = "Link Blocks: " + ((CSBConfig.isLinkBlocks()) ? "ON" : "OFF");
        } else if (button.id == 13) {
            CSBConfig.setShowHidden(!CSBConfig.isShowHidden());
            button.message = "Show Hidden: " + ((CSBConfig.isShowHidden()) ? "ON" : "OFF");
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if (this.minecraft.world == null) {
            this.drawBackgroundTexture(0);
        }
        fillGradient(0, 0, this.width, 48 - 4, -1072689136, -804253680); // top
        fillGradient(0, this.height / 2 - 91, 158, this.height / 2 + 59, -1072689136, -804253680); // left
        fillGradient(this.width - 158, this.height / 2 - 73, this.width, this.height / 2 + 53, -1072689136, -804253680); // right
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
            this.red = CSBConfig.getRed();
            this.green = CSBConfig.getGreen();
            this.blue =  CSBConfig.getBlue();
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
}