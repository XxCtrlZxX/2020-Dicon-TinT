package com.sunrin.tint.Screen.Show;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomAnimation implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) {    // (-Infinity, -1)
            // This page is way off-screen to the left.
            page.setAlpha(0);
        } else if (position <= 1) { // [-1, 1]
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;

            if (position < 0)
                page.setTranslationX(horizontalMargin - verticalMargin / 2);
            else
                page.setTranslationX(-horizontalMargin - verticalMargin / 2);

            // Scale the page down
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else {    // (1, Infinity)
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }
}