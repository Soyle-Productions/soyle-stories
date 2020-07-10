package com.soyle.stories.theme.valueOppositionWebs

import com.soyle.stories.gui.View
import com.soyle.stories.theme.OppositionValueNameCannotBeBlank
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.ListOppositionsInValueWeb
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.OppositionsInValueWeb
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ValueWebList
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.OppositionRemovedFromValueWeb
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.ValueWebRemovedFromTheme
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.theme.usecases.renameOppositionValue.RenamedOppositionValue
import com.soyle.stories.theme.usecases.renameValueWeb.RenameValueWeb
import com.soyle.stories.theme.usecases.renameValueWeb.RenamedValueWeb
import java.util.*

class ValueOppositionWebsPresenter(
    themeId: String,
    private val view: View.Nullable<ValueOppositionWebsViewModel>
) : ListValueWebsInTheme.OutputPort, AddValueWebToTheme.OutputPort, ListOppositionsInValueWeb.OutputPort,
    AddOppositionToValueWeb.OutputPort,
    RenameOppositionValue.OutputPort,
    RenameValueWeb.OutputPort,
    RemoveValueWebFromTheme.OutputPort,
    RemoveOppositionFromValueWeb.OutputPort,
    AddSymbolicItemToOpposition.OutputPort {

    private val themeId = UUID.fromString(themeId)

    override suspend fun valueWebsListedInTheme(response: ValueWebList) {
        view.update {
            ValueOppositionWebsViewModel(
                response.valueWebs.map {
                    ValueWebItemViewModel(it.valueWebId.toString(), it.valueWebName)
                },
                selectedValueWeb = null,
                oppositionValues = listOf(),
                errorMessage = null,
                errorSource = null
            )
        }
    }

    override suspend fun addedValueWebToTheme(response: ValueWebAddedToTheme) {
        if (themeId != response.themeId) return
        view.updateOrInvalidated {
            copy(
                valueWebs = valueWebs + ValueWebItemViewModel(response.valueWebId.toString(), response.valueWebName)
            )
        }
    }

    override suspend fun valueWebRenamed(response: RenamedValueWeb) {
        if (themeId != response.themeId) return
        val valueWebId = response.valueWebId.toString()
        view.updateOrInvalidated {
            copy(
                valueWebs = valueWebs.map {
                    if (it.valueWebId == valueWebId) {
                        ValueWebItemViewModel(it.valueWebId, response.newName)
                    } else it
                },
                selectedValueWeb = if (selectedValueWeb?.valueWebId == valueWebId) {
                    ValueWebItemViewModel(valueWebId, response.newName)
                } else selectedValueWeb
            )
        }
    }

    override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
        if (themeId != response.themeId) return
        val valueWebId = response.valueWebId.toString()
        view.updateOrInvalidated {
            copy(
                valueWebs = valueWebs.filterNot { it.valueWebId == valueWebId },
                selectedValueWeb = if (selectedValueWeb?.valueWebId == valueWebId) null else selectedValueWeb
            )
        }
    }

    override suspend fun oppositionsListedInValueWeb(response: OppositionsInValueWeb) {
        view.updateOrInvalidated {
            copy(
                oppositionValues = response.oppositions.map {
                    OppositionValueViewModel(it.oppositionValueId.toString(), it.oppositionValueName, false, it.symbols.map {
                        SymbolicItemViewModel(it.symbolicId.toString(), it.name)
                    })
                }
            )
        }
    }

    override suspend fun addedOppositionToValueWeb(response: OppositionAddedToValueWeb) {
        if (themeId != response.themeId) return
        view.updateOrInvalidated {
            if (selectedValueWeb == null || selectedValueWeb.valueWebId != response.valueWebId.toString()) {
                return@updateOrInvalidated this
            }
            copy(
                oppositionValues = oppositionValues + OppositionValueViewModel(
                    response.oppositionValueId.toString(),
                    response.oppositionValueName,
                    true,
                    listOf()
                )
            )
        }
    }

    override suspend fun oppositionValueRenamed(response: RenamedOppositionValue) {
        if (themeId != response.themeId) return
        val oppositionValueId = response.oppositionValueId.toString()
        view.updateOrInvalidated {
            if (selectedValueWeb == null || selectedValueWeb.valueWebId != response.valueWebId.toString()) {
                return@updateOrInvalidated this
            }
            copy(
                oppositionValues = oppositionValues.map {
                    if (it.oppositionValueId != oppositionValueId) it
                    else it.copy(
                        oppositionValueName = response.oppositionValueName,
                        isNew = false
                    )
                }
            )
        }
    }

    override suspend fun removedOppositionFromValueWeb(response: OppositionRemovedFromValueWeb) {
        if (themeId != response.themeId) return
        val oppositionValueId = response.oppositionValueId.toString()
        view.updateOrInvalidated {
            if (selectedValueWeb == null || selectedValueWeb.valueWebId != response.valueWebId.toString()) {
                return@updateOrInvalidated this
            }
            copy(
                oppositionValues = oppositionValues.filterNot {
                    it.oppositionValueId == oppositionValueId
                }
            )
        }
    }

    override suspend fun addedSymbolicItemToOpposition(response: SymbolicRepresentationAddedToOpposition) {
        if (themeId != response.themeId) return
        val oppositionId = response.oppositionId.toString()
        view.updateOrInvalidated {
            if (selectedValueWeb == null || selectedValueWeb.valueWebId != response.valueWebId.toString()) {
                return@updateOrInvalidated this
            }
            copy(
                oppositionValues = oppositionValues.map {
                    if (it.oppositionValueId == oppositionId) it.copy(
                        symbolicItems = it.symbolicItems + SymbolicItemViewModel(response.itemId().toString(), response.itemName)
                    )
                    else it
                }
            )
        }
    }

    internal fun presentError(entityId: String, error: Throwable) {
        view.updateOrInvalidated {
            copy(
                errorSource = entityId,
                errorMessage = error.localizedMessage?.takeIf { it.isNotBlank() }
                    ?: "Something went wrong: ${error::class.simpleName ?: error.toString()}"
            )
        }
    }

}