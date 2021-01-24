package com.soyle.stories.common

import tornadofx.Component
import tornadofx.Scope
import kotlin.reflect.KClass

abstract class ScopedTestDouble<S : Scope>(scopeType: KClass<S>) : Component() {

    override val scope: S = if (scopeType.isInstance(super.scope))
        @Suppress("UNCHECKED_CAST")
        super.scope as S
    else error("Scope is not of type $scopeType")

}