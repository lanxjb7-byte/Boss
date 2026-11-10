package com.lanxx.gamebooster;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager wm;
    private View crosshair;
    private View bubble;
    private WindowManager.LayoutParams crossParams;
    private WindowManager.LayoutParams bubbleParams;
    private static volatile String crossColor = "#FF304F";
    private static volatile int crossSize = 34;
    private static OverlayService activeService;

    public static void setCrosshairStyle(String color, int size) {
        if (color != null && color.matches("#[0-9A-Fa-f]{6}")) crossColor = color;
        crossSize = Math.max(16, Math.min(100, size));
        OverlayService service = activeService;
        if (service != null) service.applyCrosshairStyle();
    }

    private void applyCrosshairStyle() {
        new android.os.Handler(getMainLooper()).post(() -> {
            if (wm != null && crosshair != null && crossParams != null) {
                crossParams.width = crossSize;
                crossParams.height = crossSize;
                try { wm.updateViewLayout(crosshair, crossParams); } catch (Exception ignored) {}
                crosshair.invalidate();
            }
        });
    }

    @Override public void onCreate() {
        super.onCreate();
        activeService = this;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        showCrosshair();
        showBubble();
    }

    private int overlayType() {
        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    private void showCrosshair() {
        crosshair = new CrosshairView(this);
        crossParams = new WindowManager.LayoutParams(
            crossSize, crossSize, overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        crossParams.gravity = Gravity.CENTER;
        crosshair.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startX, startY;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = e.getRawX(); downY = e.getRawY();
                    startX = crossParams.x; startY = crossParams.y;
                    return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    crossParams.x = startX + (int)(e.getRawX() - downX);
                    crossParams.y = startY + (int)(e.getRawY() - downY);
                    wm.updateViewLayout(crosshair, crossParams);
                    return true;
                }
                return true;
            }
        });
        wm.addView(crosshair, crossParams);
    }

    private void showBubble() {
        TextView b = new TextView(this);
        b.setText("L");
        b.setTextColor(Color.WHITE);
        b.setTextSize(20);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(210, 20, 45));
        bg.setCornerRadius(100);
        bg.setStroke(2, Color.WHITE);
        b.setBackground(bg);
        bubble = b;
        bubbleParams = new WindowManager.LayoutParams(
            54, 54, overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        bubbleParams.gravity = Gravity.TOP | Gravity.END;
        bubbleParams.x = 18; bubbleParams.y = 180;
        b.setOnClickListener(v -> togglePanel());
        b.setOnTouchListener(new View.OnTouchListener() {
            float dx, dy; int sx, sy;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    dx=e.getRawX(); dy=e.getRawY(); sx=bubbleParams.x; sy=bubbleParams.y;
                    return false;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    bubbleParams.x = sx - (int)(e.getRawX()-dx);
                    bubbleParams.y = sy + (int)(e.getRawY()-dy);
                    wm.updateViewLayout(bubble, bubbleParams);
                    return true;
                }
                return false;
            }
        });
        wm.addView(bubble, bubbleParams);
    }

    private void togglePanel() {
        if (crosshair.getVisibility() == View.VISIBLE) {
            crosshair.setVisibility(View.GONE);
            ((TextView) bubble).setText("×");
        } else {
            crosshair.setVisibility(View.VISIBLE);
            ((TextView) bubble).setText("L");
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (activeService == this) activeService = null;
        if (wm != null) {
            if (crosshair != null) wm.removeView(crosshair);
            if (bubble != null) wm.removeView(bubble);
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    private static class CrosshairView extends View {
        private final android.graphics.Paint p = new android.graphics.Paint(3);
        CrosshairView(android.content.Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        @Override protected void onDraw(android.graphics.Canvas c) {
            super.onDraw(c);
            float cx=getWidth()/2f, cy=getHeight()/2f;
            float r=Math.min(getWidth(),getHeight())*0.34f;
            p.setColor(Color.parseColor(crossColor));
            p.setStrokeWidth(Math.max(2f, crossSize/18f));
            p.setStyle(android.graphics.Paint.Style.STROKE);
            c.drawCircle(cx, cy, r*0.55f, p);
            p.setStyle(android.graphics.Paint.Style.FILL);
            c.drawCircle(cx, cy, Math.max(2f, r*0.10f), p);
            p.setStrokeWidth(Math.max(2f, crossSize/16f));
            c.drawLine(cx-r,cy,cx-r*0.65f,cy,p);
            c.drawLine(cx+r*0.65f,cy,cx+r,cy,p);
            c.drawLine(cx,cy-r,cx,cy-r*0.65f,p);
            c.drawLine(cx,cy+r*0.65f,cx,cy+r,p);
        }
    }
  }
          
