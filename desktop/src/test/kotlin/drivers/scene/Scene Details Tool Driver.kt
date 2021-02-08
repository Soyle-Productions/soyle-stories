package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.desktop.config.drivers.character.getCreateArcSectionDialogOrError
import com.soyle.stories.desktop.view.scene.sceneDetails.drive
import com.soyle.stories.desktop.view.scene.sceneDetails.driver
import com.soyle.stories.desktop.view.scene.sceneList.drive
import com.soyle.stories.desktop.view.scene.sceneList.driver
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.sceneDetails.SceneDetails
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneList.SceneListView

fun SceneListView.givenSceneDetailsToolHasBeenOpened(scene: Scene): SceneDetails =
    getOpenSceneDetails(scene) ?: openSceneDetails(scene).run { getOpenSceneDetailsOrError(scene) }

fun SceneListView.getOpenSceneDetailsOrError(scene: Scene): SceneDetails =
    getOpenSceneDetails(scene) ?: error("Scene details tool is not open for the scene ${scene.name}")

fun SceneListView.getOpenSceneDetails(scene: Scene): SceneDetails? =
    scope.toolScopes.asSequence().filterIsInstance<SceneDetailsScope>()
        .filter { it.sceneId == scene.id.uuid }.firstOrNull()
        ?.get<SceneDetails>().takeIf { it?.currentStage?.isShowing == true }

private fun SceneListView.openSceneDetails(scene: Scene) {
    val sceneItem = driver().getSceneItemOrError(scene.name.value)
    drive {
        tree.selectionModel.select(sceneItem)
        sceneItem
            .getSceneDetailsItem()
            .fire()
    }
}

fun SceneDetails.givenPositionOnArcInputForCharacterHasBeenSelected(character: Character): SceneDetails {
    if (! driver().getIncludedCharacter(character.id.uuid.toString()).getPositionOnArcInput().isShowing) {
        selectPositionOnArcInputForCharacter(character)
    }
    return this
}

fun SceneDetails.selectPositionOnArcInputForCharacter(character: Character) {
    drive {
        getIncludedCharacter(character.id.uuid.toString())
            .getPositionOnArcInput()
            .fire()
    }
}

fun SceneDetails.coverSectionInArc(arcName: String, sectionName: String) {
    val includedCharacterDriver = driver().findIncludedCharacter { it.getPositionOnArcInput().isShowing }!!
    with (includedCharacterDriver) {
        val sectionItem = getPositionOnArcInput().getArcItem(arcName)!!
            .getArcSectionItemOrError(sectionName)
        if (sectionItem.isCovered()) return@with
        drive {
            sectionItem.fire()
            getPositionOnArcInput().hide()
        }
    }
}

fun SceneDetails.uncoverSectionInArc(arcName: String, sectionName: String) {
    val includedCharacterDriver = driver().findIncludedCharacter { it.getPositionOnArcInput().isShowing }!!
    with (includedCharacterDriver) {
        val sectionItem = getPositionOnArcInput().getArcItem(arcName)!!
            .getArcSectionItemOrError(sectionName)
        if (! sectionItem.isCovered()) return@with
        drive {
            sectionItem.fire()
            getPositionOnArcInput().hide()
        }
    }
}

fun SceneDetails.givenCreateNewSectionInArcSelected(arcName: String): CreateArcSectionDialogView
{
    val includedCharacterDriver = driver().findIncludedCharacter { it.getPositionOnArcInput().isShowing }!!
    with (includedCharacterDriver) {
        val createNewOption = getPositionOnArcInput().getArcItem(arcName)!!
            .getCreateNewSectionOption()
        drive {
            createNewOption.fire()
        }
    }
    return getCreateArcSectionDialogOrError()
}