package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.TimelineDimensions
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRulerComponent
import javafx.scene.Node
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.OS
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.observableSetOf
import tornadofx.onChange

class `Ruler Design` : DesignTest(), TimelineRulerComponent by TimelineViewPortRulerComponentDouble() {

    private val dimensions = objectProperty<TimelineDimensions>(
        TimelineDimensions.of(Scale.at(48.0).getOrThrow(), Pixels(0.0), Pixels(0.0)).getOrThrow()
    )
    private val firstTimeUnit = objectProperty<UnitOfTime>(UnitOfTime(0))

    override val node: TimelineRuler by lazy { TimelineRuler(observableSetOf()) }

    private fun bindVMToStage(stage: Stage) {
        stage.scene.setOnScroll {
            if (it.isControlDown) {
                dimensions.set(
                    TimelineDimensions.of(
                        dimensions.get().scale.zoomed(it.deltaY),
                        dimensions.get().offsetX,
                        dimensions.get().width,
                    ).getOrThrow()
                )
            }
        }
        stage.scene.widthProperty().onChange {
            node.visibleRange = node.scale(node.offsetX) .. node.scale(node.offsetX + Pixels(it))
        }

    }

    @Test
    fun standard() {
        verifyDesign {
            bindVMToStage(this)
        }
    }

    @Test
    fun `nearing 1k`() {
        firstTimeUnit.set(UnitOfTime(896))
        verifyDesign {
            node.offsetX = node.scale(UnitOfTime(896))
            bindVMToStage(this)
        }
    }

    @Test
    fun `zoomed to 1 pixel`() {
        dimensions.set(
            TimelineDimensions.of(Scale.at(1.0).getOrThrow(), Pixels(0.0), Pixels(0.0)).getOrThrow()
        )
        verifyDesign {
            node.scale = Scale.at(1.0).getOrThrow()
            node.offsetX = node.scale(UnitOfTime(215))
            bindVMToStage(this)
        }
    }

}