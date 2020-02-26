package com.hfad.calculator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val mathEval: Evaluator = Evaluator()
    private var brackets: Int = 0
    private var mInputString: ArrayList<String> = arrayListOf()
    private val mTerms: ArrayList<String> = arrayListOf("+", "-", "*", "(", ")", "÷", ".", "‐")

    private fun textViewer(){
        if (mInputString.isEmpty())
            expression.text = "0"
        else
            expression.text = mInputString.joinToString("")
    }

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

    private fun textViewCleaner(){
        if(result.text != ""){
            result.text = ""
            mInputString.clear()
            brackets = 0
        }
    }

    private fun textViewSwitcher(){
        if(result.text != "" && result.text.length < 10){
            val st = result.text
            result.text = ""
            mInputString.clear()
            brackets = 0
            for(i in st.indices) {
                mInputString.add(st[i].toString())
            }
        }
    }

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

    private val longListener = View.OnLongClickListener {
        mInputString.clear()
        //mResultString.clear()
        brackets = 0
        textViewer()
        result.text = mInputString.joinToString("")
        return@OnLongClickListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null){
            brackets = savedInstanceState.get("brackets") as Int
            mInputString = savedInstanceState.get("input") as ArrayList<String>
            result.text = savedInstanceState.get("result") as String
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
        buttonEqually.setOnClickListener{
            if(mInputString.isNotEmpty()) {
                while(mInputString.isNotEmpty() && mTerms.contains(mInputString.last()) && mInputString.last() != ")") {
                    if (mInputString.last() == "(")
                        brackets++
                    mInputString.removeAt(mInputString.size - 1)
                }

                while(brackets < 0){
                    mInputString.add(")")
                    brackets++
                }
            }
            textViewer()
            if(mInputString.isNotEmpty()) {
                result.text = mathEval.evaluate(mInputString.joinToString("")).toString()
            }
            else
                result.text = mInputString.joinToString("")
        }
        buttonDel.setOnClickListener{
            if(mInputString.isNotEmpty()) {
                if((mInputString.last() == "." || mInputString.last() == "-") && mInputString.first() == "0" && mInputString.size == 2)
                    mInputString.clear()
            }
            if(mInputString.isNotEmpty()){
                if(mInputString.last() == "("){
                    brackets++
                    mInputString.removeAt(mInputString.size - 1)
                }
                else if(mInputString.last() == ")"){
                    brackets--
                    mInputString.removeAt(mInputString.size - 1)
                }
                else
                    mInputString.removeAt(mInputString.size - 1)
            }
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
        buttonMinus.setOnClickListener{
            textViewSwitcher()
            if(mInputString.isEmpty()){
                //mInputString.add("0")
                mInputString.add("‐")
            }
            else if(!mTerms.contains(mInputString.last()) || mInputString.last() == ")") {
                mInputString.add("-")
            }
            else if(mInputString.last() == "("){
                //mInputString.add("0")
                mInputString.add("‐")
            }
            else if(mTerms.contains(mInputString.last()) && mInputString.last() != ")" && mInputString.last() != "."){
                mInputString.add("(")
                mInputString.add("‐")
                brackets--
            }
            textViewer()
        }
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
        buttonBracketClose.setOnClickListener{
            if(mInputString.isNotEmpty()){
                if((!mTerms.contains(mInputString.last()) || mInputString.last() == ")") && brackets < 0) {
                    mInputString.add(")")
                    brackets++
                }
            }
            textViewer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("brackets", brackets)
        outState.putStringArrayList("input", mInputString)
        outState.putString("result", result.text.toString())
    }

}
