package com.example.greenlens.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ObjectDetectionOverlay extends View {
    private Paint overlayPaint;
    private Paint transparentPaint;
    private Paint cornerPaint;
    private RectF objectRect = null;
    private static final float CORNER_LENGTH = 40f; // 코너 선의 길이
    private static final float STROKE_WIDTH = 4f;   // 선의 두께

    public ObjectDetectionOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 반투명 검은색 배경
        overlayPaint = new Paint();
        overlayPaint.setColor(Color.BLACK);
        overlayPaint.setAlpha(128);

        // 투명 영역
        transparentPaint = new Paint();
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // 코너 표시용 페인트
        cornerPaint = new Paint();
        cornerPaint.setColor(Color.WHITE);
        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeWidth(STROKE_WIDTH);
    }

    public void setObjectRect(RectF rect) {
        this.objectRect = rect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (objectRect != null) {
            // 새로운 레이어 생성
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);

            // 전체 화면을 반투명 검은색으로
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

            // 객체 영역을 투명하게
            canvas.drawRect(objectRect, transparentPaint);

            // 레이어 복원
            canvas.restoreToCount(saveCount);

            // 코너 표시 그리기
            drawCorners(canvas, objectRect);
        }
    }

    private void drawCorners(Canvas canvas, RectF rect) {
        // 좌상단
        canvas.drawLine(rect.left, rect.top, rect.left + CORNER_LENGTH, rect.top, cornerPaint);
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + CORNER_LENGTH, cornerPaint);

        // 우상단
        canvas.drawLine(rect.right - CORNER_LENGTH, rect.top, rect.right, rect.top, cornerPaint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + CORNER_LENGTH, cornerPaint);

        // 좌하단
        canvas.drawLine(rect.left, rect.bottom - CORNER_LENGTH, rect.left, rect.bottom, cornerPaint);
        canvas.drawLine(rect.left, rect.bottom, rect.left + CORNER_LENGTH, rect.bottom, cornerPaint);

        // 우하단
        canvas.drawLine(rect.right - CORNER_LENGTH, rect.bottom, rect.right, rect.bottom, cornerPaint);
        canvas.drawLine(rect.right, rect.bottom - CORNER_LENGTH, rect.right, rect.bottom, cornerPaint);
    }
}