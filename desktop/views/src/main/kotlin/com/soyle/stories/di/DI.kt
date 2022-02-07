package com.soyle.stories.di

import tornadofx.Component
import tornadofx.FX
import tornadofx.Scope
import tornadofx.find
import java.util.logging.Logger
import kotlin.collections.set
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


private sealed class ResolutionStrategy<T : Any, S : Scope> {
    val registrationSource: StackTraceElement =
        Thread.currentThread().stackTrace.asSequence().drop(1)
            .dropWhile { it.fileName == "DI.kt" }
            .first()
}

private class SubScopeRouting<T : Any, S : Scope>(
    private val scopeType: KClass<out S>,
    private val router: S.() -> Scope
) : ResolutionStrategy<T, S>() {
    fun routedScope(scope: Scope): Scope {
        if (!scopeType.isInstance(scope)) {
            error(
                """
                Wrong scope type provided.
                    Received: ${scope::class.simpleName}
                    Expected ${scopeType.simpleName}
            """.trimIndent()
            )
        }
        @Suppress("UNCHECKED_CAST")
        return (scope as S).router()
    }
}

private class Singleton<T : Any, S : Scope>(
    private val scopeType: KClass<out S>,
    private val factory: S.() -> T,
) : ResolutionStrategy<T, S>() {
    fun createType(scope: Scope): T {
        if (!scopeType.isInstance(scope)) {
            error(
                """
                Wrong scope type provided.
                    Received: ${scope::class.simpleName}
                    Expected ${scopeType.simpleName}
            """.trimIndent()
            )
        }
        @Suppress("UNCHECKED_CAST")
        return (scope as S).factory()
    }
}

private class Factory<T : Any, S : Scope>(
    private val scopeType: KClass<out S>,
    private val factory: S.() -> T,
) : ResolutionStrategy<T, S>() {
    fun createType(scope: Scope): T {
        if (!scopeType.isInstance(scope)) {
            throw IllegalArgumentException(
                """
                Wrong scope type provided.
                    Received: ${scope::class.simpleName}
                    Expected ${scopeType.simpleName}
            """.trimIndent()
            )
        }
        @Suppress("UNCHECKED_CAST")
        return (scope as S).factory()
    }
}

@JvmInline
private value class TypedResolutionStrategies(
    private val strategies: MutableMap<KClass<*>, ResolutionStrategy<*, *>> = mutableMapOf()
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> strategyFor(type: KClass<in T>): ResolutionStrategy<T, *>? =
        strategies[type]?.let { it as ResolutionStrategy<T, *> }

    operator fun <T : Any> set(type: KClass<in T>, strategy: ResolutionStrategy<T, *>) {
        strategies[type] = strategy
    }

    fun hasStrategyFor(type: KClass<*>): Boolean = strategies.containsKey(type)
}

@JvmInline
value class InstantiatedObjects(private val types: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(type: KClass<in T>): T? = types[type]?.let { it as T }
    operator fun <T : Any> set(type: KClass<in T>, value: T) {
        types[type] = value
    }

    fun hasInstanceOf(type: KClass<*>): Boolean = types.containsKey(type)
}

private val resolutionStrategiesFor = mutableMapOf<KClass<out Scope>, TypedResolutionStrategies>()
private val instantiatedObjectsFor = mutableMapOf<Scope, InstantiatedObjects>()

private fun <T : Any> resolveDependency(type: KClass<T>, scope: Scope, visitedScopeRoutes: Set<Scope>): T {
    if (scope in visitedScopeRoutes) circularScopeRoute(type, visitedScopeRoutes)
    val resolutionStrategies = resolutionStrategiesFor[scope::class]
    val instantiatedObjects = instantiatedObjectsFor.getOrPut(scope, ::InstantiatedObjects)

    val instantiatedObject: T = when (val resolutionStrategy = resolutionStrategies?.strategyFor(type)) {
        is SubScopeRouting<T, *> -> resolveDependency(
            type,
            resolutionStrategy.routedScope(scope),
            visitedScopeRoutes + scope
        )
        is Singleton<T, *> -> instantiatedObjects[type] ?: resolutionStrategy.createType(scope)
        is Factory<T, *> -> return resolutionStrategy.createType(scope)
        else -> tryDefaultInstantiation(type, scope) ?: cannotInstantiateType(type, scope)
    }

    instantiatedObjects[type] = instantiatedObject
    instantiatedObjectsFor[scope] = instantiatedObjects

    return instantiatedObject
}

private fun <T : Component> deferToFX(type: KClass<T>, scope: Scope): T = find(type, scope)

private fun circularScopeRoute(type: KClass<*>, visitedScopeRoutes: Set<Scope>): Nothing {
    throw Throwable(
        """
        Circular Scope Route detected when resolving ${type.simpleName}
          scopes visited: $visitedScopeRoutes
    """.trimIndent()
    )
}

private fun <T : Any> tryDefaultInstantiation(type: KClass<T>, scope: Scope): T? {
    if (Component::class.java.isAssignableFrom(type.java)) {
        @Suppress("UNCHECKED_CAST")
        return deferToFX(type as KClass<Component>, scope) as? T
    }
    return null/* try {
        type.createInstance()
    } catch (t: Throwable) {
        null
    }*/
}

private fun cannotInstantiateType(type: KClass<*>, scope: Scope): Nothing {
    throw Throwable(
        """
        Could not find valid resolution strategy for $type in $scope
    """.trimIndent()
    )
}

private fun <S : Scope, T : Any> register(
    scopeType: KClass<out Scope>,
    type: KClass<T>,
    strategy: ResolutionStrategy<T, S>
) {
    val resolutionStrategies = resolutionStrategiesFor.getOrPut(scopeType, ::TypedResolutionStrategies)
    if (resolutionStrategies.hasStrategyFor(type)) {
        val existing = resolutionStrategies.strategyFor(type)!!
        if (existing.registrationSource == strategy.registrationSource)
            error("Should not create duplicate entries for same type ${existing.registrationSource}")
        Logger.getGlobal().warning(
            """
            Overriding strategy for ${type}
                Existing: $existing defined at ${existing.registrationSource}
                Overriding With: $strategy defined at ${strategy.registrationSource}
        """.trimIndent()
        )
    }
    resolutionStrategies[type] = strategy
}

fun <S : Scope, T : Any> registerFactory(
    scopeType: KClass<S>,
    type: KClass<T>,
    createType: S.() -> T
) {
    @Suppress("UNCHECKED_CAST")
    register(scopeType, type, Factory(scopeType, createType as Scope.() -> T))
}

fun <S : Scope, T : Any> registerSingleton(
    scopeType: KClass<S>,
    type: KClass<T>,
    createType: S.() -> T
) {
    @Suppress("UNCHECKED_CAST")
    register(scopeType, type, Singleton(scopeType, createType as Scope.() -> T))
}

fun <S : Scope, T : Any> registerRoute(
    scopeType: KClass<S>,
    type: KClass<T>,
    hoistScope: S.() -> Scope
) {
    @Suppress("UNCHECKED_CAST")
    register(scopeType, type, SubScopeRouting(scopeType, hoistScope))
}

object DI {

    fun getRegisteredTypes(scope: Scope = FX.defaultScope): InstantiatedObjects {
        return instantiatedObjectsFor[scope] ?: InstantiatedObjects()
    }

    val activeScopes: List<Scope>
        get() = instantiatedObjectsFor.keys.toList()

    fun deregister(scope: Scope) {
        instantiatedObjectsFor.remove(scope)
    }

    fun isScopeRegistered(scope: Scope) = instantiatedObjectsFor.containsKey(scope)

    inline fun <reified T : Any> Scope.get() = resolve(T::class, this)
    inline fun <reified T : Any> resolve(scope: Scope = FX.defaultScope) = resolve(T::class, scope)
    inline fun <reified T : Any> resolveLater(scope: Scope = FX.defaultScope) = lazy { resolve(T::class, scope) }

    fun <T : Any> resolve(type: KClass<T>, scope: Scope): T {
        val resolutionFailure = Error("Failed to resolve ${type.simpleName}")
        return try {
            resolveDependency(type, scope, emptySet())
        } catch (t: Throwable) {
            resolutionFailure.initCause(t)
            throw resolutionFailure
        }
    }

}

inline fun <reified T : Any> Scope.get() = DI.resolve(T::class, this)
inline fun <reified T : Any> Component.resolve(scope: Scope = this.scope) = DI.resolve(T::class, scope)
inline fun <reified T : Any> Component.resolveLater(scope: Scope = this.scope) = lazy { DI.resolve(T::class, scope) }

class InScope<S : Scope>(private val scopeClass: KClass<S>) {

    inline fun <reified T : Any> resolveLater(): Lazy<T> {
        return DI.resolveLater<T>()
    }

    inline fun <reified T : Any> provide(vararg types: KClass<in T>, noinline factory: S.() -> T) =
        provide(T::class, types, factory)

    fun <T : Any> provide(type: KClass<T>, types: Array<out KClass<in T>>, factory: S.() -> T) {
        val uniqueTypes = types.toSet() - type
        registerSingleton(scopeClass, type, factory)
        uniqueTypes.forEach { otherType ->
            registerSingleton(scopeClass, otherType) {
                DI.resolve(type, this)
            }
        }
    }

    inline fun <reified T : Any> hoist(noinline extractor: S.() -> Scope) = hoist(T::class, extractor)
    fun <T : Any> hoist(type: KClass<T>, extract: S.() -> Scope) {
        registerRoute(scopeClass, type, extract)
    }

    inline fun <reified T : Any> factory(vararg types: KClass<in T>, noinline factory: S.() -> T) =
        factory(T::class, types, factory)

    fun <T : Any> factory(type: KClass<T>, types: Array<out KClass<in T>>, factory: S.() -> T) {
        val uniqueTypes = types.toSet() - type
        registerFactory(scopeClass, type, factory)
        uniqueTypes.forEach { otherType ->
            registerFactory(scopeClass, otherType) {
                DI.resolve(type, this)
            }
        }
    }
}

inline fun <reified S : Scope> scoped(inScope: InScope<S>.() -> Unit) {
    InScope<S>(S::class).inScope()
}
