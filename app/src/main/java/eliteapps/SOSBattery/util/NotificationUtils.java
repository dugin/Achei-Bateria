package eliteapps.SOSBattery.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.List;
import java.util.Random;


public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method checks if the app is in background or not
     *
     *
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (NullPointerException e) {

            return true;
        }


        return isInBackground;
    }

    public void showNotificationMessage(String title, String message, Intent intent, int imgPath, int smalIcon, int notificationID) {

        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        //if (isAppIsInBackground(mContext)) {
        // notification icon


        intent.putExtra("id", notificationID);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
        Notification notification = mBuilder.setSmallIcon(smalIcon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(inboxStyle)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), imgPath))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notification);

          /*  } else {
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            } */
    }

    public String[] msgBateriaMorrendo() {
        String[] msg1 = new String[2];
        String[] msg2 = new String[2];


        msg1[0] = "Bateria tá acabando?";
        msg1[1] = "Clique aqui para saber onde carregar!";

        msg2[0] = "Precisando carregar?";
        msg2[1] = "Clique aqui e descubra onde!";



        Random gerador = new Random();

        int numero = gerador.nextInt(2);

        switch (numero) {
            case 0:
                return msg1;
            case 1:
                return msg2;

        }


        return msg1;
    }

    public String[] msgCarregouLoja() {
        String[] msg1 = new String[2];
        String[] msg2 = new String[2];
        String[] msg3 = new String[2];


        msg1[0] = "Obrigado por usar o SOS Battery!";
        msg1[1] = "Ficamos felizes em ajudar! :)";

        msg2[0] = "Obrigado por usar o SOS Battery!";
        msg2[1] = "Aproveite sua bateria sem moderação! :)";

        msg3[0] = "Obrigado por usar o SOS Battery!";
        msg3[1] = "Volte sempre! :)";

        Random gerador = new Random();

        int numero = gerador.nextInt(3);

        switch (numero) {
            case 0:
                return msg1;
            case 1:
                return msg2;
            case 2:
                return msg3;
        }


        return msg1;
    }
}