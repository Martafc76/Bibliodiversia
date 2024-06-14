package com.example.practicaevaluable

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.practicaevaluable.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Verifica si hay un usuario autenticado al iniciar la actividad
        val currentUser = auth.currentUser
        if (currentUser != null && sharedPreferences.getBoolean("is_logged_in", false)) {
            openMainActivity()
            return
        }

        setListeners()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            if (checkFields()) {
                basicLogin()
            }
        }

        binding.btnGoogle.setOnClickListener {
            googleLogin()
        }

        binding.tvNoCuenta.setOnClickListener {
            openRegistrationActivity()
        }
    }


    private fun googleLogin() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInClient.signOut()

        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        openMainActivity()
                    } else {
                        sendEmailVerification()
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Verification email sent to ${user.email}.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(
                        this,
                        "Por favor, verifique su email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private var email=""
    private var password=""

    private fun basicLogin() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            saveSessionState(true) // Guardar sesión como iniciada
                            openMainActivity()
                        } else {
                            // Verificar si la cuenta es nueva
                            if (task.result?.additionalUserInfo?.isNewUser == true) {
                                sendEmailVerification()
                            } else {
                                saveSessionState(true) // Guardar sesión como iniciada
                                openMainActivity()
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveSessionState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }

    private fun clearSessionState() {
        val editor = sharedPreferences.edit()
        editor.remove("is_logged_in")
        editor.apply()
    }

    private fun checkFields(): Boolean {
        email = binding.etEmail.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter a valid email address"
            binding.etEmail.requestFocus()
            return false
        }

        password = binding.etPassword.text.toString().trim()
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun openMainActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun openRegistrationActivity() {
        startActivity(Intent(this, RegistroActivity::class.java))
    }

    companion object {
        private const val TAG = "AuthActivity"
    }
}
