package com.secureguard.app.activities

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.secureguard.app.R
import com.secureguard.app.databinding.ActivityDeviceAdminBinding
import com.secureguard.app.receivers.DeviceAdminReceiver

class DeviceAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceAdminBinding
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDeviceAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, DeviceAdminReceiver::class.java)
        
        binding.btnEnableAdmin.setOnClickListener {
            if (!devicePolicyManager.isAdminActive(componentName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_explanation))
                startActivity(intent)
            } else {
                // Já é administrador, redirecionar para a MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        
        // Verificar se já é administrador ao iniciar
        if (devicePolicyManager.isAdminActive(componentName)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
