package com.soyle.stories

import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.junit.jupiter.api.Assertions

interface DependentProperty<T : Any> {
	val dependencies: List<(SoyleStoriesTestDouble) -> Unit>
	fun set(double: SoyleStoriesTestDouble) {
		dependencies.forEach { it(double) }
		whenSet(double)
	}
	fun get(double: SoyleStoriesTestDouble): T?
	fun check(double: SoyleStoriesTestDouble): Boolean = get(double) != null
	fun whenSet(double: SoyleStoriesTestDouble)
	fun given(double: SoyleStoriesTestDouble) {
		if (! check(double)) {
			set(double)
		}
		Assertions.assertTrue(check(double))
	}
}