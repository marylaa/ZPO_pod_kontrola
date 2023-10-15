package com.example.myapp.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapp.R
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null
    private lateinit var dbRef: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        loginButton = findViewById(R.id.loginButton)

        loginButton?.setOnClickListener{ logInRegisteredUser() }

        auth = FirebaseAuth.getInstance()



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        findViewById<Button>(R.id.loginButtonGoogle).setOnClickListener{
            signInGoogle()
        }

        //////////////////////////////////////////////////////////////////////////////////////////
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.d("TOKEN", "Fetching FCM registration token failed")
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            Log.d("TOKEN POZNIEJ", token)
//            Toast.makeText(baseContext, "Twój token: " + token, Toast.LENGTH_SHORT).show()
//        })
        ///////////////////////////////////////////////////////////////////////////////////////////
    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.registerButton ->{
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            } else -> {
                showErrorSnackBar("Wprowadzono poprawnie dane",false)
                true
            }
        }
    }

    private fun logInRegisteredUser(){

        if(validateLoginDetails()){
            val email = inputEmail?.text.toString().trim(){ it<= ' '}
            val password = inputPassword?.text.toString().trim(){ it<= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->

                    if(task.isSuccessful){
                        showErrorSnackBar("Zalogowano pomyślnie",false)
                        goToNextActivity()
                    } else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }

    private fun goToNextActivity() {
        val user = FirebaseAuth.getInstance().currentUser;

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(user?.uid.toString())
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child("userType").value.toString().equals("Pacjent")) {
                    val intent = Intent(this@LoginActivity, UserScheduleActivity::class.java)
                    startActivity(intent)

                    val id = FirebaseAuth.getInstance().currentUser!!.uid
                    Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", id)
                } else {
                    val intent = Intent(this@LoginActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)

                    val id = FirebaseAuth.getInstance().currentUser!!.uid
                    Log.d("BBBBBBBBBBBBBBBBBBBB", id)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    private fun signInGoogle(){
        val signInINtent = googleSignInClient.signInIntent
        launcher.launch(signInINtent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        println("tutajjj 111")

        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account != null){
                println("tutajjj")
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val intent: Intent = Intent(this, UserScheduleActivity::class.java)
                startActivity(intent)

                // Dodaj Toast poniżej
                Toast.makeText(this, "Zalogowano pomyślnie przez konto Google", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}