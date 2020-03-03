package com.hfad.calculator

import java.util.*
import kotlin.math.pow

class Evaluator {
    //метод определяющий является ли переданный ему параметр оператором
    private fun isTerm(c: Char): Boolean {
        if (("+-*()÷‐^<≤>≥?:".indexOf(c)) != -1)
            return true
        return false
    }
    //возвращает приоритет оператора
    private fun priority(c: Char): Int{
        return when(c) {
            '(' -> 0
            ')' -> 1
            '?' -> 2
            ':' -> 3
            '<' -> 4
            '≤' -> 4
            '>' -> 4
            '≥' -> 4
            '+' -> 5
            '-' -> 6
            '*' -> 7
            '÷' -> 7
            '‐' -> 8 //Унарный минус
            '^' -> 9
            else -> 10
        }
    }
    //преобразует полученную строку в инфиксной нотации, в строку в обратной польской нотации
    private fun toRpn(input: String): String {
        var output = ""    //выходная строка
        val termStack: Stack<Char> = Stack()    //стек операторов
        var i = 0
        while (i < input.length){
            if(!isTerm(input[i])){  //если число, записываем полностью в выходную строку
                while (!isTerm(input[i])){
                    output += input[i]
                    i++
                    if (i == input.length) break
                }
                output += " "   //отделяем пробелом
                i--
            }
            if(isTerm(input[i])) {
                when {
                    input[i] == '(' -> termStack.push(input[i])
                    input[i] == ')' -> {    //если закрывающая скобка, то выталкиваем все операторы из стека в выходную строку, пока не встретим открывающую скобку
                        var s = termStack.pop()
                        while (s != '('){
                            output += "$s "
                            s = termStack.pop()
                        }
                    }
                    input[i] == ':' -> {
                        var s = termStack.pop()
                        while (s != '?'){
                            output += "$s "
                            s = termStack.pop()
                        }
                        termStack.push(s)
                    }
                    else -> {
                        /*если приоритет текущего оператора ниже либо равен(левая ассоциативность) приоритету последнего оператора в стеке,
                            * то выталкиваем последний оператор из стека в выходную строку, а в стек добавляем текущий оператор.
                            * Если текущий оператор "?", последний оператор из стка выталкивается в выходную строку только в случае равенства приоритетов(правая ассоциативность).
                            * Иначе, текущий оператор добавляется в стек операторов*/
                        if(termStack.count() > 0) {
                            while (termStack.count() > 0 && priority(input[i]) <= priority(termStack.peek())) {
                                output += if(priority(input[i]) < priority(termStack.peek()))
                                    "${termStack.pop()} "
                                else if(priority(input[i]) == priority(termStack.peek()) && input[i] != '?')
                                    "${termStack.pop()} "
                                else
                                    break
                            }
                        }
                        termStack.push(input[i])
                    }
                }
            }
            i++
        }
        //выталкиваем оставшиеся операторы из стека в выходную строку отделяя пробелами
        while (termStack.count() > 0)
            output += "${termStack.pop()} "
        return output
    }
    //метод получает в качестве параметра строку выражения в ОПН и вовзращает результат выражения в качестве строки
    private fun calculate(input: String): String {
        var result: Operable = OpDouble(0.0)
        val temp: Stack<Operable> = Stack()     //стек значений(Double и Boolean)

        var i = 0
        while (i < input.length) {
            //если число, записывается полностью в обертку и помещается в стек
            if (input[i].isDigit()){
                var a = ""
                while (!isTerm(input[i]) && (input[i] != ' ')){
                    a += input[i]
                    i++
                    if (i == input.length) break
                }
                temp.push(OpDouble(a.toDouble()))
                i--
            }
            /*если оператор, извлекает нужное количество чисел с вершины стека, выполняет операцию над ними,
            и возвращает результат обратно в стек. Попутно проверяя соответствие типов операндов.
             */
            else if (isTerm(input[i]) && input[i] != ':'){
                try {
                    when (input[i]) {
                        '+' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpDouble(b.value + a.value)
                            else
                                return "err"
                        }
                        '-' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpDouble(b.value - a.value)
                            else
                                return "err"
                        }
                        '*' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpDouble(b.value * a.value)
                            else
                                return "err"
                        }
                        '÷' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpDouble(b.value / a.value)
                            else
                                return "err"
                        }
                        '‐' -> {
                            val a = temp.pop()
                            if (a is OpDouble)
                                result = OpDouble(a.value * -1)
                            else
                                return "err"
                        }
                        '^' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpDouble(b.value.pow(a.value))
                            else
                                return "err"
                        }
                        '<' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpBoolean(b.value < a.value)
                            else
                                return "err"
                        }
                        '≤' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpBoolean(b.value <= a.value)
                            else
                                return "err"
                        }
                        '>' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpBoolean(b.value > a.value)
                            else
                                return "err"
                        }
                        '≥' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            if (a is OpDouble && b is OpDouble)
                                result = OpBoolean(b.value >= a.value)
                            else
                                return "err"
                        }
                        '?' -> {
                            val a = temp.pop()
                            val b = temp.pop()
                            val c = temp.pop()
                            if(c is OpBoolean) //нормальное состояние
                                result = if(c.value)
                                    b
                                else
                                    a
                            else
                                return "err"
                        }
                    }
                }
                catch (e: EmptyStackException){
                    return "err"
                }
                temp.push(result)
            }
            i++
        }
        return temp.peek().toString()
    }



    fun evaluate(input: String): String {
        val output = toRpn(input)
        return calculate(output)
    }
}