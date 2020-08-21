package com.soyle.stories.project

import com.soyle.stories.common.ScopedTestDouble
import com.soyle.stories.di.get
import com.soyle.stories.layout.config.dynamic.ValueOppositionWebs
import com.soyle.stories.project.layout.GroupSplitterViewModel
import com.soyle.stories.project.layout.ToolGroupViewModel
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.WindowChildViewModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import javafx.collections.FXCollections
import tornadofx.*

class WorkbenchDouble : ScopedTestDouble<ProjectScope>(ProjectScope::class) {

    private val model = scope.get<WorkBenchModel>()

    init {
        val toolViewModels = FXCollections.observableArrayList<ToolViewModel>()
        model.primaryWindow.onChange {
            toolViewModels.setAll(collectTools(listOfNotNull(it?.child)))
            Unit.toProperty()
        }
        FXCollections.observableArrayList<Scope>().bind(toolViewModels) {
            when (val type = it.type) {
                is ValueOppositionWebs -> ValueOppositionWebsScope(scope, it.toolId, type)
                else -> Scope()
            }
        }
    }

    private tailrec fun collectTools(children: List<WindowChildViewModel>, collected: List<ToolViewModel> = emptyList()): List<ToolViewModel> {
        val child = children.firstOrNull() ?: return collected
        if (child is ToolGroupViewModel) return collectTools(children.drop(1), collected + child.tools)
        child as GroupSplitterViewModel
        return collectTools(children.drop(1) + child.children.map { it.second }, collected)
    }

}