package com.example.carebuddy.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

// 👇 Modern DataStore setup (this replaces old delegate syntax)
private val Context.stepStore by preferencesDataStore(name = "steps_prefs")

@Singleton
class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    companion object {
        private val KEY_BASELINE = longPreferencesKey("steps_baseline")
        private val KEY_DAY = longPreferencesKey("steps_day")
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _deviceSteps = MutableStateFlow(0L)
    val deviceSteps: StateFlow<Long> = _deviceSteps

    private val _stepsToday = MutableStateFlow(0L)
    val stepsToday: StateFlow<Long> = _stepsToday

    private var baseline = -1L
    private var baselineDay = -1L

    init {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // load saved baseline
        scope.launch {
            val prefs = context.stepStore.data.first()
            baseline = prefs[KEY_BASELINE] ?: -1L
            baselineDay = prefs[KEY_DAY] ?: -1L
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val totalSteps = event.values.firstOrNull()?.toLong() ?: return

        _deviceSteps.value = totalSteps

        scope.launch {
            val today = System.currentTimeMillis() / TimeUnit.DAYS.toMillis(1)

            if (baseline == -1L || baselineDay != today) {
                baseline = totalSteps
                baselineDay = today

                context.stepStore.edit { prefs ->
                    prefs[KEY_BASELINE] = baseline
                    prefs[KEY_DAY] = today
                }

                _stepsToday.value = 0L
            } else {
                _stepsToday.value = max(0, totalSteps - baseline)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}
