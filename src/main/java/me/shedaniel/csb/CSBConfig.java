package me.shedaniel.csb;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class CSBConfig implements ClientModInitializer {

    public static boolean enabled;
    public static boolean entityEnabled;
    public static float red;
    public static float green;
    public static float blue;
    public static float alpha;
    public static float thickness;
    public static float blinkAlpha;
    public static float blinkSpeed;
    public static boolean linkBlocks;
    public static boolean showHidden;
    public static boolean rainbow;
    public static BreakAnimation breakAnimation;
    private static final File configFile = new File(FabricLoader.getInstance().getGameDirectory(), "config" + File.separator + "CSB" + File.separator + "config.json");

    private static void loadConfig() throws IOException {
        configFile.getParentFile().mkdirs();
        String content = configFile.exists() ? FileUtils.readFileToString(configFile, "utf-8") : "{}";
        JsonElement jsonElement = new JsonParser().parse(content);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        enabled = !jsonObject.has("enabled") || jsonObject.get("enabled").getAsBoolean();
        entityEnabled = jsonObject.has("entityEnabled") && jsonObject.get("entityEnabled").getAsBoolean();
        red = jsonObject.has("colourRed") ? jsonObject.get("colourRed").getAsInt() / 255.0F : 0F;
        green = jsonObject.has("colourGreen") ? jsonObject.get("colourGreen").getAsInt() / 255.0F : 0F;
        blue = jsonObject.has("colourBlue") ? jsonObject.get("colourBlue").getAsInt() / 255.0F : 0F;
        alpha = jsonObject.has("alpha") ? jsonObject.get("alpha").getAsInt() / 255.0F : 1F;
        thickness = jsonObject.has("thickness") ? jsonObject.get("thickness").getAsInt() : 2F;
        blinkSpeed = jsonObject.has("blinkSpeed") ? jsonObject.get("blinkSpeed").getAsInt() / 100.0F : 0.2F;
        blinkAlpha = jsonObject.has("blinkAlpha") ? jsonObject.get("blinkAlpha").getAsInt() / 255.0F : 0.390625F;
        rainbow = jsonObject.has("rainbow") && jsonObject.get("rainbow").getAsBoolean();
        linkBlocks = jsonObject.has("linkBlocks") && jsonObject.get("linkBlocks").getAsBoolean();
        showHidden = jsonObject.has("showHidden") && jsonObject.get("showHidden").getAsBoolean();
        breakAnimation = jsonObject.has("breakAnimation") ? BreakAnimation.values()[jsonObject.get("breakAnimation").getAsInt()] : BreakAnimation.NONE;

        saveConfig();
    }

    public static void saveConfig() throws FileNotFoundException {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", enabled);
        object.addProperty("entityEnabled", entityEnabled);
        object.addProperty("colourRed", (int) (red * 255));
        object.addProperty("colourGreen", (int) (green * 255));
        object.addProperty("colourBlue", (int) (blue * 255));
        object.addProperty("alpha", (int) (alpha * 255));
        object.addProperty("thickness", (int) thickness);
        object.addProperty("blinkSpeed", (int) (blinkSpeed * 100));
        object.addProperty("blinkAlpha", (int) (blinkAlpha * 255));
        object.addProperty("rainbow", rainbow);
        object.addProperty("linkBlocks", linkBlocks);
        object.addProperty("showHidden", showHidden);
        object.addProperty("breakAnimation", breakAnimation.ordinal());
        if (configFile.exists()) {
            configFile.delete();
        }
        PrintWriter writer = new PrintWriter(configFile);
        writer.print(objectToString(object));
        writer.close();
    }

    private static String objectToString(JsonObject object) {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            jsonWriter.setIndent("\t");
            Streams.write(object, jsonWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static void reset(boolean mc) throws FileNotFoundException {
        setEnabled(true);
        setEntityEnabled(false);
        setRed(0.0F);
        setGreen(0.0F);
        setBlue(0.0F);
        setAlpha(mc ? 0.4F : 1.0F);
        setThickness(mc ? 1.0F : 2.0F);
        setBlinkAlpha(mc ? 0.0F : 0.390625F);
        setBlinkSpeed(0.2F);
        setIsRainbow(false);
        setLinkBlocks(false);
        setShowHidden(false);
        saveConfig();
    }

    public static boolean isLinkBlocks() {
        return linkBlocks;
    }

    public static void setLinkBlocks(boolean linkBlocks) {
        CSBConfig.linkBlocks = linkBlocks;
    }

    public static boolean isShowHidden() {
        return showHidden;
    }

    public static void setShowHidden(boolean showHidden) {
        CSBConfig.showHidden = showHidden;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        CSBConfig.enabled = enabled;
    }

    public static boolean isEntityEnabled() {
        return entityEnabled;
    }

    public static void setEntityEnabled(boolean entityEnabled) {
        CSBConfig.entityEnabled = entityEnabled;
    }

    public static float getRed() {
        return MathHelper.clamp(red, 0.0F, 1.0F);
    }

    public static void setRed(float r) {
        red = MathHelper.clamp(r, 0.0F, 1.0F);
    }

    public static float getGreen() {
        return MathHelper.clamp(green, 0.0F, 1.0F);
    }

    public static void setGreen(float g) {
        green = MathHelper.clamp(g, 0.0F, 1.0F);
    }

    public static float getBlue() {
        return MathHelper.clamp(blue, 0.0F, 1.0F);
    }

    public static void setBlue(float b) {
        blue = MathHelper.clamp(b, 0.0F, 1.0F);
    }

    public static float getAlpha() {
        return MathHelper.clamp(alpha, 0.0F, 1.0F);
    }

    public static void setAlpha(float a) {
        alpha = MathHelper.clamp(a, 0.0F, 1.0F);
    }

    public static float getThickness() {
        return MathHelper.clamp(thickness, 0.1F, 7.0F);
    }

    public static void setThickness(float t) {
        thickness = MathHelper.clamp(t, 0.1F, 7.0F);
    }

    public static float getBlinkAlpha() {
        return MathHelper.clamp(blinkAlpha, 0.0F, 1.0F);
    }

    public static void setBlinkAlpha(float ba) {
        blinkAlpha = MathHelper.clamp(ba, 0.0F, 1.0F);
    }

    public static float getBlinkSpeed() {
        return MathHelper.clamp(blinkSpeed, 0.0F, 1.0F);
    }

    public static void setBlinkSpeed(float s) {
        blinkSpeed = MathHelper.clamp(s, 0.0F, 1.0F);
    }

    public static void setIsRainbow(boolean b) {
        rainbow = b;
    }

    public static boolean isRainbow() {
        return rainbow;
    }

    public static void setBreakAnimation(BreakAnimation ba) {
        breakAnimation = ba;
    }

    public static BreakAnimation getBreakAnimation() {
        return breakAnimation;
    }

    public enum BreakAnimation {
        NONE,
        SHRINK,
        DOWN,
        ALPHA,
    }

    @Override
    public void onInitializeClient() {
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
