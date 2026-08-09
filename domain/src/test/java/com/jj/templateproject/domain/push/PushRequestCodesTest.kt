package com.jj.templateproject.domain.push

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Android matches pending intents on everything except their extras, so two intents sharing a
 * request code are one intent wearing two labels. These pin the property that prevents it: within a
 * notification and across notifications, no two slots ever collide.
 */
class PushRequestCodesTest {

    @Test
    fun `a notification's tap and its buttons all get distinct codes`() {
        val notificationId = 2000
        val codes = buildList {
            add(PushRequestCodes.of(notificationId, PushRequestCodes.TAP))
            repeat(PushPayload.MAX_ACTIONS) { index ->
                add(PushRequestCodes.of(notificationId, PushRequestCodes.actionSlot(index)))
            }
        }

        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `two notifications never share a code, whatever slot each one uses`() {
        val first = (0 until PushRequestCodes.SLOTS_PER_NOTIFICATION)
            .map { PushRequestCodes.of(2000, it) }
        val second = (0 until PushRequestCodes.SLOTS_PER_NOTIFICATION)
            .map { PushRequestCodes.of(2001, it) }

        assertEquals(emptySet<Int>(), first.toSet() intersect second.toSet())
    }

    @Test
    fun `a notification reserves one slot per button plus one for the body tap`() {
        assertEquals(PushPayload.MAX_ACTIONS + 1, PushRequestCodes.SLOTS_PER_NOTIFICATION)
    }

    @Test
    fun `the first action sits after the body tap, not on it`() {
        assertEquals(PushRequestCodes.TAP + 1, PushRequestCodes.actionSlot(0))
    }
}
