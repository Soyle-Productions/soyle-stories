package com.soyle.stories.core.framework

interface PotentialChangesAssertions {

    fun `should include`(effect: Any)

    fun `should not include`(effect: Any)
}