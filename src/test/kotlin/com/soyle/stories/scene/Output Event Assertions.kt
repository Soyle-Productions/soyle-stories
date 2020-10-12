package com.soyle.stories.scene

import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun characterArcSectionCoveredByScene(baseSection: CharacterArcSection, baseArc: CharacterArc, sceneId: UUID) = fun (actual: CharacterArcSectionCoveredByScene?) {
    actual as CharacterArcSectionCoveredByScene
    assertEquals(baseSection.characterId.uuid, actual.characterId) {
        "Unexpected characterId for CharacterArcSectionCoveredByScene" }
    assertEquals(baseSection.themeId.uuid, actual.themeId) {
        "Unexpected themeId for CharacterArcSectionCoveredByScene" }
    assertEquals(baseSection.template.name, actual.characterArcSectionName) {
        "Unexpected characterArcSectionName for CharacterArcSectionCoveredByScene" }
    assertEquals(baseSection.value, actual.characterArcSectionValue) {
        "Unexpected characterArcSectionValue for CharacterArcSectionCoveredByScene" }
    assertEquals(baseArc.id.uuid, actual.characterArcId) {
        "Unexpected characterArcId for CharacterArcSectionCoveredByScene" }
    assertEquals(baseArc.name, actual.characterArcName) {
        "Unexpected characterArcName for CharacterArcSectionCoveredByScene" }
    assertEquals(sceneId, actual.sceneId) {
        "Unexpected sceneId for CharacterArcSectionCoveredByScene" }
    assertEquals(baseSection.template.allowsMultiple, actual.isMultiTemplate) {
        "Unexpected isMultiTemplate for CharacterArcSectionCoveredByScene" }
}