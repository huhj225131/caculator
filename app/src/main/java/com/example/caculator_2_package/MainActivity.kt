package com.example.caculator_2_package

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.Button
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var resultTV: TextView
    private lateinit var workingTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        resultTV = findViewById(R.id.resultTV)
        workingTV = findViewById(R.id.workingTV)
    }

    fun numberAction(view: View) {
        // Xử lý nếu ô kết quả đã có trước đó
        if (resultTV.text.isNotEmpty()){
            resultTV.text = ""
        }
        if(workingTV.text.isEmpty() || workingTV.text.last() != ')' ) {
            if (view is Button) {
                workingTV.append(view.text)
            }
        }
    }
    fun clearExpression(view: View){
        resultTV.text = ""
        workingTV.text = ""
    }
    fun clear(view: View){
        workingTV.text = ""
    }
    fun backSpace(view: View){
        val length = workingTV.length()
        if(length > 0){
            workingTV.text = workingTV.text.subSequence(0, length - 1)
        }
    }

    fun operationAction(view: View){
        //Xử lý khi muốn tính tiếp kết quả đã tính
        if (resultTV.text.isNotEmpty() && workingTV.text.isEmpty()) {
            if (resultTV.text[0] == '-'){
                val insert = "(${resultTV.text.toString()})"
                workingTV.text = insert
            }
            else {
                workingTV.text = resultTV.text
            }
            if (view is Button) {
                workingTV.append(view.text) // Thêm toán tử nếu hợp lệ
            }
        }

        //Xử lý tránh hai dấu liền nhau
        else if (workingTV.text.isNotEmpty()){
            val lastChar = workingTV.text.last() // Lấy ký tự cuối cùng
            if (lastChar != '+' && lastChar != '-' && lastChar != 'x' && lastChar != '/') {
                if (view is Button) {
                    workingTV.append(view.text) // Thêm toán tử nếu hợp lệ
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun reverseSign(view: View){
        val expression = workingTV.text.toString()
        if (expression.isEmpty()) return
        if (workingTV.text.last() == ')') return

        var endIndex = expression.length - 1
        var startIndex = endIndex
        val operators = setOf('+', '-', 'x', '/')
        while (startIndex >= 0 && !operators.contains(expression[startIndex])) {
            startIndex--
        }
        startIndex++
        // Nếu gặp rộng hoặc gặp ngay dấu
        if (startIndex > endIndex) return
        val lastNumber = expression.substring(startIndex, endIndex + 1)
        val reversedNumber = "(-$lastNumber)"
        this.workingTV.text = expression.substring(0, startIndex) + reversedNumber

    }
    fun equal(view: View){
        resultTV.text = caculate()
        workingTV.text = ""
    }
    private fun caculate(): String{
        val digits = digitList()
        val operators = operatorList()

        if (digits.isEmpty()) return ""
        if (operators.isEmpty()) return digits[0].toString()

        //Xu ly phep nhan
        val tempDigits = mutableListOf<Any>()
        val tempOperators = mutableListOf<Char>()
        tempDigits.add(digits[0])

        for (i in 0 until operators.size) {
            if (operators[i] == 'x') {
                val lastNum = tempDigits.removeAt(tempDigits.size - 1) as Int
                val nextNum = digits[i + 1] as Int
                tempDigits.add(lastNum * nextNum)
            } else if (operators[i] == '/') {
                val lastNum = tempDigits.removeAt(tempDigits.size - 1) as Int
                val nextNum = digits[i + 1] as Int
                if (nextNum == 0) return "Error"
                tempDigits.add(lastNum / nextNum)
            } else {
                tempDigits.add(digits[i + 1])
                tempOperators.add(operators[i])
            }
        }

        //Xu ly cong tru
        var result = tempDigits[0] as Int
        for (i in 0   until tempOperators.size) {
            if (tempOperators[i] == '+') {
                result = result + (tempDigits[i + 1] as Int)
            } else if (tempOperators[i] == '-') {
                result = result - (tempDigits[i + 1] as Int)
            }
        }

        return result.toString()

    }
    private fun operatorList(): MutableList<Char> {
        val list = mutableListOf<Char>()
        var insideParentheses = false

        for (character in workingTV.text) {
            if (character == '(') {
                insideParentheses = true
            } else if (character == ')') {
                insideParentheses = false
            } else if (!insideParentheses) {
                if (character == '+' || character == '-' || character == 'x' || character == '/') {
                    list.add(character)
                }
            }
        }
        return list
    }
    private fun digitList(): MutableList<Any>{
        val list = mutableListOf<Any>()
        var   digit = ""
        var negative = false
        for (character in workingTV.text){
            if(character.isDigit() ){
                digit += character
            }
             if(character == '('){
                 if (digit.isNotEmpty()){
                     list.add(digit.toInt())
                 }
                 digit = "-"
                 negative = true
            }
            if(character == 'x' || character == '+' || character == '/' || (character=='-' && negative == false )) {
                list.add(digit.toInt())
                digit = ""
            }
            if(character ==')'){
                negative= false
            }
        }
        if(digit != ""){
            list.add(digit.toInt())
        }
        else{
            list.add(0)
        }
        return list
    }
}