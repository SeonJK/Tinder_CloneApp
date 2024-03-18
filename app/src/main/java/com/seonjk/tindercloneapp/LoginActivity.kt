package com.seonjk.tindercloneapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.seonjk.tindercloneapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        initSignInButton()
        initSignUpButton()
        enableButtons()
        initFacebookSignInButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun initSignInButton() {
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handleSuccessLogin()
                        finish()
                    } else {
                        Toast.makeText(this, "올바르지 않은 이메일 혹은 패스워드입니다.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun handleSuccessLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)

        Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
    }

    private fun initSignUpButton() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "정상적으로 회원 가입되었습니다. 로그인 버튼을 눌러 로그인해주세요.",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(this, "이미 가입한 이메일입니다.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun enableButtons() {
        binding.emailEditText.addTextChangedListener {
            val enable: Boolean =
                binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.signInButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }

        binding.passwordEditText.addTextChangedListener {
            val enable: Boolean =
                binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.signInButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookSignInButton() {
        binding.facebookSignInButton.setPermissions("email", "public_profile")
        binding.facebookSignInButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // 로그인 성공
                    val credential: AuthCredential =
                        FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                Log.d("jkseon", "onSuccess() 111")
                                handleSuccessLogin()
                                finish()
                            } else {
                                Log.d("jkseon", "onSuccess() 222")
                                Toast.makeText(
                                    this@LoginActivity,
                                    "페이스북 로그인이 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                }

                override fun onCancel() {
                    Log.d("jkseon", "onCancel()")
                }

                override fun onError(error: FacebookException) {
                    Log.d("jkseon", "onError() :: $error")
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }
}