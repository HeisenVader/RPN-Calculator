package com.hfad.calculator

interface Operable

class OpBoolean (val value: Boolean) : Operable {
    override fun toString(): String {
        return value.toString()
    }
}

class OpDouble (val value: Double) : Operable {
    override fun toString(): String {
        return value.toString()
    }
}