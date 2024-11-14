package org.grating.recolldroid.ui.data

/**
 * Try to add some helper functions to make the very basic Protobuff API easier to use.
 */

fun RecollDroidSettings.Builder.removePastSearchAt(idx: Int): RecollDroidSettings.Builder {
    val searches = pastSearchList.toMutableList()
    searches.removeAt(idx)
    clearPastSearch().addAllPastSearch(searches)
    return this
}