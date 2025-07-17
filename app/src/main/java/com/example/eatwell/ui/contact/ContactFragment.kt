package com.example.eatwell.ui.contact

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eatwell.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        showContactPopup()

        return binding.root
    }

    private fun showContactPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Thông tin liên hệ")
            .setMessage("Email: nguyentu98lt@gmail.com\nSĐT: 034 560 5055")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}