package com.kelompokc.tubes.ui.settings;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.kelompokc.tubes.LoginActivity;
import com.kelompokc.tubes.MainActivity;
import com.kelompokc.tubes.MapActivity;
import com.kelompokc.tubes.R;
import com.kelompokc.tubes.SplashSreenActivity;

public class SettingsFragment extends Fragment
{
    private String CHANNEL_ID="Channel 1";
    public static final String SHARE_PREFS = "SharedPrefs";
    public static final String SWITCH1 = "switch1";

    private MaterialButton logOutBtn;
    private SwitchMaterial myswitch;
    private MaterialButton aboutBtn;
    private MaterialButton map;
    private boolean switchOnOff;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        myswitch = (SwitchMaterial) root.findViewById(R.id.myswitch);
        logOutBtn = root.findViewById(R.id.button_logOut);
        aboutBtn = root.findViewById(R.id.button_aboutUs);
        map = root.findViewById(R.id.button_map);

        loadData();

        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                saveData(b);

                if(b)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getContext().setTheme(R.style.darkTheme);
                }

                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getContext().setTheme(R.style.AppTheme);
                }

                startActivity(new Intent(root.getContext(), MainActivity.class));
                getActivity().finish();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                logOutDialog();
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                aboutUsDialog();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MapActivity.class));
            }
        });

        return root;
    }

    public void logOutDialog()
    {
        new AlertDialog.Builder(getContext()).setTitle("Log Out").setMessage("Are You Sure?")
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        FirebaseAuth.getInstance().signOut();
                        createNotificationChannel();
                        addNotification();
                        getActivity().finish();
                        startActivity(new Intent(getContext(), SplashSreenActivity.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })
                .create().show();
    }

    public void aboutUsDialog()
    {
        new AlertDialog.Builder(getContext()).setTitle("Atma Library")
                .setMessage("Sebuah aplikasi yang akan membantu anda untuk mempermudah meminjam buku di perpustakan.\n\n" +
                        "Atma Library dengan fitur yang memadai mempermudah peminjaman dan pengembalian buku bagi seluruh mahasiswa.\n\n"
                        +"Atma Library juga hadir dengan sistem yang mudah digunakan untuk siapa saja.")
                .setNegativeButton("Close", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                }).create().show();
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
                .setContentTitle("Goodbye").setContentText("Please Comeback Again...").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent notificationIntent = new Intent(getContext(), LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void saveData(boolean selected)
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH1, selected);
        editor.commit();
    }

    public void loadData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, getContext().MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
        if(switchOnOff)
        {
            getContext().setTheme(R.style.darkTheme);
        }

        else
        {
            getContext().setTheme(R.style.AppTheme);
        }

        myswitch.setChecked(switchOnOff);
    }
}