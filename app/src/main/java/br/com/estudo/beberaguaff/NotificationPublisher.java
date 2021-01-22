package br.com.estudo.beberaguaff;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationPublisher extends BroadcastReceiver {

    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_NOTIFICATION_ID = "key_notification_id";

    @Override
    public void onReceive(Context context, Intent intent) { // Através das intents eu consigo transferir os dados

        Intent ii = new Intent(context.getApplicationContext(), MainActivity.class); //Criando o Intent para abrir minha MainActivity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, ii, 0);

        // preciso fazer um cast para pegar o NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//Com o getSystemService eu posso pegar qualquer recurso do sistema(Localização, bluetooth, horário)

        String message = intent.getStringExtra(KEY_NOTIFICATION);
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

        Notification notification = getNotification(message, context, notificationManager, pIntent); //Criando o objeto notification e o método getNotification();

        notificationManager.notify(id, notification); //notificationManeger, basicamente notifica uma notificação!
    }

    private Notification getNotification(String content, Context context, NotificationManager manager, PendingIntent intent) {
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext())
                .setContentText(content)
                .setTicker("Alerta")
                .setContentIntent(intent)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher);

        // Tratando a notificação para celularem que possuem a versão Oreo (Para que eles também possam receber as notificações).
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder.build();
    }
}

//BroadCastReciver = é o recurso onde a gente define o código que vai escutar, qualquer notificação vinda do sistema operacional do Android**/
///Quem vai notificar ele = é o Android!
//Como ele vai notificar = Vamos criar um alarme para ele identificar e disparar
//Qual horario que ele vai disparar? = No horario que definirmos no relógio.
