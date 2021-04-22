package com.example.jetpackcomposedonut

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

private const val THETA_SPACING = 0.3f
private const val PHI_SPACING = 0.1f
private const val CIRCLE_RADIUS_FACTOR = 0.005f
private const val TWO_PI = 6.28f
private const val R1 = 1 // circle radius
private const val R2 = 2 // center point
private const val K1 = 628 // scale
private const val K2 = 5 // donut <-> viewer distance

private const val ANIM_DURATION_IN_MILLIS = 10000

@Composable
fun RotatingDonut(modifier: Modifier) {
    val animatedProgressX = remember { Animatable(0f) }
    val animatedProgressZ = remember { Animatable(0f) }

    LaunchedEffect(animatedProgressX){
        animatedProgressX.animateTo(
            targetValue = TWO_PI,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = ANIM_DURATION_IN_MILLIS,
                    easing = LinearEasing
                )
            )
        )
    }

    LaunchedEffect(animatedProgressZ){
        animatedProgressZ.animateTo(
            targetValue = TWO_PI,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = ANIM_DURATION_IN_MILLIS,
                    easing = LinearEasing
                )
            )
        )
    }

    Canvas(modifier = modifier, onDraw = {
        val cosA = cos(animatedProgressX.value)
        val cosB = cos(animatedProgressZ.value)
        val sinA = sin(animatedProgressX.value)
        val sinB = sin(animatedProgressZ.value)

        var theta = 0f
        while (theta < TWO_PI) {
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)

            var phi = 0f
            while (phi < TWO_PI) {
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)

                // x,y = (R2, 0, 0) + (R1 cos theta, R1 sin theta, 0)
                val circleX = R2 + R1 * cosTheta
                val circleY = R1 * sinTheta

                // 3D coordinates
                val x = circleX * (cosB * cosPhi + sinA * sinB * sinPhi) - circleY * cosA * sinB
                val y = circleX * (sinB * cosPhi - sinA * cosB * sinPhi) + circleY * cosA * cosB

                val oneOverZ = 1 / (K2 + cosA * circleX * sinPhi + sinA * circleY)

                val projectedX = (K1 * oneOverZ * x)
                val projectedY = (K1 * oneOverZ * y)

                val green = Color(0xFF78B899)

                drawCircle(
                    green,
                    center = Offset(center.x + projectedX, center.y - projectedY),
                    radius = size.minDimension * CIRCLE_RADIUS_FACTOR
                )
                phi += PHI_SPACING
            }
            theta += THETA_SPACING
        }
    })
}
