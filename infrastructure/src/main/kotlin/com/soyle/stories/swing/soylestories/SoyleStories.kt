package com.soyle.stories.swing.soylestories

import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewModel
import java.awt.EventQueue

fun main(args: Array<String>) {
	EventQueue.invokeLater { createAndShowGUI(args.toList()) }
}

fun createAndShowGUI(args: List<String>) {
	SplashScreen()
	// initialize other stuff
	SoyleStories().start(args)
}

/*

View 		- UI 			- Colors, fonts, size, layout, effects, animations
ViewModel 	- UX 			- What components are available on a given submodule (page or subcomponent)
Controller 	- Functionality - How components trigger other parts of the system (event listeners to use cases)
Presenter 	- Feedback 		- How does the UX respond to triggers



View : IView<VM> {
	update(vm)
}

ViewModelImpl : ViewModel {

}


 */

class SoyleStories(
  // applicationViewListener
) : ProjectListView {

	private var viewModel: ProjectListViewModel? = null

	fun start(args: List<String>) {
		// applicationViewListener.startApplicationWithArguments(args)

		WelcomeScreen()

	}

	override fun updateOrInvalidated(update: ProjectListViewModel.() -> ProjectListViewModel) {
		viewModel = viewModel?.update() ?: return invalidate()
	}

	override fun update(update: ProjectListViewModel?.() -> ProjectListViewModel) {
		viewModel = viewModel.update()
	}

	private fun invalidate() {

	}
}
/*
class SoyleStoriesViewModel(

)
class SoyleStoriesPresenter() {

}
 */