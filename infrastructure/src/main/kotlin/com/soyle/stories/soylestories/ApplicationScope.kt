package com.soyle.stories.soylestories

import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import javafx.beans.property.ReadOnlySetProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import tornadofx.Scope
import tornadofx.find
import tornadofx.onChange
import tornadofx.toObservable
import java.util.*
import kotlin.coroutines.CoroutineContext

class ApplicationScope : Scope(), CoroutineScope {

	private val job by lazy { SupervisorJob() }
	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Default + job

	private val _projectScopes = mutableMapOf<UUID, ProjectScope>()

	val projectScopesProperty: ReadOnlySetProperty<ProjectScope> = SimpleSetProperty(FXCollections.observableSet())
	var projectScopes: Set<ProjectScope>
		get() = projectScopesProperty.get()
		private set(value) {
			(projectScopesProperty as SimpleSetProperty)
			  .set(value.toObservable())
		}

	init {
		val applicationModel = find<ApplicationModel>(scope = this)
		applicationModel.openProjects.onChange { list: ObservableList<ProjectFileViewModel>? ->
			val openProjects = list ?: FXCollections.emptyObservableList()
			createScopesForEachProject(openProjects)
			removeScopesOfProjectsNotInList(openProjects)
			projectScopes = _projectScopes.values.toMutableSet()
		}
	}

	private fun createScopesForEachProject(openProjects: List<ProjectFileViewModel>) {
		openProjects.forEach {
			_projectScopes.getOrPut(it.projectId) { ProjectScope(this, it) }
		}
	}

	private fun removeScopesOfProjectsNotInList(openProjects: List<ProjectFileViewModel>) {
		val projectIds = openProjects.map(ProjectFileViewModel::projectId).toSet()
		_projectScopes.entries.removeIf { (_, projectScope) ->
			(projectScope.projectId !in projectIds)
		}
	}

}