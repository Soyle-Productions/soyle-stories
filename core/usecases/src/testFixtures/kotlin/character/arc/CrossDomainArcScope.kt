package com.soyle.stories.usecase.character.arc

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.usecase.framework.CrossDomainTest

interface CrossDomainArcScope {
    val arc: CharacterArc
    val test: CrossDomainTest
}
interface CharacterArcGivens : CrossDomainArcScope
interface CharacterArcWhens : CrossDomainArcScope
interface CharacterArcThens : CrossDomainArcScope

fun CrossDomainTest.`when the character arc`(arc: CharacterArc): CharacterArcWhens = object : CharacterArcWhens {
    override val arc: CharacterArc = arc
    override val test: CrossDomainTest = this@`when the character arc`
}

fun CrossDomainTest.`then the character arc`(arc: CharacterArc): CharacterArcThens = object : CharacterArcThens {
    override val arc: CharacterArc = arc
    override val test: CrossDomainTest = this@`then the character arc`
}