package com.example.eatwell.ui.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import java.text.Normalizer
import android.widget.AutoCompleteTextView
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.eatwell.R
import com.example.eatwell.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Danh sách các món ăn và thông tin tương ứng
    private val foodMap = mapOf(
        "GẠO NẾP CÁI" to "",
        "GẠO TẺ" to "",
        "2.\tGẠO TẺ" to "",
        "Bánh mì" to ""
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

                // Chuyển tên món ăn thành tên file ảnh trong drawable
                val drawableName = formatDrawableName(selectedFood)
                val resId = resources.getIdentifier(drawableName, "drawable", requireContext().packageName)

                val extraImageName = drawableName + "_extra"
                val extraResId = resources.getIdentifier(extraImageName, "drawable", requireContext().packageName)
                binding.imageFoodExtra.setImageResource(
                    if (extraResId != 0) extraResId else R.drawable.placeholder
                )
                if (resId != 0) {
                    binding.imageFood.setImageResource(resId)
                } else {
                    binding.imageFood.setImageResource(R.drawable.placeholder) // Ảnh mặc định nếu không có ảnh món
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.textFoodInfo.text = "Chọn một món ăn để xem thông tin"
                binding.imageFood.setImageResource(R.drawable.placeholder)
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
