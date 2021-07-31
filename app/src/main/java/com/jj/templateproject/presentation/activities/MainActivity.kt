package com.jj.templateproject.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jj.templateproject.data.utils.getAboutVersionText
import com.jj.templateproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setMainLabelText()
    }

    private fun setMainLabelText() {
        activityMainBinding.mainLabel.text = getAboutVersionText()
    }
}