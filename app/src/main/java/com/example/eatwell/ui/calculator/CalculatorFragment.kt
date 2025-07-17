package com.example.eatwell.ui.calculator

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.example.eatwell.R
import com.example.eatwell.databinding.FragmentCalculatorBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class CalculatorFragment : Fragment() {

    private var suggestedCaloMin: Int = 0
    private var suggestedCaloMax: Int = 0
    data class FoodItem(
        val name: String,
        val energy: Double,
        val water: Double,
        val protein: Double,
        val fat: Double,
        val carbs: Double,
        val fiber: Double
    )

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodList: List<FoodItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodList = readCSVFromAssets(requireContext())

        setupSpinners()

        // Gọi tự động khi người dùng chọn tuổi, giới, bữa ăn
        binding.spinnerAgeRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                calculateCalorie()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerMeal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                calculateCalorie()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.radioGender.setOnCheckedChangeListener { _, _ ->
            calculateCalorie()
        }

        binding.btnXemBang.setOnClickListener {
            showNutritionTable()
        }

        binding.btnTinhCalo.setOnClickListener {
            calculateTotalCalories()
        }
    }

    private fun readCSVFromAssets(context: Context): List<FoodItem> {
        val list = mutableListOf<FoodItem>()
        val inputStream = context.assets.open("thucpham.csv")
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))

        reader.readLine() // Bỏ dòng tiêu đề

        reader.forEachLine { line ->
            val tokens = line.split(",")
            if (tokens.size >= 7) {
                try {
                    val item = FoodItem(
                        name = tokens[0].trim(),
                        energy = tokens[1].toDoubleOrNull() ?: 0.0,
                        water = tokens[2].toDoubleOrNull() ?: 0.0,
                        protein = tokens[3].toDoubleOrNull() ?: 0.0,
                        fat = tokens[4].toDoubleOrNull() ?: 0.0,
                        carbs = tokens[5].toDoubleOrNull() ?: 0.0,
                        fiber = tokens[6].toDoubleOrNull() ?: 0.0
                    )
                    list.add(item)
                } catch (_: Exception) {}
            }
        }

        return list
    }

    private fun setupSpinners() {
        val foodNames = foodList.map { it.name }

        val foodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, foodNames)
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner1.adapter = foodAdapter
        binding.spinner2.adapter = foodAdapter
        binding.spinner3.adapter = foodAdapter

        val ageOptions = listOf("Dưới 18", "18-30", "31-50", "Trên 50")
        val ageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ageOptions)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAgeRange.adapter = ageAdapter

        val mealOptions = listOf("Sáng", "Trưa", "Tối")
        val mealAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealOptions)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMeal.adapter = mealAdapter
    }

    private fun showNutritionTable() {
        val tableLayout = binding.tableLayout
        tableLayout.removeAllViews()

        val headerTitles = listOf("Tên", "Năng lượng", "Nước", "Đạm", "Béo", "Bột", "Xơ")
        val headerRow = TableRow(requireContext())

        headerTitles.forEach { title ->
            val tv = TextView(requireContext()).apply {
                text = title
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 4, 8, 4)
            }
            headerRow.addView(tv)
        }
        tableLayout.addView(headerRow)

        foodList.forEach { item ->
            val row = TableRow(requireContext())
            val values = listOf(
                item.name,
                item.energy,
                item.water,
                item.protein,
                item.fat,
                item.carbs,
                item.fiber
            )

            values.forEach {
                val tv = TextView(requireContext()).apply {
                    text = it.toString()
                    setPadding(8, 4, 8, 4)
                }
                row.addView(tv)
            }

            tableLayout.addView(row)
        }
    }
    private fun calculateCalorie() {

        val ageRange = binding.spinnerAgeRange.selectedItem?.toString() ?: return
        val meal = binding.spinnerMeal.selectedItem?.toString() ?: return
        val genderId = binding.radioGender.checkedRadioButtonId
        if (genderId == -1) return

        val gender = if (genderId == R.id.radio_male) "Nam" else "Nữ"

        val (minBase, maxBase) = when (ageRange) {
            "Dưới 18" -> if (gender == "Nam") 1800 to 2400 else 1600 to 2000
            "18-30" -> if (gender == "Nam") 2200 to 2800 else 1800 to 2200
            "31-50" -> if (gender == "Nam") 2200 to 2600 else 1800 to 2000
            else -> if (gender == "Nam") 2000 to 2400 else 1600 to 1800
        }

        val mealFactor = when (meal) {
            "Sáng" -> 0.25
            "Trưa" -> 0.4
            "Tối" -> 0.35
            else -> 0.0
        }

        suggestedCaloMin = (minBase * mealFactor).toInt()
        suggestedCaloMax = (maxBase * mealFactor).toInt()

        val gramMin = (suggestedCaloMin / 1.5).toInt()
        val gramMax = (suggestedCaloMax / 1.5).toInt()

        binding.textCalorieResult.text = "Kcal: $suggestedCaloMin – $suggestedCaloMax"
        binding.textWeightResult.text = "≈ $gramMin – $gramMax gram"
    }

    private fun calculateTotalCalories() {
        val i1 = binding.spinner1.selectedItemPosition
        val i2 = binding.spinner2.selectedItemPosition
        val i3 = binding.spinner3.selectedItemPosition

        val total = foodList[i1].energy + foodList[i2].energy + foodList[i3].energy

        val ageGroup = binding.spinnerAgeRange.selectedItem.toString()
        val meal = binding.spinnerMeal.selectedItem.toString()
        val isMale = binding.radioGender.checkedRadioButtonId == binding.radioMale.id

        val suggestedRange = when (ageGroup) {
            "Dưới 18" -> if (isMale) "1600–2200" else "1400–2000"
            "18-30" -> if (isMale) "2200–2800" else "1800–2400"
            "31-50" -> if (isMale) "2000–2600" else "1600–2200"
            "Trên 50" -> if (isMale) "2000–2400" else "1600–2000"
            else -> "--"
        }

        val mealNote = when (meal) {
            "Sáng" -> "Khuyến nghị ~25% tổng calo/ngày"
            "Trưa" -> "Khuyến nghị ~35–40% tổng calo/ngày"
            "Tối" -> "Khuyến nghị ~30% tổng calo/ngày"
            else -> ""
        }

        binding.txtKetQuaCalo.text = """
        Tổng calo: ${"%.0f".format(total)} kcal
        Khuyến nghị theo độ tuổi/giới: $suggestedRange kcal/ngày
        $mealNote
    """.trimIndent()

        // So sánh với khuyến nghị
        if (suggestedCaloMin > 0 && suggestedCaloMax > 0) {
            val comparisonText = when {
                total < suggestedCaloMin -> "⚠️ Bạn đang ăn **thiếu** ${"%.0f".format(suggestedCaloMin - total)} kcal"
                total > suggestedCaloMax -> "⚠️ Bạn đang ăn **vượt** ${"%.0f".format(total - suggestedCaloMax)} kcal"
                else -> "✅ Bạn đang ăn **phù hợp** với nhu cầu năng lượng"
            }

            binding.textComparison.text = comparisonText
            binding.textComparison.visibility = View.VISIBLE
        } else {
            binding.textComparison.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

