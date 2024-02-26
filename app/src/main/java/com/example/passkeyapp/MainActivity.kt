package com.example.passkeyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.passkeyapp.data.models.UserRequest
import com.example.passkeyapp.viewModel.LoginViewModel
import com.example.passkeyapp.viewModel.ViewModelLoginFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelLoginFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()

        val user = UserRequest("kminchelle")
        viewModel.getUserResponse(user).observe(this as LifecycleOwner){
                userResponse->
            if(userResponse.getOrNull()!=null){
                Log.e("login", "success")
            }else{
                Log.e("Login", "error")
            }
        }

    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
    }

}