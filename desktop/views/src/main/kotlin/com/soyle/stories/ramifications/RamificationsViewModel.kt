package com.soyle.stories.ramifications

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.layout.closeTool.CloseToolController
import com.soyle.stories.layout.config.fixed.Ramifications
import com.soyle.stories.layout.openTool.OpenToolController
import javafx.beans.InvalidationListener
import tornadofx.observableMapOf
import kotlin.reflect.KClass

class RamificationsViewModel(
    private val openToolController: OpenToolController,
    private val closeToolController: CloseToolController
) {

    var toolId: String? = null

    internal val _reports = observableMapOf<ReportKey, Report>()
    init {
        _reports.addListener(InvalidationListener { _ ->
            if (_reports.isEmpty() || _reports.values.none { it.isListed }) {
                toolId?.let { closeToolController.closeTool(it) }
            }
        })
    }

    val reports: List<Report>
        get() = _reports.values.toList()

    fun report(
        operation: KClass<*>,
        identifiableProps: Any?,
        initialize: Report.() -> Unit
    ): Report {
        val key = ReportKey(operation, identifiableProps)
        if (_reports.containsKey(key)) {
            return _reports.getValue(key)
        }
        return Report()
            .apply(initialize)
            .also {
                _reports[key] = it
                if (it.isListed) openToolController.openRamificationsTool()
                it.isListed().onChangeUntil({ it != true}) {
                    if (it == true) openToolController.openRamificationsTool()
                    if (it != true) _reports.remove(key)
                }
            }
    }

    internal data class ReportKey(
        val operation: KClass<*>,
        val identifiableProps: Any?
    )

}