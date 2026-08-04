package com.dylphiiee.piecalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dylphiiee.piecalculator.ui.theme.NeumorphicStyle
import com.dylphiiee.piecalculator.ui.theme.OrangeAccent
import com.dylphiiee.piecalculator.ui.theme.SurfaceDark
import com.dylphiiee.piecalculator.ui.theme.TextPrimary
import com.dylphiiee.piecalculator.ui.theme.neumorphic

/** Panel neumorphic generik untuk pembungkus konten (display, kartu hasil, dll) */
@Composable
fun NeumorphicPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 20,
    style: NeumorphicStyle = NeumorphicStyle.PRESSED,
    backgroundColor: Color = SurfaceDark,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .neumorphic(
                shape = RoundedCornerShape(cornerRadius.dp),
                style = style,
                backgroundColor = backgroundColor
            )
            .background(backgroundColor, RoundedCornerShape(cornerRadius.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

/** Tombol kalkulator neumorphic dengan efek "ditekan" saat disentuh */
@Composable
fun NeumorphicButton(
    label: String,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = if (isAccent) OrangeAccent else SurfaceDark
    val textColor = if (isAccent) Color.Black else TextPrimary
    val style = if (isPressed) NeumorphicStyle.PRESSED else NeumorphicStyle.RAISED

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
            .neumorphic(
                shape = RoundedCornerShape(18.dp),
                style = style,
                backgroundColor = bgColor
            )
            .background(bgColor, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = textColor, fontSize = 20.sp)
    }
}
