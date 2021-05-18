package com.soyle.stories.common.components.surfaces

import com.soyle.stories.common.ViewBuilder
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.layout.VBox
import tornadofx.*
import kotlin.reflect.full.createInstance

@ViewBuilder
inline fun EventTarget.surface(
    elevation: Int = 1,
    op: VBox.() -> Unit = {}
) = surface(VBox(), elevation, op)

@ViewBuilder
inline fun <reified N : Node> EventTarget.surface(
    elevation: Int = 1,
    noinline op: N.() -> Unit = {}
): N = surface(N::class.createInstance(), elevation, op)

@ViewBuilder
inline fun <N : Node> EventTarget.surface(
    component: N,
    elevation: Int = 1,
    op: N.() -> Unit = {}
): N {
    val node = Surface(component = component, elevation = elevation)
    return opcr(this, node, op)
}

inline fun <reified N : Node> Surface(
    elevation: Int = 1
): N = Surface(component = N::class.createInstance(), elevation = elevation)

private const val SURFACE_ELEVATION_PROPERTY = "surface_elevation"
var Node.surfaceElevation: Int
    get() = surfaceElevationProperty().get()
    set(value) = surfaceElevationProperty().set(value)

fun Node.surfaceElevationProperty(): IntegerProperty =
    properties.getOrPut(SURFACE_ELEVATION_PROPERTY) {
        SimpleIntegerProperty(0)
    } as IntegerProperty

private const val SURFACE_RELATIVE_ELEVATION_PROPERTY = "surface_relative_elevation"
var Node.surfaceRelativeElevation: Int?
    get() = surfaceRelativeElevationProperty().get()
    set(value) = surfaceRelativeElevationProperty().set(value ?: 0)

fun Node.surfaceRelativeElevationProperty(): IntegerProperty =
    properties.getOrPut(SURFACE_RELATIVE_ELEVATION_PROPERTY) {
        SimpleIntegerProperty(-1)
    } as IntegerProperty

fun <N : Node> Surface(
    component: N,
    elevation: Int = 1
): N {
    return component.asSurface(elevation)
}

fun <N : Node> N.asSurface(elevation: Int = 1): N {
    surfaceElevationProperty().onChange { absoluteElevation ->
        (0..24).forEach {
            toggleClass(SurfaceStyles.elevated[it], absoluteElevation == it)
        }
    }
    surfaceRelativeElevationProperty().onChange { relativeElevation ->
        (0..24).forEach {
            togglePseudoClass(SurfaceStyles.relativeElevation[it].name, relativeElevation == it)
        }
    }
    if (elevation >= 0 && elevation <= 24) {
        viewOrderProperty().bind(surfaceElevationProperty().doubleBinding { (25.0 - elevation).coerceAtLeast(0.0) })
        surfaceElevation = elevation
        surfaceRelativeElevation = elevation
    }
    return this
}