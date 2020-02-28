package com.hfad.calculator

interface Operable

class OpBoolean (v: Boolean) : Operable {
    val value = v
    override fun toString(): String {
        return value.toString()
    }
}

class OpDouble (v: Double) : Operable {
    val value = v
    override fun toString(): String {
        return value.toString()
    }
}