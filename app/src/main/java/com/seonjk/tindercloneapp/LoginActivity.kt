package com.seonjk.tindercloneapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.seonjk.tindercloneapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        initSignUpButton()
        initSignInButton()
        enableButtons()
    }

    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()

                        finish()
                    } else {
                        Toast.makeText(this, "올바르지 않은 이메일 혹은 패스워드입니다.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun initSignInButton() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "정상적으로 회원 가입되었습니다. 로그인 버튼을 눌러 로그인해주세요.", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "이미 가입한 이메일입니다.", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }

    private fun enableButtons() {

        binding.emailEditText.addTextChangedListener {
            val enable: Boolean = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.signInButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }

        binding.passwordEditText.addTextChangedListener {
            val enable: Boolean = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
            binding.signInButton.isEnabled = enable
            binding.signUpButton.isEnabled = enable
        }
    }
}