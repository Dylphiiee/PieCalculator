package com.dylphiiee.piecalculator.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NeumorphicStyle { RAISED, PRESSED, FLAT }

/**
 * Modifier neumorphism dua-bayangan (soft UI): satu bayangan gelap di kanan-bawah,
 * satu bayangan terang di kiri-atas, membuat elemen terlihat "timbul" dari background.
 *
 * Teknik: shape dikonversi ke Path, lalu digambar berulang via android.graphics.Paint.setShadowLayer,
 * karena Compose belum punya API dual-shadow bawaan (dan Canvas tidak punya drawOutline langsung).
 */
fun Modifier.neumorphic(
    shape: Shape = RoundedCornerShape(20.dp),
    style: NeumorphicStyle = NeumorphicStyle.RAISED,
    elevation: Dp = 10.dp,
    backgroundColor: Color = SurfaceDark,
    lightShadowColor: Color = ShadowLight,
    darkShadowColor: Color = ShadowDark
): Modifier = composed {
    val density = LocalDensity.current

    when (style) {
        NeumorphicStyle.FLAT -> this.drawBehind {
            val outline = shape.createOutline(size, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }
            drawIntoCanvas { canvas ->
                val paint = Paint().apply { color = backgroundColor }
                canvas.drawPath(path, paint)
            }
        }

        NeumorphicStyle.RAISED -> this.drawBehind {
            val shadowPx = with(density) { elevation.toPx() }
            val outline = shape.createOutline(size, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }
            drawIntoCanvas { canvas ->
                val paint = Paint()
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = backgroundColor.toArgb()

                // Bayangan gelap (kanan-bawah) -> memberi kesan kedalaman
                frameworkPaint.setShadowLayer(shadowPx, shadowPx / 1.5f, shadowPx / 1.5f, darkShadowColor.toArgb())
                canvas.drawPath(path, paint)

                // Bayangan terang (kiri-atas) -> memberi kesan cahaya/highlight
                frameworkPaint.setShadowLayer(shadowPx, -shadowPx / 1.5f, -shadowPx / 1.5f, lightShadowColor.toArgb())
                canvas.drawPath(path, paint)

                // Isi solid di atas kedua bayangan agar tidak transparan
                frameworkPaint.clearShadowLayer()
                canvas.drawPath(path, paint)
            }
        }

        NeumorphicStyle.PRESSED -> this.drawBehind {
            val shadowPx = with(density) { (elevation / 2).toPx() }
            val outline = shape.createOutline(size, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }
            drawIntoCanvas { canvas ->
                val paint = Paint()
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.color = backgroundColor.toArgb()
                canvas.drawPath(path, paint)

                // Inner-shadow disimulasikan dengan bayangan tipis terbalik pada tepi
                frameworkPaint.setShadowLayer(shadowPx, -shadowPx / 2, -shadowPx / 2, darkShadowColor.toArgb())
                canvas.drawPath(path, paint)
            }
        }
    }
}
