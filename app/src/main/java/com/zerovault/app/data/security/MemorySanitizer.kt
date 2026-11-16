package com.zerovault.app.data.security

import android.util.Log
import java.lang.ref.WeakReference
import java.util.Arrays
import java.util.concurrent.CopyOnWriteArrayList

/**
 * MemorySanitizer ensures zero-remanence security for sensitive buffers in RAM.
 * When a threat is detected or when files are closed, all registered buffers
 * and memory segments are immediately overwritten with zeros.
 */
object MemorySanitizer {
    private const val TAG = "MemorySanitizer"

    // Holds weak references to active sensitive byte/char allocations
    private val registeredByteArrays = CopyOnWriteArrayList<WeakReference<ByteArray>>()
    private val registeredCharArrays = CopyOnWriteArrayList<WeakReference<CharArray>>()

    /**
     * Register a sensitive byte array to be sanitized on threat lockdown or session close.
     */
    fun register(bytes: ByteArray): ByteArray {
        registeredByteArrays.add(WeakReference(bytes))
        return bytes
    }

    /**
     * Register a sensitive char array (e.g. entered PIN) to be sanitized.
     */
    fun register(chars: CharArray): CharArray {
        registeredCharArrays.add(WeakReference(chars))
        return chars
    }

    /**
     * Explicitly wipe a byte array immediately by overwriting with zeroes.
     */
    fun wipe(bytes: ByteArray?) {
        if (bytes != null) {
            Arrays.fill(bytes, 0.toByte())
        }
    }

    /**
     * Explicitly wipe a char array immediately by overwriting with '0'.
     */
    fun wipe(chars: CharArray?) {
        if (chars != null) {
            Arrays.fill(chars, '\u0000')
        }
    }

    /**
     * Emergency RAM Sanitization: Clears all tracked sensitive memory structures.
     * Invoked when System Threat Monitor detects DoS / Brute-Force threshold breach.
     */
    @Synchronized
    fun purgeAll(): Int {
        var wipedCount = 0

        val byteIter = registeredByteArrays.iterator()
        while (byteIter.hasNext()) {
            val ref = byteIter.next()
            val array = ref.get()
            if (array != null) {
                Arrays.fill(array, 0.toByte())
                wipedCount++
            }
        }
        registeredByteArrays.clear()

        val charIter = registeredCharArrays.iterator()
        while (charIter.hasNext()) {
            val ref = charIter.next()
            val array = ref.get()
            if (array != null) {
                Arrays.fill(array, '\u0000')
                wipedCount++
            }
        }
        registeredCharArrays.clear()

        // Request aggressive Garbage Collection to reclaim sanitized heap memory
        System.gc()
        Log.w(TAG, "EMERGENCY RAM PURGE COMPLETED: Sanitized $wipedCount sensitive memory buffers.")
        return wipedCount
    }
}
