package com.soyle.stories.project.layout

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 4:07 PM
 */
class GroupSplitter : Fragment() {

    private val viewModelProperty = SimpleObjectProperty<GroupSplitterViewModel?>(null)

    var viewModel: GroupSplitterViewModel?
        get() = viewModelProperty.value
        set(value) {
            viewModelProperty.set(value)
        }

    override val root: Parent = splitpane {
        orientationProperty().bind(viewModelProperty.objectBinding { if (it?.orientation == true) Orientation.HORIZONTAL else Orientation.VERTICAL })
        fitToParentSize()
        viewModelProperty.onChange {
            if (it == null) return@onChange

            val oldItemSize = items.size

            val newItems = it.children.map { (_, child) ->
                when (child) {
                    is GroupSplitterViewModel -> {
                        val fragment = getChildFragment(GroupSplitter::class, child.id)
                        fragment.viewModel = child
                        fragment.root
                    }
                    is ToolGroupViewModel -> {
                        val fragment = getChildFragment(ToolGroup::class, child.id)
                        fragment.viewModel = child
                        fragment.root
                    }
                }
            }.toSet()

            // items.setAll forces the split pane to adjust all the dividers, so, to avoid a jarring UX in which the
            // entire workspace jumps around, we remove items if they aren't in the newItemsSet.
            items.removeIf {
                it !in newItems
            }
            // after removing items, if the items and newItems are still not in the right order, we have to setAll.
            // calling [toFront] and [toBack] on each newItem to set the order does NOTHING.
            if (! items.zip(newItems).all { it.first == it.second }) {
                items.setAll(newItems)
            }

            // clean up the child map so we don't have unused nodes sitting around in memory.
            removeUnusedChildrenFragments(it.children)

            // finally, if we removed or added items this cycle, set the dividers based on the saved layout
            if (items.size != oldItemSize) {
                val fullWeight = it.children.fold(0) { acc, (weight, _) -> acc + weight }
                it.children.dropLast(1).forEachIndexed { index, (weight, _) ->
                    dividers[index].position = weight.toDouble() / fullWeight
                }
            }
        }
    }

    private val childMap = mutableMapOf<String, Fragment>()

    private fun removeUnusedChildrenFragments(children: List<Pair<Int, WindowChildViewModel>>) {
        val childIds = children.map { it.second.id }.toSet()
        childMap.keys.removeIf { it !in childIds }
    }

    private fun <F : Fragment> Node.getChildFragment(klass: KClass<F>, id: String): F {
        val fragment = childMap[id]
        return if (klass.isInstance(fragment)) {
            @Suppress("UNCHECKED_CAST")
            fragment as F
        } else {
            find(klass) {
                this@getChildFragment += this
                childMap[id] = this
            }
        }
    }
}