package com.example.myecommerceapp.view.ui.product.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myecommerceapp.R
import com.example.myecommerceapp.databinding.FragmentProductListBinding
import com.example.myecommerceapp.domain.repository.AuthRepository
import com.example.myecommerceapp.ui.theme.MyEcommerceAppTheme
import com.example.myecommerceapp.view.ui.product.ProductListScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : Fragment() {
//    private var _binding: FragmentProductListBinding? = null
//    private val binding get() = _binding!!

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("ProductListFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ProductListFragment", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ProductListFragment", "onCreateView - Setting Compose Content")

        // Retorna un ComposeView que aloja tu Composable ProductListScreen
        return ComposeView(requireContext()).apply {
            setContent {
                MyEcommerceAppTheme {
                    ProductListScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ProductListFragment", "onViewCreated")

//        binding.logoutButton.setOnClickListener {
//            authRepository.logout() // Cierra la sesión
//            // Navega de vuelta al LoginFragment y limpia el back stack
//            findNavController().navigate(R.id.loginFragment) {
//                popUpTo(R.id.nav_graph) { inclusive = true } // Vuelve al inicio del gráfico
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProductListFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ProductListFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ProductListFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ProductListFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ProductListFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("ProductListFragment", "onDetach")
    }
}