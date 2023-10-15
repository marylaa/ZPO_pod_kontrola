package com.example.myapp.report

class Value(internal val name: String, private val unit: String, private val id: Int, internal var input: String) {

    fun getName(): String {
        return name
    }

    fun getUnit(): String {
        return unit
    }

    fun getId(): Int {
        return id
    }

    fun setInput(inputElem: String) {
        this.input =  inputElem
    }

    fun printVal(): String{
        return this.name + " " + this.input + " " + this.unit
    }

    override fun toString(): String {
        return "Value(name='$name', unit='$unit', id=$id, input='$input')"
    }

    fun getInput(): String {
        return input

    }
}
