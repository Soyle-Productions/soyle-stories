package com.soyle.stories.common.components.surfaces

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.common.scopedListener
import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.css.*
import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

private const val ELEVATION_PROP = "com.soyle.stories.common.components.surfaces.ELEVATION_PROP"
private const val SURFACE_PROP = "com.soyle.stories.common.components.surfaces.SURFACE_PROP"

@JvmInline
value class Elevation private constructor(val value: Int) {

    companion object {

        operator fun get(value: Int): Elevation? =
            if (value in 0..24) Elevation(value) else null

        fun getValue(value: Int): Elevation = get(value)
            ?: throw IllegalArgumentException("Elevation value must be between 0 and 24.  Received $value")

        val max: Int = 24
        val min: Int = 0
    }
}

interface SurfaceClasses {

    var lightLevelStyle: CssRule?
    var liftedStyle: CssRule?
}

sealed interface Elevated {

    fun inheritedElevationProperty(): ObjectProperty<Elevation>
    var inheritedElevation: Elevation

    fun absoluteElevationProperty(): ObjectProperty<Elevation>
    var absoluteElevation: Elevation

    fun relativeElevationProperty(): ObjectProperty<Elevation>
    var relativeElevation: Elevation
}

fun Node.elevated(): Elevated =
    properties.getOrPut(ELEVATION_PROP) { SurfaceGraph(this) } as Elevated


interface ElevatedSurface : Elevated, SurfaceClasses {

    val lightLevelStyleProperty: ObjectProperty<CssRule?>

    val liftedStyleProperty: ObjectProperty<CssRule?>
}

// var DEBUG_SURFACE = false

private class SurfaceGraph(private val node: Node, private val calculator: ElevationCalculator = Companion) : Elevated {

    abstract class ElevationCalculator {

        abstract fun calculateAbsoluteElevation(inheritedElevation: Elevation, relativeElevation: Elevation): Elevation
        abstract fun calculateRelativeElevation(inheritedElevation: Elevation, absoluteElevation: Elevation): Elevation
    }

    companion object : ElevationCalculator() {

        override fun calculateAbsoluteElevation(
            inheritedElevation: Elevation,
            relativeElevation: Elevation
        ): Elevation = inheritedElevation

        override fun calculateRelativeElevation(
            inheritedElevation: Elevation,
            absoluteElevation: Elevation
        ): Elevation = Elevation.getValue(0)
    }

    private var prefRelativeProperty = true
        get() = field && !absoluteElevationProperty.isBound

    private val inheritedElevationProperty = object : ObjectPropertyBase<Elevation>(Elevation.getValue(0)) {
        override fun getBean(): Any = this@SurfaceGraph
        override fun getName(): String = "inheritedElevation"
        override fun invalidated() {
            // if (DEBUG_SURFACE) println("inherited elevation of $node set to ${get()}")
            if (prefRelativeProperty) {
                absoluteElevationProperty.set(calculator.calculateAbsoluteElevation(get(), relativeElevation))
            } else if (!relativeElevationProperty.isBound) {
                relativeElevationProperty.set(calculator.calculateRelativeElevation(get(), absoluteElevation))
            }
        }
    }

    override fun inheritedElevationProperty(): ObjectProperty<Elevation> = inheritedElevationProperty

    override var inheritedElevation: Elevation by inheritedElevationProperty

    private val absoluteElevationProperty: ObjectProperty<Elevation> =
        object : ObjectPropertyBase<Elevation>(
            Elevation.getValue(1)
        ) {
            override fun getBean(): Any = this@SurfaceGraph
            override fun getName(): String = "absoluteElevation"
            override fun invalidated() {
                // if (DEBUG_SURFACE) println("absolute elevation of $node set to ${get()}")
                if (! prefRelativeProperty && ! relativeElevationProperty.isBound) {
                    relativeElevationProperty.set(calculator.calculateRelativeElevation(inheritedElevation, get()))
                }
            }
        }

    override fun absoluteElevationProperty(): ObjectProperty<Elevation> = absoluteElevationProperty
    override var absoluteElevation: Elevation
        get() = absoluteElevationProperty.get()
        set(value) {
            prefRelativeProperty = false
            absoluteElevationProperty.set(value)
        }

    private val relativeElevationProperty: ObjectProperty<Elevation> =
        object : ObjectPropertyBase<Elevation>(
            Elevation.getValue(1)
        ) {
            override fun getBean(): Any = this@SurfaceGraph
            override fun getName(): String = "relativeElevation"
            override fun invalidated() {
                // if (DEBUG_SURFACE) println("relative elevation of $node set to ${get()}")
                if (prefRelativeProperty && !absoluteElevationProperty.isBound)
                    absoluteElevationProperty.set(calculator.calculateAbsoluteElevation(inheritedElevation, get()))
            }
        }

    override fun relativeElevationProperty(): ObjectProperty<Elevation> = relativeElevationProperty
    override var relativeElevation: Elevation
        get() = relativeElevationProperty.get()
        set(value) {
            prefRelativeProperty = true
            relativeElevationProperty.set(value)
        }

    init {
        // if (DEBUG_SURFACE) println("initializing $this for $node")
        node.properties[ELEVATION_PROP] = this
    }
}

class Surface<N : Node>(val node: N) : ElevatedSurface, Elevated by SurfaceGraph(node, elevationCalculator) {

    constructor(nodeClass: KClass<N>) : this(nodeClass.createInstance())

    companion object {

        val noOverrides: SurfaceClasses.() -> Unit = {}

        inline operator fun <reified N : Node> invoke(): Surface<N> = Surface(N::class)

        fun <N : Node> N.asSurface(configuration: ElevatedSurface.() -> Unit = {}): Surface<N> {
            val surfaceComponent = properties[SURFACE_PROP]
            if (surfaceComponent is Surface<*>) return surfaceComponent.apply(configuration) as Surface<N>
            return Surface(this).apply(configuration)
        }

        @ViewBuilder
        fun EventTarget.surface(
            elevation: Elevation? = null,
            classes: SurfaceClasses.() -> Unit = {},
            createChildren: VBox.() -> Unit = {}
        ): VBox = surface(VBox::class, elevation, classes, createChildren)

        @ViewBuilder
        inline fun <reified N : Parent> EventTarget.surface(
            component: KClass<N> = N::class,
            elevation: Elevation? = null,
            classes: SurfaceClasses.() -> Unit = {},
            createChildren: N.() -> Unit = {}
        ): N {
            val surface = Surface(component.createInstance()).apply(classes)
            if (elevation != null) surface.relativeElevation = elevation
            surface.node.createChildren()
            addChildIfPossible(surface.node)
            return surface.node
        }

        @ViewBuilder
        inline fun <reified N : Node> EventTarget.surface(
            component: KClass<N> = N::class,
            elevation: Elevation? = null,
            classes: SurfaceClasses.() -> Unit = {}
        ): N {
            val surface = Surface(component.createInstance()).apply(classes)
            if (elevation != null) surface.relativeElevation = elevation
            addChildIfPossible(surface.node)
            return surface.node
        }

        private val elevationCalculator = object : SurfaceGraph.ElevationCalculator() {
            override fun calculateAbsoluteElevation(
                inheritedElevation: Elevation,
                relativeElevation: Elevation
            ): Elevation {
                return Elevation.get(inheritedElevation.value + relativeElevation.value)
                    ?: Elevation.getValue(Elevation.max)
            }

            override fun calculateRelativeElevation(
                inheritedElevation: Elevation,
                absoluteElevation: Elevation
            ): Elevation {
                return Elevation.get(absoluteElevation.value - inheritedElevation.value)
                    ?: Elevation.getValue(Elevation.min)
            }
        }

    }

    init {
        node.properties[SURFACE_PROP] = this
    }

    override val liftedStyleProperty: ObjectProperty<CssRule?> = objectProperty(null)
    override var liftedStyle: CssRule? by liftedStyleProperty

    override val lightLevelStyleProperty: ObjectProperty<CssRule?> = objectProperty(null)
    override var lightLevelStyle: CssRule? by lightLevelStyleProperty

    private val lightLevelRuleProperty = absoluteElevationProperty().objectBinding(lightLevelStyleProperty) {
        lightLevelStyle ?: SurfaceStyles.elevated[it?.value ?: 1]
    }
    private val liftedRuleProperty = relativeElevationProperty().objectBinding(liftedStyleProperty) {
        liftedStyle ?: SurfaceStyles.relativeElevation[it?.value ?: 0]
    }

    init {
        node.addClass(SurfaceStyles.surface)
        node.viewOrderProperty()
            .bind(relativeElevationProperty().doubleBinding { (25.0 - (it?.value ?: 1)).coerceAtLeast(0.0) })

        lightLevelRuleProperty.addListener { _, oldValue, newValue ->
            oldValue?.let { node.removeClass(it) }
            newValue?.let { node.addClass(it) }
        }
        lightLevelRuleProperty.value?.let { node.addClass(it) }

        liftedRuleProperty.addListener { _, oldValue, newValue ->
            oldValue?.let { node.removeClass(it) }
            newValue?.let { node.addClass(it) }
        }
        liftedRuleProperty.value?.let { node.addClass(it) }
        node.pickOnBoundsProperty().bind(liftedRuleProperty.booleanBinding { it == SurfaceStyles.elevated[0] })
    }

    operator fun component1() = node

}
