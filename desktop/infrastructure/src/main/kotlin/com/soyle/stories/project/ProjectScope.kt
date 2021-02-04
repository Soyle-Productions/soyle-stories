package com.soyle.stories.project

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.scene.trackSymbolInScene.SynchronizeTrackedSymbolsWithProseController
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlySetProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import tornadofx.FX
import tornadofx.Scope
import tornadofx.toObservable

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:06 PM
 */
class ProjectScope(val applicationScope: ApplicationScope, val projectViewModel: ProjectFileViewModel) : Scope() {

    val projectId = projectViewModel.projectId

    private val finalized = lazy {  }
    val isRegistered: ReadOnlyBooleanProperty = SimpleBooleanProperty(true)

    private val _toolScopes = mutableMapOf<String, Scope>()

    val toolScopesProperty: ReadOnlySetProperty<Scope> = SimpleSetProperty(FXCollections.observableSet())
    var toolScopes: Set<Scope>
        get() = toolScopesProperty.get()
        private set(value) {
            (toolScopesProperty as SimpleSetProperty)
              .set(value.toObservable())
        }

    init {
        applicationScope.projectScopesProperty.onChangeUntil({ finalized.isInitialized() }) {
            if (it?.contains(this) != true) {
                deregister()
                DI.deregister(this)
                (isRegistered as SimpleBooleanProperty).set(false)
                finalized.value
            }
        }
        FX.getComponents(this)
        DI.getRegisteredTypes(this)
        get<SynchronizeTrackedSymbolsWithProseController>()
    }

    fun addScope(toolId: String, scope: Scope) {
        if (! _toolScopes.containsKey(toolId)) {
            _toolScopes[toolId] = scope
            toolScopes = _toolScopes.values.toSet()
        }
    }
    fun removeScope(toolId: String, scope: Scope) {
        if (_toolScopes.containsKey(toolId)) {
            _toolScopes.remove(toolId)
            toolScopes = _toolScopes.values.toSet()
        }
    }

}