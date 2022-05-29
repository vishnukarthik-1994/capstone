package com.example.dfu_app

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.dfu_app.ui.error_message.ErrorMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth = Firebase.auth
    private val users = Firebase.firestore.collection("users")
    private lateinit var user:String
    private val navIntent = Intent()
    private var signOut = false
    private var back:Boolean = false
    private var allUsers = mutableSetOf<String>()
    override fun onStart() {
        super.onStart()
        back = intent.getBooleanExtra("back",false)
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser?.email
        if (currentUser != null && !back && allUsers.contains(currentUser.toString())){
            navIntent.setClass(this, MainActivity::class.java)
            startActivity(navIntent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Check if user is signed in (non-null) and update UI accordingly.
        // Get all users from db
        getUsers()
        //listen return result from launched intent
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
                else{
                    ErrorMessage.setErrorMessage(applicationContext,getString(R.string.internet_unavailable))
                }
            }
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Instantiate google sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val btnSignIn = findViewById<SignInButton>(R.id.sign_in_button)
        btnSignIn.setOnClickListener {signIn()}
        //Detect sign out action
        signOut = intent.getBooleanExtra("signOut",false)
        if (signOut){
            signOut()
        }
    }
    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient!!.signOut()
    }
    private fun signIn(){
        val signInIntent = mGoogleSignInClient!!.signInIntent
        resultLauncher.launch(signInIntent)
    }
    private fun getUsers(){
        val docs = users.get()
        while (!docs.isComplete) {}
        for (doc in docs.result) {
            allUsers.add(doc.get("user").toString())
        }
    }
    //Connect to firebase google authentication
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    user = auth.currentUser!!.email!!
                    if(user in allUsers) {
                        navIntent.setClass(this, MainActivity::class.java)
                        navIntent.putExtra("user", user)
                        startActivity(navIntent)
                    }
                    else{
                        navIntent.setClass(this, SignUpActivity::class.java)
                        navIntent.putExtra("user",user)
                        startActivity(navIntent)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    ErrorMessage.setErrorMessage(this.baseContext, "Login error")
                }
            }
    }
}

