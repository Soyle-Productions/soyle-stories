package com.soyle.stories.common.components.dataDisplay.decorator

import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import tornadofx.Decorator
import tornadofx.decorators
import tornadofx.objectProperty

private val Node.singleDecoratorProperty: ObjectProperty<Decorator?>
    get() = properties.getOrPut("com.soyle.stories.components.dataDisplay.decorator") {
        object : SimpleObjectProperty<Decorator?>(decorators.singleOrNull()) {
            override fun set(newValue: Decorator?) {
                decorators.clear()
                if (newValue != null) decorators.add(newValue)
                super.set(newValue)
            }
        }
    } as ObjectProperty<Decorator?>

fun Node.singleDecorator(): ObjectProperty<Decorator?> = singleDecoratorProperty
var Node.singleDecorator: Decorator?
    get() = singleDecoratorProperty.get()
    set(value) { singleDecoratorProperty.set(value) }