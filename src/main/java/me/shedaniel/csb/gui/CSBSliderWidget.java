package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class CSBSliderWidget extends ButtonWidget {

    public boolean dragging;
    private float value;

    CSBSliderWidget(int id, int x, int y, float f) {
        super(id, x, y, 150, 20, "");
        this.value = f;
        this.message = getDisplayString(id);
    }

    private void updateValue(int id) {
        switch (id) {
            case 1:
                CSBConfig.setRed(getValue());
                break;
            case 2:
                CSBConfig.setGreen(getValue());
                break;
            case 3:
                CSBConfig.setBlue(getValue());
                break;
            case 4:
                CSBConfig.setAlpha(getValue());
                break;
            case 5:
                CSBConfig.setThickness(getValue() * 7);
                break;
            case 7:
                CSBConfig.setBlinkAlpha(getValue());
                break;
            case 8:
                CSBConfig.setBlinkSpeed(getValue());
                break;
        }
    }

    private float getValue() {
        return this.value;
    }

    private String getDisplayString(int id) {
        switch (id) {
            case 1:
                return "Red: " + Math.round(getValue() * 255.0F);
            case 2:
                return "Green: " + Math.round(getValue() * 255.0F);
            case 3:
                return "Blue: " + Math.round(getValue() * 255.0F);
            case 4:
                return "Outline Alpha: " + Math.round(getValue() * 255.0F);
            case 5:
                return "Outline Thickness: " + Math.round(getValue() * 7.0F);
            case 7:
                return "Blink Alpha: " + Math.round(getValue() * 255.0F);
            case 8:
                return "Blink Speed: " + Math.round(getValue() * 100.0F);
        }
        return "Option Error?! (" + id + ")";
    }

    @Override
    protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.value = ((float) (mouseX - (this.x + 4)) / (float) (this.width - 8));
                if (this.value < 0.0F) {
                    this.value = 0.0F;
                }

                if (this.value > 1.0F) {
                    this.value = 1.0F;
                }

                updateValue(this.id);
                this.message = getDisplayString(this.id);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}