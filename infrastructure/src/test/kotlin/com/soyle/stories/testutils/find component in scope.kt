package com.soyle.stories.testutils

import tornadofx.Component
import tornadofx.FX
import tornadofx.Scope

inline fun <reified T : Component> findComponentsInScope(scope: Scope): List<T> {
	return FX.getComponents(scope).values.filterIsInstance<T>()
}