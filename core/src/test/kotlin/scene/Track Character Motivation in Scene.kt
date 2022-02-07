package com.soyle.stories.core.scene

import com.soyle.stories.core.definitions.CoreTest
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.character.effects.CharacterGainedInheritedMotivationInScene
import com.soyle.stories.usecase.scene.character.effects.InheritedCharacterMotivationInSceneCleared
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Track Character Motivation in Scene` : CoreTest() {

    private val project = given.`a project`().`has been started`()
    val bigBattleScene = given.`a scene`(named = "Big Battle").`has been created in the`(project)
    val smallConflictScene = given.`a scene`(named = "Small Conflict").`has been created in the`(project)
    val character = given.`a character`(named = "Bob").`has been created in the`(project)

    init {
        given.the(character).`has been included in the`(bigBattleScene)
    }

    @Test
    fun `Set Character Motivation in Scene`() {
        `when`.the(character).`in the`(bigBattleScene).`is motivated by`("Getting dat bread")

        then.the(character).`in the`(bigBattleScene).`should be motivated by`("Getting dat bread")
    }

    @Test
    fun `Set Character Motivation in Previous Scene`() {
        with(given) {
            and.the(character).`has been included in the`(smallConflictScene)
            and.the(character).`in the`(bigBattleScene).`has been motivated by`("Getting dat bread")
        }

        val inspection: CharacterInSceneInspection = `when`.`the user`().`inspects the`(character, inThe = smallConflictScene)

        then.the(inspection).`should have an inherited motivation of`("Getting dat bread")
        then.the(inspection).`should have an inherited motivation from the`(bigBattleScene)
    }

    @Test
    fun `Override Character Motivation from Previous Scene`() {
        with(given) {
            and.the(character).`has been included in the`(smallConflictScene)
            and.the(character).`in the`(bigBattleScene).`has been motivated by`("Getting dat bread")
            and.the(character).`in the`(smallConflictScene).`has been motivated by`("Giving away bread")
        }

        val inspection: CharacterInSceneInspection = `when`.`the user`().`inspects the`(character, inThe = smallConflictScene)

        then.the(inspection).`should have an inherited motivation of`("Getting dat bread")
        then.the(inspection).`should have an inherited motivation from the`(bigBattleScene)
        then.the(inspection).`should have motivation of`("Giving away bread")
    }

    @Test
    fun `Delete Scene with Dependent Scene`() {
        with(given) {
            and.the(character).`has been included in the`(smallConflictScene)
            and.the(character).`in the`(bigBattleScene).`has been motivated by`("Getting dat bread")
            and.the(bigBattleScene).`has been removed from the`(project)
        }

        val inspection: CharacterInSceneInspection = `when`.`the user`().`inspects the`(character, inThe = smallConflictScene)

        then.the(inspection).`should not have an inherited motivation`()
    }

    @Test
    fun `Delete Scene with Dependent Scene and Back-Up Scene`() {
        val giantWarScene = given.`a scene`(named = "Giant War", atIndex = 0).`has been created in the`(project)
        with(given) {
            and.the(character).`has been included in the`(giantWarScene)
            and.the(character).`has been included in the`(smallConflictScene)
            and.the(character).`in the`(giantWarScene).`has been motivated by`("Getting dat bread")
            and.the(character).`in the`(bigBattleScene).`has been motivated by`("Giving away the bread")
            and.the(bigBattleScene).`has been removed from the`(project)
        }

        val inspection: CharacterInSceneInspection = `when`.`the user`().`inspects the`(character, inThe = smallConflictScene)

        then.the(inspection).`should have an inherited motivation of`("Getting dat bread")
        then.the(inspection).`should have an inherited motivation from the`(giantWarScene)
    }

    @Nested
    inner class `Should Confirm Before Unknowingly Modifying a Character's Motivation` {

        init {
            given.the(character).`has been included in the`(smallConflictScene)
        }

        @Test
        fun `Potentially Delete Scene with Dependent Scene`() {
            given.the(character).`in the`(bigBattleScene).`has been motivated by`("Getting dat bread")

            val potentialChanges = `when`.`the user`().`lists the potential changes of`().the(bigBattleScene).`being removed from the`(project)

            then.the(potentialChanges).`should include`(InheritedCharacterMotivationInSceneCleared(
                smallConflictScene,
                "Small Conflict",
                character,
                "Bob",
                InheritedMotivation(
                    bigBattleScene,
                    character,
                    "Big Battle",
                    "Getting dat bread"
                )
            ))
        }

        @Test
        fun `Potentially Delete Scene with Dependent Scene and Back-Up Scene`() {
            val giantWarScene = given.`a scene`(named = "Giant War", atIndex = 0).`has been created in the`(project)
            with(given) {
                and.the(character).`has been included in the`(giantWarScene)
                and.the(character).`in the`(giantWarScene).`has been motivated by`("Getting dat bread")
                and.the(character).`in the`(bigBattleScene).`has been motivated by`("Giving away the bread")
            }

            val potentialChanges = `when`.`the user`().`lists the potential changes of`().the(bigBattleScene).`being removed from the`(project)

            then.the(potentialChanges).`should include`(
                CharacterGainedInheritedMotivationInScene(
                    smallConflictScene,
                    "Small Conflict",
                    character,
                    "Bob",
                    inheritedMotivation = InheritedMotivation(
                        giantWarScene,
                        character,
                        "Giant War",
                        "Getting dat bread"
                    ),
                    previousInheritance = InheritedMotivation(
                        bigBattleScene,
                        character,
                        "Big Battle",
                        "Giving away the bread"
                    )
                )
            )
        }

    }

}