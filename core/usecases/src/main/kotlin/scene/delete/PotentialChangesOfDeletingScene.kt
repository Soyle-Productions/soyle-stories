package com.soyle.stories.usecase.scene.delete

import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.shared.potential.PotentialChanges

class PotentialChangesOfDeletingScene(
    val sceneRemoved: SceneRemoved,
    val storyEventsUncovered: List<StoryEventUncoveredFromScene>,
    val hostedScenesRemoved: List<HostedSceneRemoved>,
    val inheritedCharacterMotivationChanges: List<CharacterInSceneEffect>
) : PotentialChanges<DeleteScene>,
    List<Any> by listOf(sceneRemoved) + storyEventsUncovered + hostedScenesRemoved + inheritedCharacterMotivationChanges