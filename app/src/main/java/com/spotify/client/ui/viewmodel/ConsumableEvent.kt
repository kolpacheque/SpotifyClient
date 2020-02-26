package com.spotify.client.ui.viewmodel

import java.util.concurrent.atomic.AtomicBoolean

/**
 * A wrapper for data that is exposed via LiveData that represents an item
 */
class ConsumableItem<out T>(private val item: T?) {

    private val isConsumed = AtomicBoolean(false)

    /**
     * Returns the item and prevents its use again
     */
    fun get(): T? {
        return if (isConsumed.getAndSet(true)) {
            null
        } else {
            item
        }
    }

    /**
     * Peek on item without consuming it
     */
    fun peek(): T? = item

}