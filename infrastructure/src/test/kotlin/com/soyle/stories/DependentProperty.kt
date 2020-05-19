package com.soyle.stories

import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.junit.jupiter.api.Assertions

interface ReadOnlyDependentProperty<T : Any> {
	fun get(double: SoyleStoriesTestDouble): T?
	fun check(double: SoyleStoriesTestDouble): Boolean = get(double) != null
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