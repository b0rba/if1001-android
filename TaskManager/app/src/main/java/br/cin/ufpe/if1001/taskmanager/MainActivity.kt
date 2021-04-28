package br.cin.ufpe.if1001.taskmanager

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.cin.ufpe.if1001.taskmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.blueLightFilter.cardText.text = "Filtro de Luz Azul"
        binding.blueLightFilter.cardText.setOnClickListener {
            binding.blueLightFilter.buttonToggle.performClick()
            Toast.makeText(this, "clicou xalalal", Toast.LENGTH_SHORT).show() }



        binding.bluetoothCustomSettings.cardText.text = "Configurações boladonas de bluetooth"
        binding.mobileDataOffWifi.cardText.text = "Desligar Dados moveis no WIFI"
        binding.screenLocakUsb.cardText.text = "Manter tela ligada no usb"
        binding.wifiOffHome.cardText.text = "Desligar WIFI ao sair de casa"
        binding.wrongPasswordPicture.cardText.text = "Tirar foto do ladrão"

    }
}