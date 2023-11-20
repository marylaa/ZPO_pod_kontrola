package com.example.myapp.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.myapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private var registerButton: Button? = null
    private var inputEmail: EditText? = null
    private var inputPesel: EditText? = null
    private var inputFirstName: EditText? = null
    private var inputLastName: EditText? = null
    private var inputPassword: EditText? = null
    private var inputRepPass: EditText? = null

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val spinnerUser = findViewById<Spinner>(R.id.spinnerUser)
        spinnerUser.onItemSelectedListener = this

        registerButton = findViewById(R.id.registerButton)
        inputEmail = findViewById(R.id.inputEmaill)
        inputPesel = findViewById(R.id.inputPesel)
        inputFirstName = findViewById(R.id.inputFirstName)
        inputLastName = findViewById(R.id.inputLastName)
        inputPassword = findViewById(R.id.inputPasswordd)
        inputRepPass = findViewById(R.id.inputPassworddRepeat)

        registerButton?.setOnClickListener{
            //validateRegisterDetails()
            registerUser()
        }
    }


    private fun validateRegisterDetails(): Boolean {
        val spinnerUser = findViewById<Spinner>(R.id.spinnerUser)
        val selectedUser = spinnerUser.selectedItem as String

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(inputPesel?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_pesel),true)
                false
            }
            (selectedUser == "Lekarz" && inputPesel?.text.toString().length !== 11) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_wrong_pesel),true)
                false
            }
            TextUtils.isEmpty(inputFirstName?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(inputLastName?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }
            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            TextUtils.isEmpty(inputRepPass?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword),true)
                false
            }
            inputPassword?.text.toString().trim {it <= ' '} != inputRepPass?.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch),true)
                false
            } else -> {
                //showErrorSnackBar("Your details are valid",false)
                true
            }
        }
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser(){
        if (validateRegisterDetails()){
            val login: String = inputEmail?.text.toString().trim() {it <= ' '}
            val password: String = inputPassword?.text.toString().trim() {it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login,password).addOnCompleteListener(
                OnCompleteListener <AuthResult>{ task ->
                    if(task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}",false)

                        saveUser(firebaseUser)

                        FirebaseAuth.getInstance().signOut()
                        finish()
                    } else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                })
        }
    }

    private fun saveUser(firebaseUser: FirebaseUser) {
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val firstName = inputFirstName?.text.toString().trim() {it <= ' '}
        val lastName = inputLastName?.text.toString().trim() {it <= ' '}
        val email = inputEmail?.text.toString().trim() {it <= ' '}
        val pesel = inputPesel?.text.toString().trim() {it <= ' '}

        val spinnerUser = findViewById<Spinner>(R.id.spinnerUser)
        val selectedUser = spinnerUser.selectedItem as String

        val newUser = UserModel(email, pesel, firstName, firebaseUser.uid, lastName, selectedUser)

        dbRef.child(firebaseUser.uid).setValue(newUser)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedUserType = parent?.getItemAtPosition(position) as String
        if (selectedUserType == "Lekarz") {
            inputPesel?.visibility = View.GONE
        } else {
            inputPesel?.visibility = View.VISIBLE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }
}