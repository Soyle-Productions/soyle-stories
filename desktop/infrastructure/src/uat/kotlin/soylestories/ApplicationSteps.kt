package com.soyle.stories.soylestories

object ApplicationSteps {

	fun setApplicationStarted(double: SoyleStoriesTestDouble) {
		whenApplicationIsStarted(double)
	}

	fun getStartedApplication(double: SoyleStoriesTestDouble): SoyleStories? {
		return if (double.isStarted()) double.application
		else null
	}

	fun applicationIsStarted(double: SoyleStoriesTestDouble) = getStartedApplication(double) != null

	fun whenApplicationIsStarted(double: SoyleStoriesTestDouble) {
		double.start()
	}

	fun givenApplicationHasBeenStarted(double: SoyleStoriesTestDouble) {
		if (! applicationIsStarted(double)) {
			setApplicationStarted(double)
		}
		assert(applicationIsStarted(double))
	}

}