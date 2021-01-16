package com.example.pricetracker;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.ArrayList;

public class ScraperWorker extends Worker {

    private NotificationManagerCompat notificationManagerCompat;

    ArrayList<Product> products;
    Context context;
    public ScraperWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyDBHandler myDBHandler = new MyDBHandler(context);
        products = new ArrayList<>();
        products = myDBHandler.getAllProductsFromTableA();
        MyScraper myScraper;
        int notif_id = 1;
        if (products == null) return Result.success();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        for (Product product : products) {
            myScraper = new MyScraper(product.getUrl(), product.getMarketPlace());
            try {
                myScraper.scrapeProductInfo();
                Product updated = myScraper.getProduct();
                if (updated.getPrice() == 0.0) {
                    updated.setPrice(product.getPrice());
                }
                if (updated.getName().equals("NA")) {
                    updated.setName(product.getName());
                }
                if (!updated.getPrice().equals(product.getPrice())) {
                    Log.e("do_work_testing", updated.getPrice() + " " + product.getPrice());
                    boolean b1 = myDBHandler.updateTableA(updated);
                    Log.e("worker_testing", String.valueOf(b1));
                    int inrSign = 0x20B9;
                    if (updated.getPrice() > product.getPrice()) {
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                                .setSmallIcon(R.drawable.ic_baseline_trending_up_24)
                                .setContentTitle("Price Tracker")
                                .setContentText(getEmojiByUnicode(inrSign) + product.getPrice().toString() + " to " + getEmojiByUnicode(inrSign) + updated.getPrice().toString() + ": " + product.getName())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle("Price Increase")
                                .bigText(product.getName() + "'s price increased from " + getEmojiByUnicode(inrSign) + product.getPrice().toString() + " to " + getEmojiByUnicode(inrSign) + updated.getPrice().toString())
                                )
                                .setCategory(NotificationCompat.CATEGORY_PROMO)
                                .build();
                        notificationManager.notify(notif_id++, notification);
                    }
                    else {
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                                .setSmallIcon(R.drawable.ic_baseline_trending_down_24)
                                .setContentTitle("Price Tracker")
                                .setContentText(getEmojiByUnicode(inrSign) + product.getPrice().toString() + " to " + getEmojiByUnicode(inrSign) + updated.getPrice().toString() + ": " + product.getName())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .setBigContentTitle("Price Decrease")
                                        .bigText(product.getName() + "'s price decreased from " + getEmojiByUnicode(inrSign) + product.getPrice().toString() + " to " + getEmojiByUnicode(inrSign) + updated.getPrice().toString())
                                )
                                .setCategory(NotificationCompat.CATEGORY_PROMO)
                                .build();
                        notificationManagerCompat.notify(notif_id++, notification);
                    }

                }
                boolean b2 = myDBHandler.insertIntoTableB(updated);
                Log.e("worker_testing", String.valueOf(b2));
                if (!b2) {
                    boolean b3 = myDBHandler.updateTableB(updated);
                    Log.e("worker_testing", String.valueOf(b3));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
