package com.soyle.stories.soylestories

import com.soyle.stories.layout.LayoutComponent
import com.soyle.stories.layout.LayoutTestView
import com.soyle.stories.layout.LayoutViewModelWrapper
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectListViewModel
import java.util.*

class ProjectListViewModelWrapper(
  private val makeLayout: (ProjectFileViewModel) -> Unit
) : ProjectListView {

	private var viewModel: ProjectListViewModel? = null
		set(value) {
			field = value
			openProjects.forEach {
				makeLayout(it)
			}
		}

	val openProjects: List<ProjectFileViewModel>
		get() = viewModel!!.openProjects


	override fun update(update: ProjectListViewModel?.() -> ProjectListViewModel) {
		synchronized(this) {
			viewModel = viewModel.update()
		}
	}

	override fun updateOrInvalidated(update: ProjectListViewModel.() -> ProjectListViewModel) {
		synchronized(this) {
			viewModel = viewModel?.update() ?: return invalidate()
		}
	}

	private fun invalidate() {

	}
}