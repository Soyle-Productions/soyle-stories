package com.soyle.stories.common.collections

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import java.util.*
import java.util.function.Function

/**
 * Thanks to TomasMikula for the gist!  https://gist.github.com/TomasMikula/8883719
 * @param <E>
 * @param <F>
</F></E> */
class MappedList<E, F>(source: ObservableList<out F>?, private val mapper: Function<F, E>) :
    TransformationList<E, F>(source) {
    override fun getSourceIndex(index: Int): Int {
        return index
    }

    override fun getViewIndex(index: Int): Int {
        return index
    }

    override fun get(index: Int): E {
        return mapper.apply(source[index])
    }

    override val size: Int
        get() = source.size

    override fun sourceChanged(c: ListChangeListener.Change<out F>) {
        fireChange(object : ListChangeListener.Change<E>(this) {
            override fun wasAdded(): Boolean {
                return c.wasAdded()
            }

            override fun wasRemoved(): Boolean {
                return c.wasRemoved()
            }

            override fun wasReplaced(): Boolean {
                return c.wasReplaced()
            }

            override fun wasUpdated(): Boolean {
                return c.wasUpdated()
            }

            override fun wasPermutated(): Boolean {
                return c.wasPermutated()
            }

            override fun getPermutation(i: Int): Int {
                return c.getPermutation(i)
            }

            override fun getPermutation(): IntArray {
                // This method is only called by the superclass methods
                // wasPermutated() and getPermutation(int), which are
                // both overriden by this class. There is no other way
                // this method can be called.
                throw AssertionError("Unreachable code")
            }

            override fun getRemoved(): List<E> {
                val res = ArrayList<E>(c.removedSize)
                for (e in c.removed) {
                    res.add(mapper.apply(e))
                }
                return res
            }

            override fun getFrom(): Int {
                return c.from
            }

            override fun getTo(): Int {
                return c.to
            }

            override fun next(): Boolean {
                return c.next()
            }

            override fun reset() {
                c.reset()
            }
        })
    }
}