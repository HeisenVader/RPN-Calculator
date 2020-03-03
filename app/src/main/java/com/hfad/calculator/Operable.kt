package com.hfad.calculator

/*для хранения данных двух типов(Double и Boolean) в одном стеке, было решено создать интерфейc Operable и два класса его реализующие
OpBoolean и OpDouble
 */
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