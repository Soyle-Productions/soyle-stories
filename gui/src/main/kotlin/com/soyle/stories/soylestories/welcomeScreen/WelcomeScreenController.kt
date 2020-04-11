package com.soyle.stories.soylestories.welcomeScreen

class WelcomeScreenController(
  private val welcomeScreenPresenter: WelcomeScreenPresenter
) : WelcomeScreenViewListener {
	override fun initializeWelcomeScreen() {
		welcomeScreenPresenter.displayWelcomeScreen()
	}
}