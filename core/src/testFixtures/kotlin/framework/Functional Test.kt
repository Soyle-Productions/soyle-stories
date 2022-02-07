package com.soyle.stories.core.framework

import com.soyle.stories.core.framework.scene.`Covered Story Events Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.core.framework.storyevent.StoryEventCharacterSteps
import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.shared.potential.PotentialChanges

interface `Functional Test` {

    interface Givens : `Project Steps`.Given, `Character Steps`.Given, `Scene Steps`.Given, `Story Event Steps`.Given

    val Givens.and: Givens get() = this
    val Givens.but: Givens get() = this

    interface Whens : `Character Steps`.When, `Scene Steps`.When, `Story Event Steps`.When {
        fun `the user`(): Queries
    }

    interface PotentialWhens : `Story Event Steps`.When.UserQueries.PotentialWhens,
        `Character Steps`.When.UserQueries.PotentialWhens,
        `Scene Steps`.When.UserQueries.PotentialWhens

    interface Queries : `Scene Character Steps`.When.UserQueries, `Story Event Steps`.When.UserQueries,
        `Covered Story Events Steps`.When.UserQueries, `Character Steps`.When.UserQueries,
        StoryEventCharacterSteps.When.UserQueries {
        override fun `lists the potential changes of`(): PotentialWhens
    }

    interface Thens : `Scene Steps`.Then, `Story Event Steps`.Then, `Character Steps`.Then {
        fun the(potentialChanges: PotentialChanges<*>): PotentialChangesAssertions
    }

    val Thens.and: Thens get() = this
    val Thens.but: Thens get() = this

    val `when`: Whens

    val given: Givens
    val then: Thens

    /**
     * Allows for infix functions that take [Unit] as their only parameter to be called as infix without having "Unit"
     * at the end of a test
     */
    val `-` get() = Unit

    interface PotentialEffectBuilder : `Scene Character Steps`.PotentialEffectBuilder

    fun effect(): PotentialEffectBuilder

}