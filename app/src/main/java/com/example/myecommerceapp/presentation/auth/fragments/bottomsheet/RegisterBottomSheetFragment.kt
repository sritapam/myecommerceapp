package com.example.myecommerceapp.presentation.auth.fragments.bottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myecommerceapp.databinding.FragmentRegisterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterBottomSheetFragment : BottomSheetDialogFragment() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBottomSheetBinding? = null
    // Propiedad calculada para acceder al binding sin nullables y con seguridad
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("RegisterBSFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RegisterBSFragment", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RegisterBSFragment", "onCreateView")
        _binding = FragmentRegisterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RegisterBSFragment", "onViewCreated")

        // Bindear campos de texto al ViewModel
        binding.registerEmailEditText.addTextChangedListener { text ->
            registerViewModel.onEmailChange(text.toString())
        }
        binding.registerFullnameEditText.addTextChangedListener { text ->
            registerViewModel.onFullNameChange(text.toString())
        }
        binding.registerPasswordEditText.addTextChangedListener { text ->
            registerViewModel.onPasswordChange(text.toString())
        }
        binding.registerConfirmPasswordEditText.addTextChangedListener { text ->
            registerViewModel.onConfirmPasswordChange(text.toString())
        }

        // Observar LiveData/StateFlows del ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.registrationSuccess.collectLatest { success ->
                if (success) {
                    Toast.makeText(context, "Registro exitoso. ¡Inicia sesión!", Toast.LENGTH_LONG).show()
                    Log.d("RegisterBSFragment", "Registration successful. Dismissing.")
                    registerViewModel.registrationHandled() // Restablece la bandera
                    dismiss() // Cierra el bottom sheet
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.errorMessage.collectLatest { message ->
                // Limpiar errores previos en todos los campos al recibir un nuevo mensaje
                binding.registerEmailInputLayout.error = null
                binding.registerFullnameInputLayout.error = null
                binding.registerPasswordInputLayout.error = null
                binding.registerConfirmPasswordInputLayout.error = null

                message?.let {
                    // Lógica para asignar mensajes de error a campos específicos o Toast general
                    if (it.contains("email", ignoreCase = true)) {
                        binding.registerEmailInputLayout.error = it
                    } else if (it.contains("contraseña", ignoreCase = true) || it.contains("coinciden", ignoreCase = true)) {
                        binding.registerPasswordInputLayout.error = it
                        binding.registerConfirmPasswordInputLayout.error = it // Para ambas si no coinciden
                    } else if (it.contains("obligatorios", ignoreCase = true)) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show() // Mensaje general para campos obligatorios
                    } else {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show() // Otros errores generales
                    }
                }
            }
        }

        // Configurar click listener para el botón de registro
        binding.registerButton.setOnClickListener {
            Log.d("RegisterBSFragment", "Register button clicked.")
            registerViewModel.onRegisterClick()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("RegisterBSFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RegisterBSFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("RegisterBSFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("RegisterBSFragment", "onDestroyView")
        _binding = null // Limpiar el binding para evitar fugas de memoria
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RegisterBSFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("RegisterBSFragment", "onDetach")
    }
}