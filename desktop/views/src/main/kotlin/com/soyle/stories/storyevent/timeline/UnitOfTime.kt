package com.soyle.stories.storyevent.timeline

import javafx.beans.value.ObservableValue
import kotlin.math.*

@JvmInline
value class UnitOfTime(val value: Long) : Comparable<UnitOfTime> {
    operator fun plus(other: Int): UnitOfTime = UnitOfTime(value + other)
    operator fun plus(other: Long): UnitOfTime = UnitOfTime(value + other)
    operator fun plus(other: UnitOfTime): UnitOfTime = UnitOfTime(value + other.value)
    operator fun minus(other: UnitOfTime): UnitOfTime = UnitOfTime(value - other.value)
    operator fun minus(other: Int): UnitOfTime = UnitOfTime(value - other)
    override fun compareTo(other: UnitOfTime): Int = value.compareTo(other.value)
    operator fun compareTo(other: Long): Int = value.compareTo(other)
    operator fun div(other: UnitOfTime): UnitOfTime = UnitOfTime(value / other.value)
    operator fun times(other: UnitOfTime): UnitOfTime = UnitOfTime(value * other.value)
    operator fun rangeTo(other: UnitOfTime): TimeRange = TimeRange(value..other.value)
}

@JvmInline
value class TimeRange(val range: LongRange) : ClosedRange<UnitOfTime>, Iterable<UnitOfTime> {

    override val start: UnitOfTime
        get() = UnitOfTime(range.first)
    override val endInclusive: UnitOfTime
        get() = UnitOfTime(range.last)

    fun hasOverlapWith(other: TimeRange): Boolean {
        if (isEmpty() || other.isEmpty()) return false
        return other.start >= start && other.start < endInclusive || start >= other.start && start < other.endInclusive
    }

    val duration: UnitOfTime
        get() = endInclusive - start

    override fun iterator(): Iterator<UnitOfTime> = object : Iterator<UnitOfTime> {
        var current = start

        override fun hasNext(): Boolean = current < endInclusive

        override fun next(): UnitOfTime {
            return current.also { current += 1 }
        }
    }
}

@JvmInline
value class Pixels(val value: Double) : Comparable<Pixels> {
    operator fun plus(other: Pixels): Pixels = Pixels(value + other.value)
    operator fun plus(other: Double): Pixels = Pixels(value + other)
    operator fun minus(other: Pixels): Pixels = Pixels(value - other.value)
    operator fun minus(other: Number): Pixels = Pixels(value - other.toDouble())
    override fun compareTo(other: Pixels) = value.compareTo(other.value)
}

@JvmInline
value class Scale private constructor(val unitInPixels: Double) {

    companion object {
        fun at(pixelDensity: Double): Result<Scale> {
            if (pixelDensity < 1.0) {
                return Result.failure(Exception("Pixel density cannot be less than 1.0.  Received: $pixelDensity"))
            }
            return Result.success(Scale(round(pixelDensity)))
        }

        fun maxZoomIn(): Scale = Scale.at(48.0).getOrThrow()
        fun maxZoomOut(): Scale = at(1.0).getOrThrow()

        private val stepLookup = listOf<Long>(
            1,
            5,
            10,
            20,
            25,
            100
        )
    }

    operator fun invoke(time: UnitOfTime): Pixels = Pixels(time.value * unitInPixels)
    operator fun invoke(pixels: Pixels): UnitOfTime = UnitOfTime(floor(pixels.value / unitInPixels).toLong())

    val timeUnitStep: UnitOfTime
        get() = stepLookup.firstOrNull {
            it * unitInPixels >= 40.0
        }?.let(::UnitOfTime).let {
            if (it == null) throw Error("Could not find timeUnitStep for $unitInPixels")
            it
        }

    fun zoomed(delta: Double): Scale {
        val newPixels = unitInPixels + if (delta < 0.0) -log(abs(delta), 10.0) else log(abs(delta), 10.0)
        return when {
            newPixels < 1.0 -> Scale(1.0)
            newPixels > 48.0 -> Scale(48.0)
            else -> Scale(round(newPixels))
        }
    }

}

class TimelineDimensions private constructor(
    val offsetX: Pixels,
    val scale: Scale,
    val width: Pixels
) {

    companion object {
        fun of(scale: Scale, offsetX: Pixels, width: Pixels): Result<TimelineDimensions> {
            if (offsetX.value >= scale.unitInPixels) {
                return Result.failure(Exception("Offset cannot be greater than a single unit in pixels.  Received: $offsetX at $scale"))
            }

            return Result.success(TimelineDimensions(offsetX, scale, width))
        }
    }

    fun withOffset(newOffset: Pixels): Result<TimelineDimensions> = of(scale, newOffset, width)
    fun atScale(newScale: Scale): Result<TimelineDimensions> = of(newScale, offsetX, width)

}