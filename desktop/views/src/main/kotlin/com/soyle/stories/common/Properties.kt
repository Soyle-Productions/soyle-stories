package com.soyle.stories.common

import javafx.beans.property.ReadOnlyBooleanProperty
import kotlin.reflect.KProperty

operator fun ReadOnlyBooleanProperty.getValue(ref: Any, property: KProperty<*>): Boolean = value