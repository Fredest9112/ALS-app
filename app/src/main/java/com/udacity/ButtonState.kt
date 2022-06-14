package com.udacity


sealed class ButtonState {
    object Pending : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}