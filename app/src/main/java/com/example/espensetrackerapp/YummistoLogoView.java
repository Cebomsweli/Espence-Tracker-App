package com.example.espensetrackerapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class YummistoLogoView extends View {
    private Paint bgPaint, textPaint;
    private Path logoPath;
    private RectF rectF;
    private float cornerRadius = 40f;

    public YummistoLogoView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Background paint (green)
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#4CAF50"));
        bgPaint.setStyle(Paint.Style.FILL);

        // Create path with 3 rounded corners
        logoPath = new Path();
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw shape with 3 rounded corners
        logoPath.reset();

        // Start at top-left (rounded)
        logoPath.moveTo(cornerRadius, 0);

        // Top-right (straight)
        logoPath.lineTo(width, 0);

        // Bottom-right (rounded)
        logoPath.lineTo(width, height - cornerRadius);
        rectF.set(width - 2*cornerRadius, height - 2*cornerRadius, width, height);
        logoPath.arcTo(rectF, 0, 90, false);

        // Bottom-left (rounded)
        logoPath.lineTo(cornerRadius, height);
        rectF.set(0, height - 2*cornerRadius, 2*cornerRadius, height);
        logoPath.arcTo(rectF, 90, 90, false);

        // Close path (left side)
        logoPath.close();

        canvas.drawPath(logoPath, bgPaint);

        // Add your logo elements here (text, icons, etc)
    }
}
