package com.example.cyberdefender.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.cyberdefender.ui.theme.CrtScanlineColor
import com.example.cyberdefender.ui.theme.CrtVignetteColor

/**
 * 画面全体にブラウン管（CRT）の走査線と四隅のシャドウ（ビネット）を被せるオーバーレイ
 */
@Composable
fun CrtMonitorFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // メインコンテンツ
        content()

        // CRT走査線 & ビネットオーバーレイ
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 4f // 走査線のピッチ（低解像度感）
            val height = size.height
            val width = size.width

            // 水平スキャンライン描画
            var y = 0f
            while (y < height) {
                drawLine(
                    color = CrtScanlineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.5f
                )
                y += step
            }

            // 四隅のビネット（ブラウン管の丸み・ガラス管の影）
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        CrtVignetteColor.copy(alpha = 0.15f),
                        CrtVignetteColor.copy(alpha = 0.45f)
                    ),
                    center = Offset(width / 2f, height / 2f),
                    radius = (width.coerceAtLeast(height)) * 0.75f
                )
            )
        }
    }
}
