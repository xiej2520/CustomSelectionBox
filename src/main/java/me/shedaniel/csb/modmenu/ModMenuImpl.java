package me.shedaniel.csb.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.csb.gui.CSBSettingsScreen;

public class ModMenuImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return CSBSettingsScreen::new;
    }
}
