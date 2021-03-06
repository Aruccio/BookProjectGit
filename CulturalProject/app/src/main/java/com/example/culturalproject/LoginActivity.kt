package com.example.culturalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.culturalproject.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception



class LoginActivity : AppCompatActivity() {

    private lateinit var binding :com.example.culturalproject.databinding.ActivityLoginBinding
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var firebaseAuth :FirebaseAuth

    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance()


        //Google SignIn Burron, click to begin
        binding.bSignIn.setOnClickListener {
            //begin SignIn Google
            Log.d(TAG, "onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun checkUser() {
        val firebaseUser=firebaseAuth.currentUser;
        if(firebaseUser!=null)
        {
        //start profile activity
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SIGN_IN)
        {
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                //Google SignInSucces, now auth
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
                checkUser()
            }catch (e:Exception)
            {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                authResult ->
                //login succes
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")
                //get loggedIn user
                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: ${email}")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: ${uid}")

                //check if user is new or existing
                if(authResult.additionalUserInfo!!.isNewUser)
                {
                    //user is new - Account created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created for ${email}")
                    Toast.makeText(this@LoginActivity, "Account created for ${email}", Toast.LENGTH_SHORT).show()

                }else{
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user: ${email}")
                    Toast.makeText(this@LoginActivity, "Existing user: ${email}", Toast.LENGTH_SHORT).show()

                }

            }
            .addOnFailureListener { e->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Loggin failed due to ${e.message}")
                Toast.makeText(this@LoginActivity, "Loggin failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }


}