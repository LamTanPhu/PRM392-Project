// File: VNPayViewModel.kt
package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class VNPayViewModel : ViewModel() {

    private val _redirectUrl = MutableStateFlow<String?>(null)
    val redirectUrl: StateFlow<String?> = _redirectUrl

    fun createVNPayUrl(orderId: Long, amount: Double, returnUrl: String = "projectprmpp://vnpay/return") {
        viewModelScope.launch {
            val vnp_TmnCode = ""
            val vnp_SecretKey = ""
            val vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"

            val vnp_Amount = (amount * 100).toInt().toString()
            val vnp_TxnRef = orderId.toString()
            val vnp_OrderInfo = "Thanh toán đơn hàng #$orderId"
            val vnp_IpAddr = "127.0.0.1"
            val vnp_CreateDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val returnUrl = "projectprmpp://vnpay/return"

            val params = mutableMapOf(
                "vnp_Version" to "2.1.0",
                "vnp_Command" to "pay",
                "vnp_TmnCode" to vnp_TmnCode,
                "vnp_Amount" to vnp_Amount,
                "vnp_CurrCode" to "VND",
                "vnp_TxnRef" to vnp_TxnRef,
                "vnp_OrderInfo" to vnp_OrderInfo,
                "vnp_OrderType" to "billpayment",
                "vnp_Locale" to "vn",
                "vnp_ReturnUrl" to returnUrl,
                "vnp_IpAddr" to vnp_IpAddr,
                "vnp_CreateDate" to vnp_CreateDate
            )

            val sortedParams = params.toSortedMap()

            val hashData = sortedParams.entries.joinToString("&") {
                "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
            }

            val queryString = sortedParams.entries.joinToString("&") {
                "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
            }

            val mac = Mac.getInstance("HmacSHA512")
            val keySpec = SecretKeySpec(vnp_SecretKey.toByteArray(Charsets.UTF_8), "HmacSHA512")
            mac.init(keySpec)
            val hashBytes = mac.doFinal(hashData.toByteArray(Charsets.UTF_8))
            val secureHash = hashBytes.joinToString("") { "%02x".format(it) }

            val fullUrl = "$vnp_Url?$queryString&vnp_SecureHashType=HmacSHA512&vnp_SecureHash=$secureHash"
            _redirectUrl.value = fullUrl
        }
    }
}
