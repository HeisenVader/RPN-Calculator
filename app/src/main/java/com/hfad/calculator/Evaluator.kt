package com.hfad.calculator

import java.util.*

class Evaluator {

    private fun isTerm(c: Char): Boolean {
        if (("+-*()÷‐".indexOf(c)) != -1)
            return true
        return false
    }

    private fun priority(c: Char): Int{
        return when(c) {
            '(' -> 0
            ')' -> 1
            '+' -> 2
            '-' -> 3
            '*' -> 4
            '÷' -> 4
            '‐' -> 5 //Унарный минус
            else -> 6
        }
    }

    private fun toRpn(input: String): String {
        var output = ""
        val termStack: Stack<Char> = Stack()
        var i = 0
        while (i < input.length){
            if(input[i].isDigit()){
                while (!isTerm(input[i])){
                    output += input[i]
                    i++
                    if (i == input.length) break
                }
                output += " "
                i--
            }
            if(isTerm(input[i])){
                if(input[i] == '(')
                    termStack.push(input[i])
                else if (input[i] == ')'){
                    var s = termStack.pop()
                    while (s != '('){
                        output += "$s "
                        s = termStack.pop()
                    }
                }
                else{
                    if(termStack.count() > 0)
                        if (priority(input[i]) <= priority(termStack.peek()))
                            output += "${termStack.pop()} "
                    termStack.push(input[i])
                }
            }
            i++
        }
        while (termStack.count() > 0)
            output += "${termStack.pop()} "
        return output
    }

    private fun calculate(input: String): Double {
        var result = 0.0
        val temp: Stack<Double> = Stack()

        var i = 0
        while (i < input.length) {
            if (input[i].isDigit()){
                var a = ""
                while (!isTerm(input[i]) && (input[i] != ' ')){
                    a += input[i]
                    i++
                    if (i == input.length) break
                }
                temp.push(a.toDouble())
                i--
            }
            else if (isTerm(input[i])){
                when(input[i]){
                    '+' -> {
                        val a = temp.pop()
                        val b = temp.pop()
                        result = b + a
                    }
                    '-' -> {
                        val a = temp.pop()
                        val b = temp.pop()
                        result = b - a
                    }
                    '*' -> {
                        val a = temp.pop()
                        val b = temp.pop()
                        result = b * a
                    }
                    '÷' -> {
                        val a = temp.pop()
                        val b = temp.pop()
                        result = b / a
                    }
                    '‐' -> {
                        val a = temp.pop()
                        result = a * -1
                    }
                }
                temp.push(result)
            }
            i++
        }
        return temp.peek()
    }



    fun evaluate(input: String): Double {
        val output = toRpn(input)
        return calculate(output)
    }
}