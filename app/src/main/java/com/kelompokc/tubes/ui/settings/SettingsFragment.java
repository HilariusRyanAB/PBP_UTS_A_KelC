package com.kelompokc.tubes.ui.settings;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.kelompokc.tubes.LoginActivity;
import com.kelompokc.tubes.R;

public class SettingsFragment extends Fragment
{
    private String CHANNEL_ID="Channel 1";
    MaterialButton logOutBtn;
    private SwitchMaterial myswitch;
    MaterialButton exitBtn;
    MaterialButton tentangBtn;

    public static final String SHARE_PREFS = "SharedPrefs";
    public static final String SWITCH1 = "switch1";


    public boolean switchOnOff;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        myswitch = (SwitchMaterial) root.findViewById(R.id.myswitch);
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().getApplicationContext().setTheme(R.style.AppTheme);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().getApplicationContext().setTheme(R.style.AppTheme);
                }
                saveData(b);
            }
        });
        logOutBtn = root.findViewById(R.id.button_logOut);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext()).setTitle("Log Out").setMessage("Are You Sure?")
                        .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                createNotificationChannel();
                                addNotification();
                                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create().show();
            }
        });

        exitBtn = root.findViewById(R.id.button_exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                System.exit(0);
            }
        });


        tentangBtn = root.findViewById(R.id.button_tentang);
        tentangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return root;
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getApplicationContext());
        alertDialogBuilder.setTitle("Atma Perpus");
        alertDialogBuilder
                .setMessage("Sebuah aplikasi yang akan membantu anda untuk mempermudah meminjam buku di perpustakan")
                .setMessage("Atma Perpus dengan fitur yang memadai mempermudah peminjaman dan pengembalian buku")
                .setMessage("dengan sistem yang mudah digunakan untuk siapa saja.")
                .setCancelable(false)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Channel 1";
            String description = "This Is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Goodbye").setContentText("Comeback Again...").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent notificationIntent = new Intent(getContext(), LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void saveData(boolean b)
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH1, b);
        editor.apply();
    }

    public void loadData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, getContext().MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
        myswitch.setChecked(switchOnOff);
    }
}