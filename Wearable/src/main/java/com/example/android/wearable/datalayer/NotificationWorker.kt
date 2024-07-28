package com.example.android.wearable.datalayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val challenges = arrayOf(
        "Autorretrato Creativo: Tomar una foto original de ti mismo utilizando espejos, sombras o reflejos en el agua.",
        "Paisaje Urbano: Capturar una vista única de la ciudad, enfocándote en la arquitectura y el ambiente urbano.",
        "Detalles de la Naturaleza: Fotografiar de cerca elementos naturales como hojas, flores o insectos.",
        "Contraluz: Crear una silueta tomando una foto en contraluz, jugando con la posición del sol.",
        "Retrato de Extraño: Pedir permiso a un desconocido para tomar su retrato y contar su historia brevemente.",
        "Fotografía de Movimiento: Capturar la acción en movimiento, como personas corriendo, bicicletas, o agua fluyendo.",
        "Perspectiva Forzada: Crear una ilusión óptica jugando con la perspectiva y los ángulos.",
        "Patrones y Simetría: Encontrar y fotografiar patrones repetitivos o elementos simétricos en tu entorno.",
        "Retrato de Mascota: Tomar una foto creativa de tu mascota o de un animal que encuentres en tu camino.",
        "Reflejos: Buscar superficies reflectantes como charcos, vidrios o espejos y capturar una imagen interesante.",
        "Comida Apetitosa: Tomar una foto que haga que la comida se vea irresistible.",
        "Callejones y Pasajes: Explorar y fotografiar callejones o pasajes interesantes de la ciudad.",
        "Luz y Sombras: Jugar con la luz y las sombras para crear una composición intrigante.",
        "Puerta y Ventanas: Capturar puertas y ventanas que tengan detalles arquitectónicos interesantes.",
        "Cambio de Estación: Fotografiar el mismo lugar durante las diferentes estaciones del año para mostrar cómo cambia con el tiempo."
    )


    override fun doWork(): Result {
        sendNotification()
        scheduleNextNotification()
        return Result.success()
    }

    private fun sendNotification() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            createNotificationChannel()

            val randomChallenge = challenges.random()
            val notificationId = System.currentTimeMillis().toInt()

            val builder = NotificationCompat.Builder(applicationContext, "CHALLENGE_CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("New Challenge")
                .setContentText(randomChallenge)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(notificationId, builder.build())
            }
        } else {
            Log.d("NotificationWorker", "Permission not granted")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Challenge Channel"
            val descriptionText = "Channel for challenge notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CHALLENGE_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationWorker", "Notification channel created")
        }
    }

    private fun scheduleNextNotification() {
        val nextNotificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "NotificationWork",
            ExistingWorkPolicy.REPLACE,
            nextNotificationWork
        )
    }
}
