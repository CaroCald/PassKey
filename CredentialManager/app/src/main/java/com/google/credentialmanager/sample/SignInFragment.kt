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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.credentialmanager.sample.data.models.LoginFinishRequest
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.databinding.FragmentSignInBinding
import com.google.credentialmanager.sample.viewModel.AutenticatorViewModel
import com.google.credentialmanager.sample.viewModel.LoginViewModel
import com.google.credentialmanager.sample.viewModel.ViewModelAutenticatorFactory
import com.google.credentialmanager.sample.viewModel.ViewModelLoginFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class SignInFragment : Fragment() {

    private lateinit var credentialManager: CredentialManager
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: SignInFragmentCallback

    private lateinit var viewModel: LoginViewModel

    private lateinit var viewModelAutenticator: AutenticatorViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelLoginFactory
    @Inject
    lateinit var autenticatorViewModelFactory: ViewModelAutenticatorFactory


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SignInFragmentCallback
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        viewModelAutenticator = ViewModelProvider(this, autenticatorViewModelFactory)[AutenticatorViewModel::class.java]

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        credentialManager = CredentialManager.create(requireActivity())

        binding.signInWithSavedCredentials.setOnClickListener(signInWithSavedCredentials())
    }

    private fun signInWithSavedCredentials(): View.OnClickListener {
        return View.OnClickListener {

            lifecycleScope.launch {
                configureViews(View.VISIBLE, false)
                finishRegisterFromServer("caro")

                var dataString: String
                viewModel.finishRLoginStart.observe(viewLifecycleOwner){ it ->
                    dataString = viewModelAutenticator.getResponseLoginInString(it)
                    viewLifecycleOwner.lifecycleScope.launch{
                        val data = getSavedCredentials(dataString)

                        val obj = data?.let { response ->
                            viewModelAutenticator.fromStringToObjectLoginPassKeyResponse(
                                response
                            )
                        }

                        data?.let {
                            sendSignInResponseToServer(obj)

                        }

                        viewModel.finishRLogin.observe(viewLifecycleOwner){ it1 ->
                            if(it1){
                                listener.showHome()
                            }else{
                                activity?.showErrorAlert("Error auth server")
                            }

                            configureViews(View.INVISIBLE, true)
                        }
                    }

                }




            }
        }
    }

    private fun configureViews(visibility: Int, flag: Boolean) {
        configureProgress(visibility)
        binding.signInWithSavedCredentials.isEnabled = flag
    }

    private fun configureProgress(visibility: Int) {
        binding.textProgress.visibility = visibility
        binding.circularProgressIndicator.visibility = visibility
    }

    private fun  finishRegisterFromServer(user: String){
        val userRequest = UserRequest(user)
        viewModel.doLoginStart(userRequest)
    }

    private fun sendSignInResponseToServer(obj: PasskeyLoginResponse?){

        if (obj != null) {
            viewModel.doLoginFinish(LoginFinishRequest("caro", obj))
        }
    }

    private suspend fun getSavedCredentials(dataString: String): String? {
        val getPublicKeyCredentialOption =
            GetPublicKeyCredentialOption(dataString, null)
        val getPasswordOption = GetPasswordOption()
        val result = try {
            credentialManager.getCredential(
                requireActivity(),
                GetCredentialRequest(
                    listOf(
                        getPublicKeyCredentialOption,
                        getPasswordOption
                    )
                )
            )
        } catch (e: Exception) {
            configureViews(View.INVISIBLE, true)
            Log.e("Auth", "getCredential failed with exception: " + e.message.toString())
            activity?.showErrorAlert(
                "An error occurred while authenticating through saved credentials. Check logs for additional details"
            )
            return null
        }

        if (result.credential is PublicKeyCredential) {
            val cred = result.credential as PublicKeyCredential
            DataProvider.setSignedInThroughPasskeys(true)
            return cred.authenticationResponseJson
        }
        if (result.credential is PasswordCredential) {
            val cred = result.credential as PasswordCredential
            DataProvider.setSignedInThroughPasskeys(false)
            return "Got Password - User:${cred.id} Password: ${cred.password}"
        }
        if (result.credential is CustomCredential) {
            //If you are also using any external sign-in libraries, parse them here with the
            // utility functions provided.
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        configureProgress(View.INVISIBLE)
        _binding = null
    }

    interface SignInFragmentCallback {
        fun showHome()
    }
}
