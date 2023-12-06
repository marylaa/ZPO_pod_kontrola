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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

open class LoginActivity : BaseActivity(), View.OnClickListener {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null
    private var loginButtonFacebook: LoginButton? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth1: FirebaseAuth

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        loginButton = findViewById(R.id.loginButton)
        loginButtonFacebook = findViewById(R.id.facebook_login_button)

        loginButton?.setOnClickListener{ logInRegisteredUser() }
        loginButtonFacebook?.setOnClickListener{ logInWithFacebook() }
        callbackManager = CallbackManager.Factory.create()

        auth1 = FirebaseAuth.getInstance()



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        findViewById<SignInButton>(R.id.loginButtonGoogle).setOnClickListener{
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
        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
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

    private fun logInWithFacebook() {
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        loginButtonFacebook?.setReadPermissions("email", "public_profile")
        loginButtonFacebook?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("cancel", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.e("error", "facebook:onError", error)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val displayName = user?.displayName
                    val email = user?.email
                    // Dodaj inne informacje, które są dostępne po logowaniu
                    Toast.makeText(baseContext, "Authentication succeeded. User: $displayName, Email: $email", Toast.LENGTH_SHORT).show()
                    saveUser(user)
                    goToNextActivity()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun goToNextActivity() {
        val user = auth.currentUser

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(user?.uid.toString())
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child("userType").value.toString().equals("Pacjent")) {
                    val intent = Intent(this@LoginActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LoginActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
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
        auth1.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth1.currentUser
                val displayName = user?.displayName
                val email = user?.email
                // Dodaj inne informacje, które są dostępne po logowaniu
                Toast.makeText(baseContext, "Authentication succeeded. User: $displayName, Email: $email", Toast.LENGTH_SHORT).show()
                saveUser(user)
                val intent: Intent = Intent(this, UserScheduleActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveUser(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("Users")

            // Pobierz dane użytkownika
            val email = firebaseUser.email
            val uid = firebaseUser.uid
            val displayName = firebaseUser.displayName
            var firstName = ""
            var lastName = ""

            if (!displayName.isNullOrBlank()) {
                val nameParts = displayName.split(" ")

                // Sprawdź, czy udało się podzielić na imię i nazwisko
                if (nameParts.size >= 2) {
                    firstName = nameParts[0]
                    lastName = nameParts.subList(1, nameParts.size).joinToString(" ")
                }
            }



            val newUser = UserModel(email!!,"",firstName, uid, lastName,"Pacjent" )

            dbRef.child(uid).setValue(newUser)
        }
    }

}