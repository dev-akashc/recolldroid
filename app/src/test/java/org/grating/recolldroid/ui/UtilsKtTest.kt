package org.grating.recolldroid.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsKtTest {

    @Test
    fun getQueryFragment() {
        var txt = "Thanks for the answer, but apparently this is not what I need. I want to get " +
                "the offset position by clicking a TextField (or an OutlinedTextField, for example)."

        assertEquals("what", txt.getQueryFragment(53))
        assertEquals("(or", txt.getQueryFragment(123))
        assertEquals("", txt.getQueryFragment(87))
        assertEquals("Thanks", txt.getQueryFragment(0))
        assertEquals("example).", txt.getQueryFragment(txt.length-1))

        txt = ""
        assertEquals("", txt.getQueryFragment(0))
    }
}