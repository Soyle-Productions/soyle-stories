package com.soyle.stories.project

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.common.bindImmutableMap
import com.soyle.stories.di.resolveLater
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.LayoutViewModel
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.toProperty

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:38 PM
 */
class WorkBenchModel : Model<ProjectScope, LayoutViewModel>(ProjectScope::class) {

    override val scope: ProjectScope = super.scope as ProjectScope

    val isOpen = scope.isRegistered

    val loadingProgress = SimpleDoubleProperty(0.0)
    val loadingMessage = SimpleStringProperty("")
    val projectViewModel = SimpleObjectProperty<ProjectFileViewModel?>(scope.projectViewModel)
    val isValidLayout = bind(LayoutViewModel::isValid)

    val primaryWindow = bind { item?.primaryWindow } as SimpleObjectProperty
    val staticTools = bind(LayoutViewModel::staticTools)

    val openDialogs = bind(LayoutViewModel::openDialogs)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

    companion object {
        const val MAX_LOADING_VALUE = 1.0
    }

}