package org.example.win

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import oshi.SystemInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class SystemStatService {
    private val si = SystemInfo()
    val hardware = si.hardware
    val processor = hardware.processor

    private val _stats = MutableStateFlow(SystemStats())
    val stats: StateFlow<SystemStats> = _stats

    fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            var prevTicks = processor.systemCpuLoadTicks
            while (true) {
                val ticks = processor.systemCpuLoadTicks
                val cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks)
                prevTicks = ticks
                val cpuPercent = (cpuLoad * 100).toInt()
                val memory = hardware.memory
                val used = memory.total - memory.available
                val ramPercent = ((used.toDouble() / memory.total) * 100).toInt()
                // -1 hides the battery in the bar on machines without one
                val battery = hardware.powerSources.firstOrNull()
                    ?.let { (it.remainingCapacityPercent * 100).roundToInt() }
                    ?: -1
                val now = LocalDateTime.now()
                _stats.value = SystemStats(
                    cpu = cpuPercent,
                    ram = ramPercent,
                    battery = battery,
                    time = now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    date = now.format(DateTimeFormatter.ofPattern("EEE dd MMM"))
                )
                delay(1000)
            }
        }
    }
}