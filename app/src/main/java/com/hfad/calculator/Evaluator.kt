package com.hfad.calculator

import java.util.*
import kotlin.math.pow

class Evaluator {

    private fun isTerm(c: Char): Boolean {
        if (("+-*()÷‐^<≤>≥?:".indexOf(c)) != -1)
            return true
        return false
    }

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

    private fun toRpn(input: String): String {
        var output = ""
        val termStack: Stack<Char> = Stack()
        var i = 0
        while (i < input.length){
            if(!isTerm(input[i])){
                while (!isTerm(input[i])){
                    output += input[i]
                    i++
                    if (i == input.length) break
                }
                output += " "
                i--
            }
            if(isTerm(input[i])) {
                when {
                    input[i] == '(' -> termStack.push(input[i])
                    input[i] == ')' -> {
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
        while (termStack.count() > 0)
            output += "${termStack.pop()} "
        return output
    }

    private fun calculate(input: String): String {
        var result: Operable = OpDouble(0.0)
        val temp: Stack<Operable> = Stack()

        var i = 0
        while (i < input.length) {
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
                            if(b is OpDouble && a is OpDouble && c is OpBoolean) //нормальное состояние
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