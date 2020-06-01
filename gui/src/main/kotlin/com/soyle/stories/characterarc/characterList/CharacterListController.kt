package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.tools.dynamic.BaseStoryStructure
import com.soyle.stories.layout.tools.dynamic.CharacterComparison
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

class CharacterListController(
    private val threadTransformer: ThreadTransformer,
    private val listAllCharacterArcs: ListAllCharacterArcs,
    private val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort,
    private val openTool: OpenTool,
    private val openToolOutputPort: OpenTool.OutputPort,
    private val removeCharacterFromStory: RemoveCharacterFromStory,
    private val removeCharacterFromStoryOutputPort: RemoveCharacterFromStory.OutputPort,
    private val deleteCharacterArc: DemoteMajorCharacter,
    private val deleteCharacterArcOutputPort: DemoteMajorCharacter.OutputPort,
    private val renameCharacter: RenameCharacter,
    private val renameCharacterOutputPort: RenameCharacter.OutputPort,
    private val renameCharacterArc: RenameCharacterArc,
    private val renameCharacterArcOutputPort: RenameCharacterArc.OutputPort
) : CharacterListViewListener {

    override fun getList() {
        threadTransformer.async {
            listAllCharacterArcs.invoke(listAllCharacterArcsOutputPort)
        }
    }

    override fun openBaseStoryStructureTool(characterId: String, themeId: String) {
        val request = BaseStoryStructure(
            UUID.fromString(characterId),
            UUID.fromString(themeId)
        )
        threadTransformer.async {
            openTool(request, openToolOutputPort)
        }
    }

    override fun openCharacterComparison(characterId: String, themeId: String) {
        val request = CharacterComparison(
          UUID.fromString(themeId),
            UUID.fromString(characterId)
        )
        threadTransformer.async {
            openTool(request, openToolOutputPort)
        }
    }

    override fun renameCharacter(characterId: String, newName: String) {
        threadTransformer.async {
            renameCharacter.invoke(
              UUID.fromString(characterId),
              newName,
              renameCharacterOutputPort
            )
        }
    }

    override fun removeCharacter(characterId: String) {
        threadTransformer.async {
            removeCharacterFromStory.invoke(
                UUID.fromString(characterId),
                removeCharacterFromStoryOutputPort
            )
        }
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

    override fun renameCharacterArc(characterId: String, themeId: String, newName: String) {
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