package com.example.eatwell.ui.chatbot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.eatwell.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ChatbotFragment : Fragment() {

    private lateinit var textViewResponse: TextView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonPickImage: Button
    private lateinit var imagePreview: ImageView

    private var selectedBitmap: Bitmap? = null
    private val IMAGE_PICK_CODE = 1001

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBZJYQRula3azq05fUaGn9sgAMhpSfHQFU" // ✅ Thay bằng API Key thực
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        // Gán view
        textViewResponse = view.findViewById(R.id.textViewResponse)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        buttonPickImage = view.findViewById(R.id.buttonPickImage)
        imagePreview = view.findViewById(R.id.imagePreview)

        // Gửi yêu cầu
        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString().trim()
            if (message.isEmpty() && selectedBitmap == null) {
                Toast.makeText(requireContext(), "Vui lòng nhập câu hỏi hoặc chọn ảnh!", Toast.LENGTH_SHORT).show()
            } else {
                sendMessageToGemini(message, selectedBitmap)
            }
        }

        // Mở thư viện chọn ảnh
        buttonPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Cho phép nhấn vào ảnh để bỏ chọn
        imagePreview.setOnClickListener {
            selectedBitmap = null
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = View.GONE
            Toast.makeText(requireContext(), "Đã xoá ảnh đã chọn", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                val resized = resizeBitmap(bitmap, 384)
                selectedBitmap = resized
                imagePreview.setImageBitmap(resized)
                imagePreview.visibility = View.VISIBLE
            }
        }
    }

    private fun sendMessageToGemini(message: String, bitmap: Bitmap?) {
        lifecycleScope.launch {
            textViewResponse.text = "Đang xử lý..."

            val responseText = withContext(Dispatchers.IO) {
                try {
                    val inputContent = content {
                        bitmap?.let { image(it) }
                        if (message.isNotEmpty()) text(message)
                    }

                    val response = generativeModel.generateContent(inputContent)
                    response.text ?: "Không có phản hồi từ Gemini."
                } catch (e: Exception) {
                    "Lỗi: ${e.message}"
                }
            }

            textViewResponse.text = responseText

            // Reset sau khi gửi
            selectedBitmap = null
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = View.GONE
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scale = maxSize.toFloat() / maxOf(width, height)
        return Bitmap.createScaledBitmap(
            bitmap,
            (width * scale).toInt(),
            (height * scale).toInt(),
            true
        )
    }
}
