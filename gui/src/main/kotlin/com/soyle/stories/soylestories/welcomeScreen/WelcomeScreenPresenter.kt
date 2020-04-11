package com.soyle.stories.soylestories.welcomeScreen

class WelcomeScreenPresenter(
  private val view: WelcomeScreenView
) {

	fun displayWelcomeScreen() {
		view.update {
			WelcomeScreenViewModel(
			  "Welcome to Soyle Stories",
			  "Soyle Stories",
			  "Create New Project",
			  "Open Project"
			)
		}
	}

}