package com.jj.templateproject.domain.push

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The payload arrives as remote strings and is parsed inside a background service, so every test
 * here is really the same assertion: bad input is dropped, never thrown on.
 */
class PushPayloadTest {

    @Test
    fun `parses a full campaign with three action buttons`() {
        val message = PushPayload.parse(
            mapOf(
                "title" to "New version",
                "body" to "It is faster and fixes the export bug.",
                "tap" to "settings",
                "campaign" to "v2_launch",
                "slot" to "3",
                "action1_label" to "Open settings",
                "action1_target" to "settings",
                "action2_label" to "Not now",
                "action2_target" to "home",
                "action3_label" to "Companion app",
                "action3_target" to "play:com.example.companion",
            ),
        )

        requireNotNull(message)
        assertEquals("New version", message.title)
        assertEquals(PushDestination.Settings, message.tap)
        assertEquals("v2_launch", message.campaign)
        assertEquals(3, message.notificationSlot)
        assertEquals(
            listOf(
                PushAction("Open settings", PushDestination.Settings),
                PushAction("Not now", PushDestination.Home),
                PushAction("Companion app", PushDestination.PlayStoreApp("com.example.companion")),
            ),
            message.actions,
        )
    }

    @Test
    fun `a message with no title is not displayable and yields null`() {
        assertNull(PushPayload.parse(mapOf("body" to "Body without a title")))
    }

    @Test
    fun `a message with no body is not displayable and yields null`() {
        assertNull(PushPayload.parse(mapOf("title" to "Title without a body")))
    }

    @Test
    fun `whitespace-only text counts as absent rather than as a blank notification`() {
        assertNull(PushPayload.parse(mapOf("title" to "   ", "body" to "Something")))
    }

    @Test
    fun `an empty payload yields null instead of throwing`() {
        assertNull(PushPayload.parse(emptyMap()))
    }

    @Test
    fun `a message naming no destination taps through to home`() {
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There"))

        assertEquals(PushDestination.Home, message?.tap)
    }

    @Test
    fun `an unknown tap destination falls back to home rather than dropping the message`() {
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There", "tap" to "nowhere"))

        assertEquals(PushDestination.Home, message?.tap)
    }

    @Test
    fun `an action needs both a label and a valid target`() {
        val message = PushPayload.parse(
            mapOf(
                "title" to "Hi",
                "body" to "There",
                "action1_label" to "No target",
                "action2_target" to "home",
                "action3_label" to "Bad target",
                "action3_target" to "not-a-destination",
            ),
        )

        assertTrue(message!!.actions.isEmpty())
    }

    @Test
    fun `actions past the third are ignored, because Android renders at most three`() {
        val data = mutableMapOf("title" to "Hi", "body" to "There")
        repeat(5) { index ->
            data["action${index + 1}_label"] = "Button ${index + 1}"
            data["action${index + 1}_target"] = "home"
        }

        assertEquals(PushPayload.MAX_ACTIONS, PushPayload.parse(data)!!.actions.size)
    }

    @Test
    fun `a slot beyond the ceiling is clamped, not trusted`() {
        // The slot is multiplied out into pending-intent request codes, so an arbitrary remote
        // number must never reach that arithmetic.
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There", "slot" to "100000"))

        assertEquals(PushPayload.MAX_SLOT, message?.notificationSlot)
    }

    @Test
    fun `a negative slot is clamped to zero`() {
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There", "slot" to "-5"))

        assertEquals(0, message?.notificationSlot)
    }

    @Test
    fun `a non-numeric slot falls back to the default rather than failing the message`() {
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There", "slot" to "later"))

        assertEquals(0, message?.notificationSlot)
    }

    @Test
    fun `an empty campaign name reads as no campaign`() {
        val message = PushPayload.parse(mapOf("title" to "Hi", "body" to "There", "campaign" to "  "))

        assertNull(message?.campaign)
    }
}
