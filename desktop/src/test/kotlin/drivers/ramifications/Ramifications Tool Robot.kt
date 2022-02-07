package com.soyle.stories.desktop.config.drivers.ramifications

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.project.WorkBench
import com.soyle.stories.ramifications.RamificationsView
import com.soyle.stories.ramifications.Report
import tornadofx.*
import kotlin.error
import kotlin.reflect.KClass

fun WorkBench.getOpenRamificationsTool(): RamificationsView? {
    return (FX.getComponents(scope)[RamificationsView::class] as? RamificationsView)
        ?.takeIf { it.isDocked }
}

fun WorkBench.getOpenRamificationsToolOrError(): RamificationsView =
    getOpenRamificationsTool() ?: error("Ramifications tool has not been opened")

fun <T : UIComponent> RamificationsView.getOpenReport(type: KClass<T>): Report? {
    return viewModel.reports.find {
        val component = it.content?.properties?.get(UI_COMPONENT_PROPERTY) ?: return@find false
        type.isInstance(component) && it.isListed
    }
}

fun <T : UIComponent> RamificationsView.getOpenReportOrError(type: KClass<T>): Report =
    getOpenReport(type) ?: error("No report found of type $type")