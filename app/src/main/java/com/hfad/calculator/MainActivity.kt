package com.hfad.calculator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val mathEval: Evaluator = Evaluator()
    private var brackets: Int = 0
    private var ternary = 0
    private var editing = true
    private var mInputString: ArrayList<String> = arrayListOf() //строка ввода пользователя
    private val mTerms: ArrayList<String> = arrayListOf("+", "-", "*", "(", ")", "÷", ".", "‐", "^", "<", "≤", ">", "≥", "?", ":")

    //метод для отображения ввода пользователя
    private fun textViewer(){
        if (mInputString.isEmpty())
            expression.text = "0"
        else
            expression.text = mInputString.joinToString("")
    }

    //метод добавляющий цифру, переданное ему, в строку ввода
    private fun numberAdder(st: String){
        if(mInputString.isEmpty())
            mInputString.add(st)
        else if(mInputString.isNotEmpty()){
            if(mInputString.last() != ")" && mInputString.last() != "0")
                mInputString.add(st)
            else if(mInputString.last() == "0"){
                for(i in mInputString.size-1 downTo 0){
                    if(mInputString[i] == ".") {
                        mInputString.add(st)
                        break
                    }
                    else if(!mTerms.contains(mInputString[i]) && mInputString[i] != "0") {
                        mInputString.add(st)
                        break
                    }
                    else if((mTerms.contains(mInputString[i]) || i == 0) && mInputString[i] != ")") {
                        mInputString.removeAt(mInputString.size - 1)
                        mInputString.add(st)
                        break
                    }
                }
            }
        }
    }

    //очищает поле воода и результата, а также входную строку, если пользователь вводит новое число
    private fun textViewCleaner(){
        if(result.text != "" && result.text.last().isDigit() && !editing){
            result.text = ""
            mInputString.clear()
            brackets = 0
        }
    }

    //очищает поле выражения и переносит в него результат, если пользователь вводит новую операцию
    private fun textViewSwitcher(){
        if(result.text != "" && result.text.last().isDigit() && !editing){
            val st = result.text
            result.text = ""
            mInputString.clear()
            brackets = 0
            for(i in st.indices) {
                mInputString.add(st[i].toString())
            }
        }
    }

    //слушатель для нажатия на цифры
    private val listener = View.OnClickListener{
        textViewCleaner()
        when (it.id){
            R.id.button0 -> numberAdder("0")
            R.id.button1 -> numberAdder("1")
            R.id.button2 -> numberAdder("2")
            R.id.button3 -> numberAdder("3")
            R.id.button4 -> numberAdder("4")
            R.id.button5 -> numberAdder("5")
            R.id.button6 -> numberAdder("6")
            R.id.button7 -> numberAdder("7")
            R.id.button8 -> numberAdder("8")
            R.id.button9 -> numberAdder("9")
        }
        textViewer()
    }

    //слушатель долгих нажатий для клавиши Del
    private val longListener = View.OnLongClickListener {
        mInputString.clear()
        brackets = 0
        editing = true
        ternary = 0
        textViewer()
        result.text = mInputString.joinToString("")
        return@OnLongClickListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //востановление информации при смене ориентации экрана
        if(savedInstanceState != null){
            brackets = savedInstanceState.getInt("brackets")
            mInputString = savedInstanceState.get("input") as ArrayList<String>
            result.text = savedInstanceState.getString("result")
            editing = savedInstanceState.getBoolean("editing")
            ternary = savedInstanceState.getInt("ternary")
            textViewer()
        }

        buttonDel.setOnLongClickListener(longListener)

        button0.setOnClickListener(listener)
        button1.setOnClickListener(listener)
        button2.setOnClickListener(listener)
        button3.setOnClickListener(listener)
        button4.setOnClickListener(listener)
        button5.setOnClickListener(listener)
        button6.setOnClickListener(listener)
        button7.setOnClickListener(listener)
        button8.setOnClickListener(listener)
        button9.setOnClickListener(listener)

        //слушатель "." не допускает добавление двух точек в одно число и добавление точек после операторов и скобок
        buttonDot.setOnClickListener{
            textViewCleaner()
            if(mInputString.isEmpty()){
                mInputString.add("0")
                mInputString.add(".")
            }
            else if(!mTerms.contains(mInputString.last())){
                for (i in mInputString.size-1 downTo 0){
                    if((mTerms.contains(mInputString[i]) && mInputString[i] != ".") || mInputString[i] == "."){
                        if(mTerms.contains(mInputString[i]) && mInputString[i] != "."){
                            mInputString.add(".")
                            break
                        }
                        if (mInputString[i] == ".")
                            break
                    }
                    if(i == 0)
                        mInputString.add(".")
                }
            }
            textViewer()
        }
        //слушатель "=" добавляет недостающие закрывающие скобки, удаляет оператор если он в конце строки, не пропускает незаконченные тернарные выражения
        buttonEqually.setOnClickListener{
            if(mInputString.isNotEmpty()) {
                if(mInputString.last() == ":")
                    mInputString.add("0")

                while(mInputString.isNotEmpty() && mTerms.contains(mInputString.last()) && mInputString.last() != ")") {
                    if (mInputString.last() == "(")
                        brackets++
                    else if(mInputString.last() == "?")
                        ternary++
                    mInputString.removeAt(mInputString.size - 1)
                }
                while(brackets < 0){
                    mInputString.add(")")
                    brackets++
                }
            }
            if (ternary != 0){
                result.text = "err"
            }
            else {
                editing = false
                textViewer()
                if (mInputString.isNotEmpty()) {
                    result.text = mathEval.evaluate(mInputString.joinToString(""))
                } else
                    result.text = mInputString.joinToString("")
            }
        }
        //слушатель "Del" следит за тем что удаляет
        buttonDel.setOnClickListener{
            if(mInputString.isNotEmpty()){
                when {
                    mInputString.last() == "?" -> {
                        ternary++
                        mInputString.removeAt(mInputString.size - 1)
                    }
                    mInputString.last() == ":" -> {
                        ternary--
                        mInputString.removeAt(mInputString.size - 1)
                    }
                    mInputString.last() == "(" -> {
                        brackets++
                        mInputString.removeAt(mInputString.size - 1)
                    }
                    mInputString.last() == ")" -> {
                        brackets--
                        mInputString.removeAt(mInputString.size - 1)
                    }
                    else -> mInputString.removeAt(mInputString.size - 1)
                }
            }
            editing = true
            textViewer()
        }

        buttonDivision.setOnClickListener{
            textViewSwitcher()
            if (mInputString.isNotEmpty()){
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")")
                    mInputString.add("÷")
            }
            textViewer()
        }
        buttonMultiplication.setOnClickListener{
            textViewSwitcher()
            if (mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")")
                    mInputString.add("*")
            textViewer()
        }
        buttonPlus.setOnClickListener{
            textViewSwitcher()
            if (mInputString.isNotEmpty())
                if (!mTerms.contains(mInputString.last()) || mInputString.last() == ")")
                    mInputString.add("+")
            textViewer()
        }
        //слушатель "-" в зависимости от последнего символа в строке ввода добавляет унарный(дефис) или бинарный(тире) минус
        buttonMinus.setOnClickListener{
            textViewSwitcher()
            if(mInputString.isEmpty()){
                mInputString.add("‐") // унарный минус
            }
            else if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                mInputString.add("-") // бинарный минус
            }
            else if(mInputString.last() == "("){
                mInputString.add("‐") // унарный минус
            }
            else if(mTerms.contains(mInputString.last()) && mInputString.last() != ")" && mInputString.last() != "."){
                mInputString.add("(")
                mInputString.add("‐") // унарный минус
                brackets--
            }
            textViewer()
        }
        //открывающая скобка не добавляется после открывающей скобки, точки, числа
        buttonBracketOpen.setOnClickListener{
            textViewCleaner()
            if(mInputString.isEmpty()) {
                mInputString.add("(")
                brackets--
            }
            else if(mInputString.isNotEmpty())
                if(mTerms.contains(mInputString.last()) && mInputString.last() != ")" && mInputString.last() != ".") {
                    mInputString.add("(")
                    brackets--
                }
            textViewer()
        }
        //закрывающая скобка не добавляется если ранее не было открывающих скобок или последний символ в строке ввода - оператор
        buttonBracketClose.setOnClickListener{
            if(mInputString.isNotEmpty()){
                if((!mTerms.contains(mInputString.last()) || mInputString.last() == ")") && brackets < 0) {
                    mInputString.add(")")
                    brackets++
                }
            }
            textViewer()
        }

        buttonPower?.setOnClickListener {
            textViewSwitcher()
            if (mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")")
                    mInputString.add("^")
            textViewer()
        }
        buttonLess?.setOnClickListener {
            textViewSwitcher()
            if(mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add("<")
                }
            textViewer()
        }
        buttonLessOrEq?.setOnClickListener {
            textViewSwitcher()
            if(mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add("≤")
                }
            textViewer()
        }
        buttonGreater?.setOnClickListener {
            textViewSwitcher()
            if(mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add(">")
                }
            textViewer()
        }
        buttonGreaterOrEq?.setOnClickListener {
            textViewSwitcher()
            if(mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add("≥")
                }
            textViewer()
        }
        buttonQuest?.setOnClickListener {
            if(mInputString.isNotEmpty())
                if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add("?")
                    ternary--
                }
            textViewer()
        }
        buttonOr?.setOnClickListener {
            if(mInputString.isNotEmpty() && ternary < 0) {
                if (!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                    mInputString.add(":")
                    ternary++
                }
                else{
                    mInputString.add("0")
                    mInputString.add(":")
                    ternary++
                }
            }
            textViewer()
        }
    }
    //сохранение данных при смене ориентации экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("brackets", brackets)
        outState.putStringArrayList("input", mInputString)
        outState.putString("result", result.text.toString())
        outState.putBoolean("editing", editing)
        outState.putInt("ternary", ternary)
    }

}
