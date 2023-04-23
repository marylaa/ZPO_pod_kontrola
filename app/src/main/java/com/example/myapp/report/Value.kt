package com.example.myapp.report

//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.encodeToString

//@Serializable
class Value(private val name: String, private val unit: String,private val id: Int,private var input: String) {
//    var this_name: String = name
//        // Custom Getter
//        get() {
//            return field
//        }
//    var this_id: Int = id
//        get() = id
//
//    var this_unit: String = unit
//        // Custom Getter
//        get() {
//            return field
//        }

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

//    fun toJson(): String{
//        val json = Json.encodeToString(this)
//        return json
//    }

    fun getInput(): String {
        return input

    }


}
