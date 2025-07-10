package com.example.eatwell.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import android.webkit.WebViewClient
import com.example.eatwell.R

class SlideshowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val webView: WebView = root.findViewById(R.id.webview)

        // Kích hoạt JavaScript nếu cần
        webView.settings.javaScriptEnabled = true

        // Đảm bảo mở trang web trong WebView thay vì trình duyệt ngoài
        webView.webViewClient = WebViewClient()

        // Load trang web
        webView.loadUrl("http://duocphutho.edu.vn/van-ban-phap-luat-ve-dinh-duong-an-toan-thuc-pham/")

        return root
    }
}