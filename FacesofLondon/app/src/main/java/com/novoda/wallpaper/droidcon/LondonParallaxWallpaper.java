package com.novoda.wallpaper.droidcon;

import android.content.Intent;
import android.content.IntentFilter;
import android.service.wallpaper.WallpaperService;
import android.util.Log;

import java.io.IOException;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

/**
 * Draws a wallpaper much like the regular wallpapers which slide behind
 * the home screen with a parallax effect. The difference with this wallpaper
 * is that it supports multiple images with different widths, for a much more
 * intricate-looking parallax effect.
 * <p/>
 * The main thing is that it's configurable, so you can use any set of images.
 */
public class LondonParallaxWallpaper extends GLWallpaperService {

    public static final String TAG = "LondonParallaxWallpaper";

    private LondonParallaxWallpaperRenderer renderer;
    private TimeOfDayAssetRefresher refresher;

    @Override
    public void onCreate() {
        super.onCreate();
        refresher = new TimeOfDayAssetRefresher(renderer, new TimeOfDayCalculator());
        registerReceiver(refresher, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new ParallaxEngine();
    }

    class ParallaxEngine extends GLEngine {

        ParallaxEngine() {
            super();
            renderer = new LondonParallaxWallpaperRenderer(LondonParallaxWallpaper.this.getAssets());
            setRenderer(renderer);

            setRenderMode(RENDERMODE_WHEN_DIRTY);

            initLayers();
            requestRender();
        }

        void initLayers() {
            try {
                renderer.reloadLayers();
                renderer.resizeLayers();
            } catch (IOException e) {
                Log.e(TAG, "Error loading textures", e);
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            renderer.setOffset(xOffset);
            requestRender();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refresher);
    }
}