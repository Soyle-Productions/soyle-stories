package com.soyle.stories

import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.junit.jupiter.api.Assertions

interface Conditional {
	fun check(double: SoyleStoriesTestDouble): Boolean
}
interface ReadOnlyDependentProperty<T : Any> : Conditional {
	fun get(double: SoyleStoriesTestDouble): T?
	override fun check(double: SoyleStoriesTestDouble): Boolean = get(double) != null
}

interface DependentProperty<T : Any> : ReadOnlyDependentProperty<T> {
	val dependencies: List<(SoyleStoriesTestDouble) -> Unit>
	fun set(double: SoyleStoriesTestDouble) {
		dependencies.forEach { it(double) }
		whenSet(double)
	}
	fun whenSet(double: SoyleStoriesTestDouble)
	fun given(double: SoyleStoriesTestDouble) {
		if (! check(double)) {
			set(double)
		}
		Assertions.assertTrue(check(double))
	}
}