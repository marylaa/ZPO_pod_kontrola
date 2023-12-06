package com.example.myapp.report

import java.text.DecimalFormat

class Value(internal val name: String, private val unit: String, private val id: Int, internal var input: Double) {

    fun getName(): String {
        return name
    }

    fun getUnit(): String {
        return unit
    }

    fun getId(): Int {
        return id
    }

    fun setInput(inputElem: Double) {
        this.input = inputElem
    }

    fun printVal(): String {
        return "$name $input $unit"
    }

//    override fun toString(): String {
//        return "Value(name='$name', unit='$unit', id=$id, input=$input)"
//    }

    fun getInputAsString(): String {
        // Konwertuj wartość liczbową na ciąg znaków
        return DecimalFormat("#.##").format(input)
    }
}
