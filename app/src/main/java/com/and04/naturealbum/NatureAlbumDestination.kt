package com.and04.naturealbum

import kotlinx.serialization.Serializable

interface Destination

@Serializable
sealed class NavigateDestination(val route: String) : Destination {
    @Serializable
    data object Home : NavigateDestination("home")
}