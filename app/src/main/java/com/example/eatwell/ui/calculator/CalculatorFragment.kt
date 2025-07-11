package com.example.eatwell.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.example.eatwell.R

class CalculatorFragment : Fragment() {

    private lateinit var spinnerAgeRange: Spinner
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var spinnerMeal: Spinner
    private lateinit var textCalorieResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinnerAgeRange = view.findViewById(R.id.spinner_age_range)
        radioGroupGender = view.findViewById(R.id.radio_gender)
        spinnerMeal = view.findViewById(R.id.spinner_meal)
        textCalorieResult = view.findViewById(R.id.text_calorie_result)

        val ageRanges = arrayOf("<18", "18–30", "31–50", ">50")
        val meals = arrayOf("Sáng", "Trưa", "Tối")

        spinnerAgeRange.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ageRanges)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerMeal.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, meals)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerAgeRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                calculateCalorie()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerMeal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                calculateCalorie()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        radioGroupGender.setOnCheckedChangeListener { _, _ -> calculateCalorie() }
    }

    private fun calculateCalorie() {
        val ageRange = spinnerAgeRange.selectedItem.toString()
        val meal = spinnerMeal.selectedItem.toString()
        val genderId = radioGroupGender.checkedRadioButtonId

        if (genderId == -1) return  // chưa chọn giới tính

        val gender = if (genderId == R.id.radio_male) "Nam" else "Nữ"

        // Giả lập bảng tính calo đơn giản
        val baseCalo = when (ageRange) {
            "<18" -> 1800
            "18–30" -> 2200
            "31–50" -> 2000
            else -> 1800
        }

        val genderFactor = if (gender == "Nam") 1.1 else 1.0
        val mealFactor = when (meal) {
            "Sáng" -> 0.25
            "Trưa" -> 0.40
            "Tối" -> 0.35
            else -> 0.0
        }

        val result = (baseCalo * genderFactor * mealFactor).toInt()
        textCalorieResult.text = "Kcal: $result"
    }
}