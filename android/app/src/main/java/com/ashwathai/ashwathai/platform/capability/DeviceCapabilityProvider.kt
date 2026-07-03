package com.ashwathai.ashwathai.platform.capability

interface DeviceCapabilityProvider {
    suspend fun getDeviceCapabilities(): DeviceCapabilities
}

data class DeviceCapabilities(
    val totalRamGb: Double,
    val availableRamGb: Double,
    val hasNpu: Boolean,
    val cpuCores: Int,
    val gpuVendor: String?
)
