/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.credentialmanager.sample

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePasswordResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.credentialmanager.sample.data.models.GetPasskeyResponseData
import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.databinding.FragmentSignUpBinding
import com.google.credentialmanager.sample.viewModel.AutenticatorViewModel
import com.google.credentialmanager.sample.viewModel.LoginViewModel
import com.google.credentialmanager.sample.viewModel.ViewModelAutenticatorFactory
import com.google.credentialmanager.sample.viewModel.ViewModelLoginFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class SignUpFragment : Fragment() {

    private lateinit var credentialManager: CredentialManager
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: SignUpFragmentCallback

    private lateinit var viewModel: LoginViewModel

    private lateinit var viewModelAutenticator: AutenticatorViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelLoginFactory
    @Inject
    lateinit var autenticatorViewModelFactory: ViewModelAutenticatorFactory


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SignUpFragmentCallback
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()

        credentialManager = CredentialManager.create(requireActivity())

        binding.signUp.setOnClickListener(signUpWithPasskeys())
        binding.signUpWithPassword.setOnClickListener(signUpWithPassword())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        viewModelAutenticator = ViewModelProvider(this, autenticatorViewModelFactory)[AutenticatorViewModel::class.java]

    }
    private fun signUpWithPassword(): View.OnClickListener {
        return View.OnClickListener {
            binding.password.visibility = View.VISIBLE

            if (binding.username.text.isNullOrEmpty()) {
                binding.username.error = "User name required"
                binding.username.requestFocus()
            } else if (binding.password.text.isNullOrEmpty()) {
                binding.password.error = "Password required"
                binding.password.requestFocus()
            } else {
                lifecycleScope.launch {

                    configureViews(View.VISIBLE, false)

                    createPassword()

                    simulateServerDelayAndLogIn()

                }
            }
        }
    }

    private fun simulateServerDelayAndLogIn() {
        Handler(Looper.getMainLooper()).postDelayed({

            DataProvider.setSignedInThroughPasskeys(false)

            configureViews(View.INVISIBLE, true)

            listener.showHome()
        }, 2000)
    }

    private fun signUpWithPasskeys(): View.OnClickListener {
        return View.OnClickListener {

            binding.password.visibility = View.GONE

            if (binding.username.text.isNullOrEmpty()) {
                binding.username.error = "User name required"
                binding.username.requestFocus()
            } else {
                lifecycleScope.launch {
                    configureViews(View.VISIBLE, false)

                    fetchRegistrationJsonFromServer()
                    var data: String
                    viewModel.userResponse.observe(viewLifecycleOwner) { it1 ->
                        data = viewModelAutenticator.getResponseInString(it1)
                        viewLifecycleOwner.lifecycleScope.launch{
                            val dataPasskey = createPasskey(CreatePublicKeyCredentialRequest(data))
                            dataPasskey?.let { it2 ->
                                registerResponse(it2)
                                viewModel.finishRegister.observe(viewLifecycleOwner){
                                    println("FINISH REGISTER$it")
                                    DataProvider.setSignedInThroughPasskeys(it)
                                }

                                listener.showHome()
                            }
                        }
                    }

                    configureViews(View.INVISIBLE, true)

                }
            }
        }
    }

    private fun  fetchRegistrationJsonFromServer(){
        val user = UserRequest(binding.username.text.toString())
        viewModel.getUserResponse(user)
    }

    private fun  finishRegisterFromServer(response: GetPasskeyResponseData){
        val user = UserRequest(binding.username.text.toString())
        val registerFinish = RegisterFinish( user.username, response)
        viewModel.getRegisterFinish(registerFinish)
    }


    private fun getEncodedUserId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(64)
        random.nextBytes(bytes)
        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }

    private fun getEncodedChallenge(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }

    private suspend fun createPassword() {
        val request = CreatePasswordRequest(
            binding.username.text.toString(),
            binding.password.text.toString()
        )
        try {
            credentialManager.createCredential(requireActivity(), request) as CreatePasswordResponse
        } catch (e: Exception) {
            Log.e("Auth", "createPassword failed with exception: " + e.message)
        }
    }

    private suspend fun createPasskey(request: CreatePublicKeyCredentialRequest): CreatePublicKeyCredentialResponse? {
        var response: CreatePublicKeyCredentialResponse? = null
        try {
            response = request.let {
                credentialManager.createCredential(
                    requireActivity(),
                    it
                )
            } as CreatePublicKeyCredentialResponse
        } catch (e: CreateCredentialException) {
            configureProgress(View.INVISIBLE)
            handlePasskeyFailure(e)
        }
        return response
    }

    private fun configureViews(visibility: Int, flag: Boolean) {
        configureProgress(visibility)
        binding.signUp.isEnabled = flag
        binding.signUpWithPassword.isEnabled = flag
    }

    private fun configureProgress(visibility: Int) {
        binding.textProgress.visibility = visibility
        binding.circularProgressIndicator.visibility = visibility
    }

    // These are types of errors that can occur during passkey creation.
    private fun handlePasskeyFailure(e: CreateCredentialException) {
        val msg = when (e) {
            is CreatePublicKeyCredentialDomException -> {
                // Handle the passkey DOM errors thrown according to the
                // WebAuthn spec using e.domError
                "An error occurred while creating a passkey, please check logs for additional details."
            }
            is CreateCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to register the credential.
                "The user intentionally canceled the operation and chose not to register the credential. Check logs for additional details."
            }
            is CreateCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
                "The operation was interrupted, please retry the call. Check logs for additional details."
            }
            is CreateCredentialProviderConfigurationException -> {
                // Your app is missing the provider configuration dependency.
                // Most likely, you're missing "credentials-play-services-auth".
                "Your app is missing the provider configuration dependency. Check logs for additional details."
            }
            is CreateCredentialUnknownException -> {
                "An unknown error occurred while creating passkey. Check logs for additional details."
            }
            is CreateCredentialCustomException -> {
                // You have encountered an error from a 3rd-party SDK. If you
                // make the API call with a request object that's a subclass of
                // CreateCustomCredentialRequest using a 3rd-party SDK, then you
                // should check for any custom exception type constants within
                // that SDK to match with e.type. Otherwise, drop or log the
                // exception.
                "An unknown error occurred from a 3rd party SDK. Check logs for additional details."
            }
            else -> {
                Log.w("Auth", "Unexpected exception type ${e::class.java.name}")
                "An unknown error occurred."
            }
        }
        Log.e("Auth", "createPasskey failed with exception: " + e.message.toString())
        activity?.showErrorAlert(msg)
    }

    private fun registerResponse(createPublicKeyCredentialResponse: CreatePublicKeyCredentialResponse): Boolean {
        println(createPublicKeyCredentialResponse)
        val obj = viewModelAutenticator.getResponseInObject(createPublicKeyCredentialResponse.registrationResponseJson)
        val parcelableValue = createPublicKeyCredentialResponse.data.getBundle("")
        finishRegisterFromServer(obj)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        configureProgress(View.INVISIBLE)
        _binding = null
    }

    interface SignUpFragmentCallback {
        fun showHome()
    }
}
