package com.soyle.stories.core.definitions

import com.soyle.stories.core.framework.PotentialChangesAssertions
import com.soyle.stories.usecase.shared.potential.PotentialChanges
import org.amshove.kluent.should

class PotentialChangesAssertionsDef(
    private val potentialChanges: PotentialChanges<*>
) : PotentialChangesAssertions {

    override fun `should include`(effect: Any) {
        potentialChanges as Collection<*>
        potentialChanges.should("""
            $potentialChanges should include $effect
                items: ${potentialChanges.toList()}
        """.trimIndent()) {
            (potentialChanges as Collection<*>).contains(effect)
        }
    }

    override fun `should not include`(effect: Any) {
        potentialChanges as Collection<*>
        potentialChanges.should("""
            $potentialChanges should not include $effect
                items: ${potentialChanges.toList()}
        """.trimIndent()) {
            ! (potentialChanges as Collection<*>).contains(effect)
        }
    }

}