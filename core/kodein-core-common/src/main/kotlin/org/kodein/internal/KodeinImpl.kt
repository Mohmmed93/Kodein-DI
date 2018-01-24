package org.kodein.internal

import org.kodein.DKodein
import org.kodein.Kodein
import org.kodein.KodeinContainer
import org.kodein.bindings.BindingKodein
import org.kodein.direct

/**
 * Kodein implementation.
 *
 * Contains almost nothing because the Kodein object itself contains very few logic.
 * Everything is delegated wither to [container].
 */
internal open class KodeinImpl internal constructor(private val _container: KodeinContainer) : Kodein {

    /**
     * Creates a Kodein object with a [Kodein.Builder]'s internal.
     *
     * - Uses the [KodeinContainer.Builder] to create the [container].
     * - Calls all callbacks registered in [Kodein.Builder.callbacks].
     *
     * @param builder The builder to use.
     */
    @Suppress("unused")
    private constructor(builder: Kodein.MainBuilder, runCallbacks: Boolean) : this(KodeinContainerImpl(builder.containerBuilder, builder.externalSource, runCallbacks))

    /**
     * "Main" constructor.
     */
    constructor(allowSilentOverride: Boolean = false, init: Kodein.MainBuilder.() -> Unit) : this(_newBuilder(allowSilentOverride, init), true)

    companion object {
        private fun _newBuilder(allowSilentOverride: Boolean = false, init: Kodein.MainBuilder.() -> Unit) = Kodein.MainBuilder(allowSilentOverride).apply(init)

        fun withDelayedCallbacks(allowSilentOverride: Boolean = false, init: Kodein.MainBuilder.() -> Unit): Pair<Kodein, () -> Unit> {
            val kodein = KodeinImpl(_newBuilder(allowSilentOverride, init), false)
            return kodein to { kodein._container.initCallbacks?.invoke() ; Unit }
        }
    }

    final override val container: KodeinContainer by lazy {
        if (_container.initCallbacks != null)
            throw IllegalStateException("Kodein has not been initialized")
        _container
    }

}

@Suppress("UNCHECKED_CAST")
internal open class BindingKodeinImpl<out C, out A, out T: Any> internal constructor(
        override val dkodein: DKodein,
        private val _key: Kodein.Key<C, A, T>,
        override val context: C,
        override val receiver: Any?,
        private val _overrideLevel: Int
) : DKodein by dkodein, BindingKodein<C> {
    override fun overriddenFactory(): (Any?) -> Any = container.factory(_key, context, receiver, _overrideLevel + 1) as (Any?) -> Any
    override fun overriddenFactoryOrNull(): ((Any?) -> Any)? = container.factoryOrNull(_key, context, receiver, _overrideLevel + 1) as ((Any?) -> Any)?
}
