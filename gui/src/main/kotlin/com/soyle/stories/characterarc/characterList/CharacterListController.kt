/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 4:05 PM
 */
package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.layout.usecases.openTool.OpenTool
import java.util.*

class CharacterListController(
    private val threadTransformer: ThreadTransformer,
    private val listAllCharacterArcs: ListAllCharacterArcs,
    private val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort,
    private val openTool: OpenTool,
    private val openToolOutputPort: OpenTool.OutputPort,
    private val removeCharacterFromStory: RemoveCharacterFromLocalStory,
    private val removeCharacterFromStoryOutputPort: RemoveCharacterFromLocalStory.OutputPort,
    private val deleteCharacterArc: DeleteLocalCharacterArc,
    private val deleteCharacterArcOutputPort: DeleteLocalCharacterArc.OutputPort,
    private val renameCharacter: RenameCharacter,
    private val renameCharacterOutputPort: RenameCharacter.OutputPort
) : CharacterListViewListener {

    override fun getList() {
        threadTransformer.async {
            listAllCharacterArcs.invoke(listAllCharacterArcsOutputPort)
        }
    }

    override fun openBaseStoryStructureTool(characterId: String, themeId: String) {
        val request = OpenTool.RequestModel.BaseStoryStructure(
            UUID.fromString(characterId),
            UUID.fromString(themeId)
        )
        threadTransformer.async {
            openTool(request, openToolOutputPort)
        }
    }

    override fun openCharacterComparison(characterId: String, themeId: String) {
        val request = OpenTool.RequestModel.CharacterComparison(
            UUID.fromString(characterId),
            UUID.fromString(themeId)
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
}