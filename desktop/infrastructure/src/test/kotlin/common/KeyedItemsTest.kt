package com.soyle.stories.common

import javafx.collections.ListChangeListener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tornadofx.bind
import tornadofx.observableListOf

class KeyedItemsTest {

    class Source(val id: String, val value: Any?)
    class Counted(val id: Int) {
        override fun toString(): String = "Counted($id)"
    }

    val sourceList = observableListOf<Source>()

    val targetList = observableListOf<Counted>()

    @Test
    fun `Basic Binding`() {
        var counter = 0
        sourceList.mapObservableTo(targetList) { Counted(counter++) }
        sourceList.add(Source("", 9))
        assertEquals(
            """
                [Counted(0)]
            """.trimIndent(),
            targetList.toString()
        )
    }

    @Test
    fun `Synchronized if mapped with values`() {
        var counter = 0
        sourceList.add(Source("", 9))
        sourceList.mapObservableTo(targetList) { Counted(counter++) }
        assertEquals(
            """
                [Counted(0)]
            """.trimIndent(),
            targetList.toString()
        )
    }

    @Test
    fun `Notified when removed`() {
        var counter = 0
        val firstSource = Source("", 9)
        sourceList.add(firstSource)
        sourceList.mapObservableTo(targetList) { Counted(counter++) }
        var removedTargets = mutableListOf<Counted>()
        targetList.addListener { change: ListChangeListener.Change<out Counted> ->
            while (change.next()) {
                removedTargets.addAll(change.removed)
            }
        }
        sourceList.remove(firstSource)
        assertEquals(
            """
                [Counted(0)]
            """.trimIndent(),
            removedTargets.toString()
        )
    }

    @Test
    fun `Moving items with keys moves associated target items`() {
        var counter = 0
        sourceList.add(Source("A", "banana"))
        sourceList.add(Source("B", "orange"))
        sourceList.mapObservableTo(targetList, { it.id }) { Counted(counter++) }
        sourceList.setAll(sourceList[1], sourceList[0])
        assertEquals(
            """
                [Counted(1), Counted(0)]
            """.trimIndent(),
            targetList.toString()
        )
    }

    @Test
    fun `Keyed items are still able to be removed`() {
        var counter = 0
        val poppedSource = Source("A", "banana")
        sourceList.add(poppedSource)
        sourceList.add(Source("B", "orange"))
        sourceList.mapObservableTo(targetList, { it.id }) { Counted(counter++) }
        sourceList.remove(poppedSource)
        sourceList.add(poppedSource)
        assertEquals(
            """
                [Counted(1), Counted(2)]
            """.trimIndent(),
            targetList.toString()
        )
    }
}