package com.example.newstg.consts;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.example.newstg.R;
import com.example.newstg.obj.Color;

import java.util.Arrays;
import java.util.List;

public class ColorCons {
    public static Color sunsetGlow(Context ctx) {
        return new Color("Sunset Glow", ContextCompat.getColor(ctx, R.color.sunset_glow));
    }

    public static Color harvestMoon(Context ctx) {
        return new Color("Harvest Moon", ContextCompat.getColor(ctx, R.color.harvest_moon));
    }

    public static Color morningSun(Context ctx) {
        return new Color("Morning Sun", ContextCompat.getColor(ctx, R.color.morning_sun));
    }

    public static Color springDew(Context ctx) {
        return new Color("Spring Dew", ContextCompat.getColor(ctx, R.color.spring_dew));
    }

    public static Color forestMist(Context ctx) {
        return new Color("Forest Mist", ContextCompat.getColor(ctx, R.color.forest_mist));
    }

    public static Color gentleTide(Context ctx) {
        return new Color("Gentle Tide", ContextCompat.getColor(ctx, R.color.gentle_tide));
    }

    public static Color clearSky(Context ctx) {
        return new Color("Clear Sky", ContextCompat.getColor(ctx, R.color.clear_sky));
    }

    public static Color winterMorning(Context ctx) {
        return new Color("Winter Morning", ContextCompat.getColor(ctx, R.color.winter_morning));
    }

    public static Color twilightBlue(Context ctx) {
        return new Color("Twilight Blue", ContextCompat.getColor(ctx, R.color.twilight_blue));
    }

    public static Color duskLavender(Context ctx) {
        return new Color("Dusk Lavender", ContextCompat.getColor(ctx, R.color.dusk_lavender));
    }

    public static Color auroraPink(Context ctx) {
        return new Color("Aurora Pink", ContextCompat.getColor(ctx, R.color.aurora_pink));
    }

    public static Color cherryBlossom(Context ctx) {
        return new Color("Cherry Blossom", ContextCompat.getColor(ctx, R.color.cherry_blossom));
    }
    public static List<Color> getAllColors(Context ctx) {
        return Arrays.asList(
                sunsetGlow(ctx),
                harvestMoon(ctx),
                morningSun(ctx),
                springDew(ctx),
                forestMist(ctx),
                gentleTide(ctx),
                clearSky(ctx),
                winterMorning(ctx),
                twilightBlue(ctx),
                duskLavender(ctx),
                auroraPink(ctx),
                cherryBlossom(ctx)
        );
    }
}