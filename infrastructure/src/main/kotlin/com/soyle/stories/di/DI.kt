package com.soyle.stories.di

import tornadofx.Component
import tornadofx.FX
import tornadofx.Scope
import tornadofx.find
import java.util.*
import kotlin.collections.set
import kotlin.reflect.KClass

private class RegisteredFactory<T : Any, S : Scope>(val factory: (S) -> T, val scopeType: KClass<S>) {
	fun create(scope: Scope): T? {
		if (! scopeType.isInstance(scope)) return null
		return factory(scope as S)
	}
}

object DI {

	var verbose: Boolean = false

	private val factories = mutableMapOf<KClass<*>, RegisteredFactory<*, *>>()
	private val relatedScopes = mutableMapOf<KClass<*>, Scope>()

	private val registeredTypes = mutableMapOf<Scope, HashMap<KClass<*>, Any>>()
	fun getRegisteredTypes(scope: Scope = FX.defaultScope) = registeredTypes.getOrPut(scope) { HashMap<KClass<*>, Any>() }
	fun deregister(scope: Scope) {
		registeredTypes.remove(scope)
	}
	fun isScopeRegistered(scope: Scope) = registeredTypes.containsKey(scope)

	inline fun <reified T : Any> resolve(scope: Scope = FX.defaultScope) = resolveClass(T::class, scope)
	inline fun <reified T : Any> resolveLater(scope: Scope = FX.defaultScope) = lazy { resolve<T>(scope) }

	@Suppress("UNCHECKED_CAST")
	fun <T : Any> resolveClass(kClass: KClass<in T>, scope: Scope = FX.defaultScope): T {
		return if (Component::class.java.isAssignableFrom(kClass.java)) {
			find(kClass as KClass<Component>, scope) as T
		} else {
			val useScope = relatedScopes[kClass] ?: scope
			val registeredTypes = getRegisteredTypes(useScope)

			if (! registeredTypes.containsKey(kClass)) {
				synchronized(FX.lock) {
					try {
						if (!registeredTypes.containsKey(kClass)) {

							val t = createType(kClass, useScope) ?: run {
								val errorMessage = StringBuilder("No registered module for type $kClass.")
								if (verbose) errorMessage.append("\nAll factories: ${factories}")
								throw Error(errorMessage.toString())
							}

							registeredTypes[kClass] = t
						}
					} catch (t: Throwable) {
						throw Error("Failed to retrieve $kClass in $useScope.\nCaused by: $t", t)
					}
				}
			}

			registeredTypes[kClass] as T
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T : Any> createType(kClass: KClass<in T>, scope: Scope): T? {
		val registeredFactory = factories[kClass] ?: return null
		val t = registeredFactory.create(scope) as T? ?: error("Incorrect scope $scope.  Was expecting ${registeredFactory.scopeType} for type $kClass")
		return t
	}

	@JvmName("registerTypeFactoryDefaultScope")
	inline fun <reified T : Any> registerTypeFactory(noinline factory: (Scope) -> T) = registerTypeFactory(T::class, Scope::class, factory)

	inline fun <reified T : Any, reified S : Scope> registerTypeFactory(noinline factory: (S) -> T) = registerTypeFactory(T::class, S::class, factory)
	fun <T : Any, S: Scope> registerTypeFactory(kClass: KClass<in T>, scopeClass: KClass<S>, factory: (S) -> T) {
		factories[kClass] = RegisteredFactory(factory, scopeClass)
	}

}

inline fun <reified T : Any> Component.resolve(scope: Scope = this.scope) = DI.resolve<T>(scope)
inline fun <reified T : Any> Component.resolveLater(scope: Scope = this.scope) = lazy { DI.resolve<T>(scope) }

inline fun <reified T : Any> Scope.get(): T = DI.resolve(this)

class InScope<S : Scope>(val scopeClass: KClass<S>) {

	inline fun <reified T : Any> resolveLater() = DI.resolveLater<T>()
	inline fun <reified T : Any> provide(vararg types: KClass<in T>, noinline factory: S.() -> T) {
		val uniqueTypes = types.toSet()
		val type = T::class
		DI.registerTypeFactory(T::class, scopeClass, factory)
		uniqueTypes.forEach { otherType ->
			DI.registerTypeFactory<T, S>(otherType, scopeClass) {
				DI.resolveClass(type, it)
			}
		}
	}
}
inline fun <reified S : Scope> scoped(inScope: InScope<S>.() -> Unit) {
	InScope<S>(S::class).inScope()
}
