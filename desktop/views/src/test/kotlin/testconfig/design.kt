package com.soyle.stories.desktop.view.testconfig

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.soylestories.Styles
import javafx.scene.Node
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import tornadofx.FX
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import org.junit.jupiter.api.condition.EnabledIf

const val DESIGN = true
const val ROOT_DESIGN = true
fun designEnabled() = DESIGN && !ROOT_DESIGN
fun rootOnlyDesignEnabled() = DESIGN

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EnabledIf("com.soyle.stories.desktop.view.testconfig.DesignKt#designEnabled", disabledReason = "Design tests are not enabled or root-only design tests are enabled")
@Test
annotation class DesignTest

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EnabledIf("com.soyle.stories.desktop.view.testconfig.DesignKt#rootOnlyDesignEnabled", disabledReason = "No design tests are not enabled")
@Test
annotation class RootDesignTest