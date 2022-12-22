package com.ianpedraza.streamingbootcamp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ianpedraza.streamingbootcamp.databinding.ActivityMainBinding
import com.ianpedraza.streamingbootcamp.utils.viewBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }
}
