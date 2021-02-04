package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.components.Chip
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroup
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.sceneDetails.includedCharacter.AvailableArcSectionViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.AvailableCharacterArcViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.CoveredArcSectionViewModel
import javafx.scene.Node
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot

private val robot = FxRobot()

fun ProjectScope.getSceneDetails(scene: Scene): SceneDetails? {
    return toolScopes.filterIsInstance<SceneDetailsScope>().find { it.sceneId == scene.id.uuid }?.get()
}

fun ProjectScope.getSceneDetailsOrError(scene: Scene): SceneDetails =
    getSceneDetails(scene) ?: error("No open scene details tool for scene $scene")

fun ProjectScope.openSceneDetails(scene: Scene): SceneDetails {
    robot.interact {
        get<OpenToolController>().openSceneDetailsTool(scene.id.uuid.toString())
    }
    return getSceneDetailsOrError(scene)
}

fun ProjectScope.givenSceneDetailsHasBeenOpened(scene: Scene): SceneDetails =
    getSceneDetails(scene) ?: openSceneDetails(scene)

fun SceneDetails.getListedCharacter(character: Character): SceneDetailsListedCharacterDriver? {
    val node = robot.from(root).lookup(".included-character").queryAll<Node>().find {
        it.id == character.id.uuid.toString()
    }
    return node?.let(::SceneDetailsListedCharacterDriver)
}

fun SceneDetails.getListedCharacterOrError(character: Character): SceneDetailsListedCharacterDriver =
    getListedCharacter(character)
        ?: error("Scene details for Scene: ${scope.sceneId} does not contain character $character")

class SceneDetailsListedCharacterDriver(val node: Node) {

    private fun menuChipGroup(): MenuChipGroup {
        return robot.from(node).lookup(".position-on-arc").query<MenuChipGroup>()
    }

    private fun List<MenuItem>.gatherChildren(): Sequence<MenuItem> {
        return asSequence().flatMap {
            when (it) {
                is Menu -> it.items.gatherChildren()
                else -> sequenceOf(it)
            }
        }
    }

    fun whenPositionOnCharacterArcsSelected() {
        robot.interact { menuChipGroup().show() }
    }

    fun givenPositionOnCharacterArcsHasBeenSelected() {
        if (!isPositionOnArcDisplayingAvailableArcs()) whenPositionOnCharacterArcsSelected()
    }

    fun isPositionOnArcDisplayingAvailableArcs(): Boolean = menuChipGroup().isShowing

    fun whenCharacterArcSectionsAreSelected(arcSections: List<CharacterArcSection>) {
        val arcSectionIds = arcSections.map { it.id.uuid.toString() }.toSet()
        val arcSectionItems = getListedArcSectionsItems()
        robot.interact {
            arcSectionItems.filter { it.second.arcSectionId in arcSectionIds }
                .forEach { it.first.fire() }
        }
    }

    fun whenPositionOnCharacterArcsIsHidden() {
        robot.interact { menuChipGroup().hide() }
    }

    fun getCoveredArcSections(): Set<String> {
        return menuChipGroup().chips.mapNotNull {
            (it.node.userData as? String)
        }.toSet()
    }

    fun getListedArcs(): List<String> = getListedArcItems().map { it.second.characterArcId }

    fun getListedArcItems(): List<Pair<Menu, AvailableCharacterArcViewModel>> =
        menuChipGroup().items
            .mapNotNull {
                val userData = it.userData as? AvailableCharacterArcViewModel ?: return@mapNotNull null
                (it as? Menu)?.let { it to userData }
            }.toList()

    fun getListedArcSections(): List<String> = getListedArcSectionsItems().map { it.second.arcSectionId }

    fun getListedArcSectionsItems(): List<Pair<MenuItem, AvailableArcSectionViewModel>> =
        menuChipGroup().items.gatherChildren()
            .mapNotNull {
                val userData = it.userData as? AvailableArcSectionViewModel ?: return@mapNotNull null
                it to userData
            }.toList()

    fun requestNewArcSectionForArc(arc: CharacterArc) {
        val (menu, arcVM) = getListedArcItems().find { it.second.characterArcId == arc.id.uuid.toString() } ?: return
        robot.interact { menu.items.first().fire() }
    }

}