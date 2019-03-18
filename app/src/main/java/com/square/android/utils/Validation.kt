package com.square.android.utils

interface ValidationCallback<T> {
    fun isValid(item: T) : Boolean

    fun validityChanged(isValid: Boolean)
}

interface Validatable<T> {
    fun onValueChanged(callback: () -> Unit)
    
    fun getValue() : T
}