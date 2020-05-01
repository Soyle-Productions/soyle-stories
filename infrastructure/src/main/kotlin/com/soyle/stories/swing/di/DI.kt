package com.soyle.stories.swing.di

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class Scope

val defaultScope = Scope()
private val fixedScopes = mutableMapOf<KClass<*>, Scope>()
private val threadLocalScope = object : ThreadLocal<Scope>() {
	override fun initialValue(): Scope = defaultScope
}

private val _lock = Any()

private val _components = mutableMapOf<Scope, MutableMap<KClass<*>, Any>>()
val Scope.components: MutableMap<KClass<*>, Any>
	get() = _components.getOrPut(this) { mutableMapOf() }

inline fun <reified T : Any> find() = find(T::class)

fun <T : Any> find(type: KClass<T>): T {
	val useScope = fixedScopes[type] ?: defaultScope
	threadLocalScope.set(useScope)
	val scopedComponents = useScope.components
	if (!scopedComponents.containsKey(type)) {
		synchronized(_lock) {
			if (!scopedComponents.containsKey(type)) {
				val component = type.java.getDeclaredConstructor().newInstance()
				scopedComponents[type] = component
			}
		}
	}
	return scopedComponents[type] as T
}

inline fun <reified T : Any> inject() = lazy {
	find<T>()
}