package com.soyle.stories.di

import tornadofx.Component
import tornadofx.FX
import tornadofx.Scope
import tornadofx.find
import java.util.*
import kotlin.collections.set
import kotlin.reflect.KClass

private class RegisteredFactory<T : Any, S : Scope>(val factory: (S) -> T, val scopeType: KClass<S>, val isSingleton: Boolean) {

	fun create(scope: Scope): T? {
		if (! scopeType.isInstance(scope)) return null
		@Suppress("UNCHECKED_CAST")
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

			val factory = getRegisteredFactory(kClass)

			if (factory.isSingleton) {
				if (! registeredTypes.containsKey(kClass)) {
					synchronized(FX.lock) {
						if (!registeredTypes.containsKey(kClass)) {
							val t = createType(factory, kClass, useScope)
							registeredTypes[kClass] = t
						}
					}
				}
				registeredTypes[kClass] as T
			} else {
				createType(factory, kClass, useScope)
			}
		}
	}

	private fun <T : Any> getRegisteredFactory(kClass: KClass<in T>): RegisteredFactory<T, *>
	{
		val factory = factories[kClass] ?: error("No registered module for type $kClass.")
		@Suppress("UNCHECKED_CAST")
		return factory as RegisteredFactory<T, *>
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T : Any> createType(factory: RegisteredFactory<T, *>, kClass: KClass<in T>, scope: Scope): T {
		return try {
			factory.create(scope)
		} catch (t: Throwable) {
			throw Error("$t\n\tWhen resolving type $kClass.", t)
		} ?: error("Incorrect scope $scope.  Was expecting ${factory.scopeType} for type $kClass")
	}

	@JvmName("registerTypeFactoryDefaultScope")
	inline fun <reified T : Any> registerTypeFactory(noinline factory: (Scope) -> T) = registerTypeFactory(T::class, Scope::class, factory = factory)

	inline fun <reified T : Any, reified S : Scope> registerTypeFactory(noinline factory: (S) -> T) = registerTypeFactory(T::class, S::class, factory = factory)
	fun <T : Any, S: Scope> registerTypeFactory(kClass: KClass<in T>, scopeClass: KClass<S>, isSingleton: Boolean = true, factory: (S) -> T) {
		if (factories.containsKey(kClass) && verbose) {
			println("WARNING: Already registered type factory for $kClass as ${factories[kClass]?.scopeType}.  Replacing with $scopeClass")
		}
		factories[kClass] = RegisteredFactory(factory, scopeClass, isSingleton)
	}

}

inline fun <reified T : Any> Component.resolve(scope: Scope = this.scope) = DI.resolve<T>(scope)
inline fun <reified T : Any> Component.resolveLater(scope: Scope = this.scope) = lazy { DI.resolve<T>(scope) }

inline fun <reified T : Any> Scope.get(): T = DI.resolve(this)

class InScope<S : Scope>(val scopeClass: KClass<S>) {


	inline fun <reified T : Any> resolveLater() = DI.resolveLater<T>()
	inline fun <reified T : Any> provide(vararg types: KClass<in T>, noinline factory: S.() -> T) =
		provide(T::class, types, factory)
	fun <T : Any> provide(type: KClass<T>, types: Array<out KClass<in T>>, factory: S.() -> T) {
		val uniqueTypes = types.toSet()
		DI.registerTypeFactory(type, scopeClass, factory = factory)
		uniqueTypes.forEach { otherType ->
			DI.registerTypeFactory<T, S>(otherType, scopeClass) {
				DI.resolveClass(type, it)
			}
		}
	}
	inline fun <reified T : Any> factory(vararg types: KClass<in T>, noinline factory: S.() -> T) =
		factory(T::class, types, factory)
	fun <T : Any> factory(type: KClass<T>, types: Array<out KClass<in T>>, factory: S.() -> T) {
		val uniqueTypes = types.toSet()
		DI.registerTypeFactory(type, scopeClass, false, factory)
		uniqueTypes.forEach { otherType ->
			DI.registerTypeFactory<T, S>(otherType, scopeClass, false) {
				DI.resolveClass(type, it)
			}
		}
	}
}
inline fun <reified S : Scope> scoped(inScope: InScope<S>.() -> Unit) {
	InScope<S>(S::class).inScope()
}
