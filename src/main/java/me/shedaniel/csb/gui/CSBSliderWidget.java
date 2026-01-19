package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class CSBSliderWidget extends ButtonWidget {

    public boolean isDragging;
    private float value;

    CSBSliderWidget(int id, int x, int y, float value) {
        super(id, x, y, 150, 20, "");
        this.value = value;
        this.message = this.getDisplayString(id);
    }

    private void updateValue(int id) {
        switch (id) {
            case 1:
                CSBConfig.setRed(this.value);
                break;
            case 2:
                CSBConfig.setGreen(this.value);
                break;
            case 3:
                CSBConfig.setBlue(this.value);
                break;
            case 4:
                CSBConfig.setAlpha(this.value);
                break;
            case 5:
                CSBConfig.setThickness(this.value * 7);
                break;
            case 7:
                CSBConfig.setBlinkAlpha(this.value);
                break;
            case 8:
                CSBConfig.setBlinkSpeed(this.value);
                break;
        }
    }

    private String getDisplayString(int id) {
        switch (id) {
            case 1:
                return "Red: " + Math.round(this.value * 255.0F);
            case 2:
                return "Green: " + Math.round(this.value * 255.0F);
            case 3:
                return "Blue: " + Math.round(this.value * 255.0F);
            case 4:
                return "Outline Alpha: " + Math.round(this.value * 255.0F);
            case 5:
                return "Outline Thickness: " + Math.round(this.value * 7.0F);
            case 7:
                return "Blink Alpha: " + Math.round(this.value * 255.0F);
            case 8:
                return "Blink Speed: " + Math.round(this.value * 100.0F);
        }
        return "Option Error?! (" + id + ")";
    }

    @Override
    protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.isDragging) {
                this.value = ((float) (mouseX - (this.x + 4)) / (float) (this.width - 8));
                this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);

                updateValue(this.id);
                this.message = this.getDisplayString(this.id);
            }

            minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x + (int) (this.value * (this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexture(this.x + (int) (this.value * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mouseClicked(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mouseClicked(minecraft, mouseX, mouseY)) {
            this.value = ((float) (mouseX - (this.x + 4)) / (float) (this.width - 8));

            if (this.value < 0.0F) {
                this.value = 0.0F;
            }

            if (this.value > 1.0F) {
                this.value = 1.0F;
            }

            this.message = getDisplayString(this.id);
            this.isDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.isDragging = false;
    }
}