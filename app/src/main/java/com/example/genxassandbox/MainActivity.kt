package com.example.genxassandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.aes256decryptor.decryptData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCurrentPassword.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let { text ->
                    val text = "07TKjs0A4wqF06J0S3m256MSKGwArCIiDNJ87PHctBXP8Qu2TaenSCfh5etL8402v4ktezCpFrPyigOIj-VJsi4m0aAuSkg1EQlvwwfv9sQ"
                    tvEncryptedPassword.text = text
                    tvDecryptedPassword.text = decryptData(text)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
}
