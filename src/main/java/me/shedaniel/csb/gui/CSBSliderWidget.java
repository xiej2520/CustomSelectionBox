package me.shedaniel.csb.gui;

import me.shedaniel.csb.CSBConfig;
import net.minecraft.client.gui.widget.SliderWidget;

public class CSBSliderWidget extends SliderWidget {

    private int id;

    CSBSliderWidget(int i, int x, int y, float f) {
        super(x, y, 150, 20, f);
        this.id = i;
        this.setMessage(getDisplayString(i));
    }

    @Override
    protected void updateMessage() {
        setMessage(getDisplayString(id));
    }

    @Override
    protected void applyValue() {
        updateValue(id);
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
        }
    }

    private float getValue() {
        return (float) value;
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
}
