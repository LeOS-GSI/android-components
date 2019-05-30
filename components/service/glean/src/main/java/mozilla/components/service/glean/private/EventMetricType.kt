/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.glean.private

import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import mozilla.components.service.glean.Dispatchers
import mozilla.components.service.glean.storages.EventsStorageEngine
import mozilla.components.service.glean.storages.RecordedEventData
import mozilla.components.support.base.log.logger.Logger

/**
 * An enum with no values for convenient use as the default set of extra keys
 * that an [EventMetricType] can accept.
 */
@Suppress("EmptyClassBlock")
enum class NoExtraKeys(val value: Int) {
    // deliberately empty
}

/**
 * This implements the developer facing API for recording events.
 *
 * Instances of this class type are automatically generated by the parsers at built time,
 * allowing developers to record events that were previously registered in the metrics.yaml file.
 *
 * The Events API only exposes the [record] method, which takes care of validating the input
 * data and making sure that limits are enforced.
 */
data class EventMetricType<ExtraKeysEnum : Enum<ExtraKeysEnum>>(
    override val disabled: Boolean,
    override val category: String,
    override val lifetime: Lifetime,
    override val name: String,
    override val sendInPings: List<String>,
    val allowedExtraKeys: List<String> = listOf()
) : CommonMetricData {

    private val logger = Logger("glean/EventMetricType")

    /**
     * Record an event by using the information provided by the instance of this class.
     *
     * @param extra optional. This is map, both keys and values need to be strings, keys are
     *              identifiers. This is used for events where additional richer context is needed.
     *              The maximum length for values is defined by [MAX_LENGTH_EXTRA_KEY_VALUE]
     */
    fun record(extra: Map<ExtraKeysEnum, String>? = null) {
        if (!shouldRecord(logger)) {
            return
        }

        // We capture the event time now, since we don't know when the async code below
        // might get executed.
        val monotonicElapsed = SystemClock.elapsedRealtime()

        val extraStrings = extra?.convertAllowedToStrings(allowedExtraKeys)

        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.launch {
            // Delegate storing the event to the storage engine.
            EventsStorageEngine.record(
                metricData = this@EventMetricType,
                monotonicElapsedMs = monotonicElapsed,
                extra = extraStrings
            )
        }
    }

    // Convert the extra key enums to strings before passing to the storage engine
    // There are two extra "keys" in play here:
    //   1. The Kotlin enumeration names, in CamelCase
    //   2. The keys sent in the ping, in snake_case
    // Here we need to get (2) to send in the ping.
    private fun Map<ExtraKeysEnum, String>.convertAllowedToStrings(allowedKeys: List<String>): Map<String, String>? =
        mapNotNull { (k, v) ->
            val stringKey = allowedKeys.getOrNull(k.ordinal)
            if (stringKey != null) {
                stringKey to v
            } else run {
                logger.debug("No string value for enum ${k.ordinal}")
                null
            }
        }.toMap()

    /**
     * Tests whether a value is stored for the metric for testing purposes only. This function will
     * attempt to await the last task (if any) writing to the the metric's storage engine before
     * returning a value.
     *
     * @param pingName represents the name of the ping to retrieve the metric for.  Defaults
     *                 to the either the first value in [defaultStorageDestinations] or the first
     *                 value in [sendInPings]
     * @return true if metric value exists, otherwise false
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun testHasValue(pingName: String = sendInPings.first()): Boolean {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.assertInTestingMode()

        val snapshot = EventsStorageEngine.getSnapshot(pingName, false) ?: return false
        return snapshot.any { event ->
            event.identifier == identifier
        }
    }

    /**
     * Returns the stored value for testing purposes only. This function will attempt to await the
     * last task (if any) writing to the the metric's storage engine before returning a value.
     *
     * @param pingName represents the name of the ping to retrieve the metric for.  Defaults
     *                 to the either the first value in [defaultStorageDestinations] or the first
     *                 value in [sendInPings]
     * @return value of the stored metric
     * @throws [NullPointerException] if no value is stored
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun testGetValue(pingName: String = sendInPings.first()): List<RecordedEventData> {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.assertInTestingMode()

        return EventsStorageEngine.getSnapshot(pingName, false)!!.filter { event ->
            event.identifier == identifier
        }
    }
}
