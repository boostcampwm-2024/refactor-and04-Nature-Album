package com.and04.naturealbum.ui

import kotlinx.serialization.Serializable

interface Destination

@Serializable
sealed class NavigateDestination(val route: String) : Destination {
    @Serializable
    data object Home : NavigateDestination("home")

    @Serializable
    data object SavePhoto : NavigateDestination("save_photo")

    @Serializable
    data object Album : NavigateDestination("album")

    @Serializable
    data object SearchLabel : NavigateDestination("search_label")

    @Serializable
    data object AlbumFolder : NavigateDestination("album_folder")
}
