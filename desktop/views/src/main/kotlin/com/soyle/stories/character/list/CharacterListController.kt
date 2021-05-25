package com.soyle.stories.character.list

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacter
import com.soyle.stories.usecase.character.arc.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

class CharacterListController(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val listAllCharacterArcs: ListAllCharacterArcs,
    private val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort,
    private val openToolController: OpenToolController,
    private val removeCharacterFromStoryController: RemoveCharacterFromStoryController,
    private val deleteCharacterArc: DemoteMajorCharacter,
    private val deleteCharacterArcOutputPort: DemoteMajorCharacter.OutputPort,
    private val renameCharacter: RenameCharacter,
    private val renameCharacterOutputPort: RenameCharacter.OutputPort,
    private val renameCharacterArc: RenameCharacterArc,
    private val renameCharacterArcOutputPort: RenameCharacterArc.OutputPort
) : CharacterListViewListener {

    private val projectId = UUID.fromString(projectId)

    override fun getList() {
        threadTransformer.async {
            listAllCharacterArcs.invoke(projectId, listAllCharacterArcsOutputPort)
        }
    }

    override fun openBaseStoryStructureTool(characterId: String, themeId: String) {
        openToolController.openBaseStoryStructureTool(themeId, characterId)
    }

    override fun openCharacterValueComparison(themeId: String) {
        openToolController.openCharacterValueComparison(themeId)
    }

    override fun openCentralConflict(themeId: String, characterId: String) {
        openToolController.openCentralConflict(themeId, characterId)
    }

    override fun renameCharacter(characterId: String, newName: NonBlankString) {
        threadTransformer.async {
            renameCharacter.invoke(
              UUID.fromString(characterId),
              newName,
              renameCharacterOutputPort
            )
        }
    }

    override fun removeCharacter(characterId: String) {
        removeCharacterFromStoryController.requestRemoveCharacter(characterId)
    }

    override fun removeCharacterArc(characterId: String, themeId: String) {
        threadTransformer.async {
            deleteCharacterArc.invoke(
                UUID.fromString(themeId),
                UUID.fromString(characterId),
                deleteCharacterArcOutputPort
            )
        }
    }

    override fun renameCharacterArc(characterId: String, themeId: String, newName: NonBlankString) {
        threadTransformer.async {
            renameCharacterArc.invoke(
              RenameCharacterArc.RequestModel(
                UUID.fromString(characterId),
                UUID.fromString(themeId),
                newName
              ),
              renameCharacterArcOutputPort
            )
        }
    }
}