package com.example.eatwell.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import java.text.Normalizer
import androidx.fragment.app.Fragment
import com.example.eatwell.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val foodMap = mapOf(
        "GẠO NẾP CÁI" to "",
        "GẠO TẺ" to "",
        "GẠO LỨT" to "",
        "NGÔ TƯƠI" to "",
        "BÁNH MÌ" to "",
        "BÚN" to "",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodNames = foodMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, foodNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFood.adapter = adapter

        binding.spinnerFood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFood = foodNames[position]
                val info = foodMap[selectedFood] ?: "Không có thông tin"
                binding.textFoodInfo.text = info

                val drawableName = formatDrawableName(selectedFood)
                val resId = resources.getIdentifier(drawableName, "drawable", requireContext().packageName)
                val extraImageName = "${drawableName}_extra"
                val extraResId = resources.getIdentifier(extraImageName, "drawable", requireContext().packageName)

                // Ẩn hiện ảnh phụ
                if (extraResId != 0) {
                    binding.imageFoodExtra.setImageResource(extraResId)
                    binding.imageFoodExtra.visibility = View.VISIBLE
                } else {
                    binding.imageFoodExtra.setImageDrawable(null)
                    binding.imageFoodExtra.visibility = View.GONE
                }

                // Ẩn hiện ảnh chính
                if (resId != 0) {
                    binding.imageFood.setImageResource(resId)
                    binding.imageFood.visibility = View.VISIBLE
                } else {
                    binding.imageFood.setImageDrawable(null)
                    binding.imageFood.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.textFoodInfo.text = "Chọn một món ăn để xem thông tin"
                binding.imageFood.setImageDrawable(null)
                binding.imageFood.visibility = View.GONE
                binding.imageFoodExtra.setImageDrawable(null)
                binding.imageFoodExtra.visibility = View.GONE
            }
        }
    }

    private fun formatDrawableName(foodName: String): String {
        val preProcessed = foodName.replace('đ', 'd').replace('Đ', 'D')
        val normalized = Normalizer.normalize(preProcessed, Normalizer.Form.NFD)
        val noAccent = normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        return noAccent.lowercase()
            .replace(" ", "_")
            .replace(Regex("[^a-z0-9_]"), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}