package com.soyle.stories.common.components.surfaces

import com.soyle.stories.common.ViewBuilder
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.javaConstructor

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

const val SURFACE_ELEVATION_PROPERTY = "surface_elevation"
val Node.surfaceElevation: Int?
    get() = properties[SURFACE_ELEVATION_PROPERTY] as? Int

const val SURFACE_RELATIVE_ELEVATION_PROPERTY = "surface_relative_elevation"
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
    component.apply {
        properties[SURFACE_ELEVATION_PROPERTY] = elevation
        if (elevation >= 0 && elevation <= 24) {
            addClass(SurfaceStyles.elevation[elevation])
            surfaceRelativeElevationProperty().onChange { relativeElevation ->
                (0..24).forEach {
                    togglePseudoClass(SurfaceStyles.relativeElevation[it].name, relativeElevation == it)
                }
            }
            surfaceRelativeElevationProperty().set(elevation)
            viewOrder = 25.0 - elevation
        }
    }
    return component
}