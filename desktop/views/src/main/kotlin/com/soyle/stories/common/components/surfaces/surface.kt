package com.soyle.stories.common.components.surfaces

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.doNothing
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.addChildIfPossible
import tornadofx.addClass
import tornadofx.objectProperty
import tornadofx.removeClass
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

private const val ELEVATION_PROP = "com.soyle.stories.common.components.surfaces.ELEVATION_PROP"
private const val ELEVATION_VARIANT_PROP = "com.soyle.stories.common.components.surfaces.ELEVATION_VARIANT_PROP"
private const val SURFACE_PROP = "com.soyle.stories.common.components.surfaces.SURFACE_PROP"

sealed class ElevationVariant

/**
 * surface should appear with a drop shadow to represent its elevation
 *
 * @param relativeLift if no [relativeLift] is provided, the drop shadow to represent the full elevation will be used.
 *      Otherwise, the drop shadow will appear based on the provided relativeLift
 */
class elevated(
    relativeLift: ObservableValue<Elevation?>? = null
) : ElevationVariant() {
    private val relativeLiftProp: ObservableValue<Elevation?> by lazy { relativeLift ?: objectProperty(null) }
    fun relativeLiftProperty(): ObservableValue<Elevation?> = relativeLiftProp
    val relativeLift: Elevation?
        get() = relativeLiftProp.value
}
object outlined : ElevationVariant()
object none : ElevationVariant()

private val Node.elevationVariantProp: ObjectProperty<ElevationVariant>
    get() = properties.getOrPut(ELEVATION_VARIANT_PROP) { initializeElevationVariantProp(this) } as ObjectProperty<ElevationVariant>

fun Node.elevationVariantProperty(): ObjectProperty<ElevationVariant> = elevationVariantProp

var Node.elevationVariant: ElevationVariant
    get() = elevationVariantProperty().get()
    set(value) = elevationVariantProperty().set(value)

private fun initializeElevationVariantProp(node: Node): ObjectProperty<ElevationVariant> {
    return object : SimpleObjectProperty<ElevationVariant>(node, "elevationVariant", elevated(null)) {
        private var oldValue: ElevationVariant = none
        private var listAmountBinding: Binding<Elevation>? = null
        override fun invalidated() {
            @Suppress("NAME_SHADOWING")
            val node = bean as Node
            val newValue = get()
            val oldValue = this.oldValue

            when(oldValue) {
                none -> doNothing()
                outlined -> {
                    node.removeClass(SurfaceStyles.relativeElevation[0])
                }
                is elevated -> {
                    val liftAmount = oldValue.relativeLift ?: node.elevation ?: Elevation.getValue(0)
                    node.removeClass(SurfaceStyles.relativeElevation[liftAmount.value])
                }
            }

            this.listAmountBinding = null

            when (newValue) {
                none -> {
                    if (! node.pickOnBoundsProperty().isBound) node.isPickOnBounds = false
                }
                outlined -> {
                    if (! node.pickOnBoundsProperty().isBound) node.isPickOnBounds = false
                    node.addClass(SurfaceStyles.relativeElevation[0])
                }
                is elevated -> {
                    val liftAmountBinding: Binding<Elevation> = createObjectBinding({
                        newValue.relativeLift ?: node.elevation ?: Elevation.getValue(0)
                    }, newValue.relativeLiftProperty(), node.elevationProperty())

                    liftAmountBinding.addListener { observable, oldLiftAmount, newLiftAmount ->
                        if (this.listAmountBinding != liftAmountBinding) return@addListener
                        oldLiftAmount!!
                        newLiftAmount!!
                        if (! node.pickOnBoundsProperty().isBound) node.isPickOnBounds = newLiftAmount.value > 0
                        node.removeClass(SurfaceStyles.relativeElevation[oldLiftAmount.value])
                        node.addClass(SurfaceStyles.relativeElevation[newLiftAmount.value])
                    }
                    this.listAmountBinding = liftAmountBinding
                }
            }

            this.oldValue = newValue
        }
    }
}

private val Node.elevationProp: ObjectProperty<Elevation?>
    get() = properties.getOrPut(ELEVATION_PROP) { initializeElevationProp(this) } as ObjectProperty<Elevation?>

fun Node.elevationProperty(): ObjectProperty<Elevation?> = elevationProp

var Node.elevation: Elevation?
    get() = elevationProperty().get()
    set(value) = elevationProperty().set(value)

private fun initializeElevationProp(node: Node): ObjectProperty<Elevation?> {

    return object : SimpleObjectProperty<Elevation?>(node, "elevation", null) {
        private var oldValue: Elevation? = null
        override fun invalidated() {
            @Suppress("NAME_SHADOWING")
            val node = bean as Node
            val newValue = get()

            oldValue?.let { oldValue ->
                node.removeClass(SurfaceStyles.elevated[oldValue.value])
            }

            if (newValue == null) {
                if (! node.viewOrderProperty().isBound) node.viewOrder = 0.0
            }

            if (newValue != null) {
                if (! node.viewOrderProperty().isBound) node.viewOrder = (25.0 - newValue.value).coerceAtLeast(0.0)
                node.addClass(SurfaceStyles.elevated[newValue.value])
            }

            node.elevationVariant // initialize if it hasn't already been
            oldValue = newValue
        }
    }

}


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

@ViewBuilder
fun EventTarget.surface(
    elevation: Elevation? = null,
    configure: VBox.() -> Unit = Node::applyNothing,
    createChildren: VBox.() -> Unit = {}
): VBox = surface(VBox::class, elevation, configure, createChildren)

@ViewBuilder
inline fun <reified N : Parent> EventTarget.surface(
    component: KClass<N> = N::class,
    elevation: Elevation? = null,
    configure: N.() -> Unit = Node::applyNothing,
    createChildren: N.() -> Unit = {}
): N {
    val surface = component.createInstance()
    if (elevation != null) surface.elevation = elevation
    surface.configure()
    surface.createChildren()
    addChildIfPossible(surface)
    return surface
}

@ViewBuilder
inline fun <reified N : Node> EventTarget.surface(
    component: KClass<N> = N::class,
    elevation: Elevation? = null,
    configure: N.() -> Unit = Node::applyNothing
): N {
    val surface = component.createInstance()
    if (elevation != null) surface.elevation = elevation
    addChildIfPossible(surface)
    surface.configure()
    return surface
}

@JvmInline
value class SurfaceBuilder(private val node: Node) {
    var absoluteElevation: Elevation?
        get() = node.elevation
        set(value) { node.elevation = value }

    var relativeElevation: Elevation?
        get() = (node.elevationVariant as? elevated)?.relativeLift
        set(value) {
            if (value == null) node.elevationVariant = elevated(null)
            else node.elevationVariant = elevated(objectProperty(value))
        }
}

inline fun <T : Node> T.asSurface(block: SurfaceBuilder.() -> Unit) = SurfaceBuilder(this).apply(block)

/*
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

const val DEBUG_SURFACE = false

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
             if (DEBUG_SURFACE) println("inherited elevation of $node set to ${get()}")
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
                 if (DEBUG_SURFACE) println("absolute elevation of $node set to ${get()}")
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
                 if (DEBUG_SURFACE) println("relative elevation of $node set to ${get()}")
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
         if (DEBUG_SURFACE) println("initializing $this for $node")
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
*/
