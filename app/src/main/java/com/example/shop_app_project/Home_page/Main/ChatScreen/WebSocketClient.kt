package com.example.shop_app_project.Home_page.Main.ChatScreen


import android.util.Log
import okhttp3.*
import okio.ByteString

class WebSocketClient(private val url: String, private val listener: WebSocketListener) {
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Connection closed")
    }
}
