package com.example.myecommerceapp.view.ui.auth.fragments.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myecommerceapp.R
import com.example.myecommerceapp.databinding.FragmentLoginBinding
import com.example.myecommerceapp.view.ui.auth.fragments.bottomsheet.RegisterBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("LoginFragment", "onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginFragment", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LoginFragment", "onCreateView")
        val viewBinding = FragmentLoginBinding.inflate(inflater, container, false)
        _binding = viewBinding
        return viewBinding.root // Retorna la vista raíz directamente de la variable local no-nula
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LoginFragment", "onViewCreated")
        binding?.let {
            currentBinding ->
            currentBinding.emailEditText.addTextChangedListener {
                text -> loginViewModel.onEmailChange(text.toString())
            }
            currentBinding.passwordEditText.addTextChangedListener {
                text -> loginViewModel.onPasswordChange(text.toString())
            }
//            currentBinding.loginButton.setOnClickListener {
//                loginViewModel.onLoginClick()

            viewLifecycleOwner.lifecycleScope.launch {
                loginViewModel.loginSuccess.collectLatest { success ->
                    if (success) {
                    Log.d("LoginFragment", "Login successful. Navega a ProductList")
                        findNavController().navigate(R.id.action_loginFragment_to_productListFragment)
                        loginViewModel.loginSuccessHandled() // Restablezco la bandera para el futuro
                    }
                }
            }

            //observo mensajes de error en en view model y los muestro en el inputLayout
            viewLifecycleOwner.lifecycleScope.launch {
                loginViewModel.errorMessage.collectLatest { errorMessage ->
                    //limpio errores previos
                    currentBinding.emailInputLayout.error = null
                        currentBinding.emailInputLayout.error = null

                    errorMessage?.let {
                        if (it.contains("email", ignoreCase = true)
                            || it.contains("vacío", ignoreCase = true)
                            || it.contains("credenciales", ignoreCase = true)) {
                            currentBinding.emailInputLayout.error = it
                        }
                        if (it.contains("contraseña", ignoreCase = true)
                            || it.contains("vacío", ignoreCase = true)
                            || it.contains("credenciales", ignoreCase = true)) {
                            currentBinding.passwordInputLayout.error = it
                        }
                        // Si el error no es específico de un campo, mostrarlo como un Toast general
                        if (currentBinding.emailInputLayout.error
                            == null && currentBinding.passwordInputLayout.error == null) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                    }
            }

            currentBinding.loginButton.setOnClickListener {
                Log.d("LoginFragment", "Botón de inicio de sesión presionado")
                loginViewModel.onLoginClick()
            }
            currentBinding.registerTextView.setOnClickListener {
                Log.d("LoginFragment", "Botón de registro presionado")
                val registerBottomSheetBinding = RegisterBottomSheetFragment() //TODO: Refacotorizar con DI
                registerBottomSheetBinding.show(parentFragmentManager, "registerBottomSheet")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("LoginFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LoginFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LoginFragment", "onStop")
    }

    // Es CRÍTICO establecer _binding a null en onDestroyView para evitar fugas de memoria,
    // ya que la vista del Fragment se destruye pero el Fragment no necesariamente.
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LoginFragment", "onDestroyView - Limpiando binding.")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LoginFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("LoginFragment", "onDetach")
    }
}