package com.soyle.stories.project

import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.common.bindImmutableMap
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.LayoutViewModel
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationModel
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:38 PM
 */
class WorkBenchModel : ItemViewModel<LayoutViewModel>(LayoutViewModel()), LayoutView {

    override val scope: ProjectScope = super.scope as ProjectScope

    val isOpen = find<ApplicationModel>(scope = scope.applicationScope).openProjects.select {
        (it.find { it.projectId == scope.projectId } != null).toProperty()
    }

    val loadingProgress = SimpleDoubleProperty(0.0)
    val loadingMessage = SimpleStringProperty("")
    val projectViewModel = SimpleObjectProperty<ProjectFileViewModel?>(scope.projectViewModel)
    val isValidLayout = bind(LayoutViewModel::isValid)

    val primaryWindow = bind { item.primaryWindow?.toProperty() } as SimpleObjectProperty
    val staticTools = bindImmutableList(LayoutViewModel::staticTools)

    val openDialogs = bindImmutableMap(LayoutViewModel::openDialogs)

    private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

    override fun update(update: LayoutViewModel.() -> LayoutViewModel) {
        threadTransformer.gui {
            this@WorkBenchModel.rebind { item = item.update() }
        }
    }

    companion object {
        const val MAX_LOADING_VALUE = 1.0
    }

}