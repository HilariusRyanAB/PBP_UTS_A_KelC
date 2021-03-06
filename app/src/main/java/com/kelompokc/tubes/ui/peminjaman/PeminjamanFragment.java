package com.kelompokc.tubes.ui.peminjaman;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kelompokc.tubes.database.DatabaseClient;
import com.kelompokc.tubes.model.Buku;
import com.kelompokc.tubes.QRBarcodeActivity;
import com.kelompokc.tubes.adapter.RecyclerViewAdapter;
import com.kelompokc.tubes.R;

import java.util.ArrayList;
import java.util.List;

public class PeminjamanFragment extends Fragment
{
    private RecyclerView recyclerView;
    private RecyclerViewAdapter pModel;
    private FloatingActionButton add;
    private ImageView scan;
    private ArrayList<Buku> tempPinjam = new ArrayList<>();
    private ArrayList<Buku> tempList = new ListPinjam().listPinjam;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_peminjaman, container, false);
        recyclerView = root.findViewById(R.id.recycler_view_peminjaman);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        scan = root.findViewById(R.id.button_qr);
        add = root.findViewById(R.id.button_pinjam);

        //deleteBukuPinjamAll(tempList);
        getBukuPinjam();

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tempPinjam = pModel.getDataBuku();
                if (tempPinjam.isEmpty())
                {
                    Toast.makeText(getContext(), "Silahkan Pilih Buku Yang Akan Dipinjam", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CharSequence[] title = getStringArray(tempPinjam);
                    getDialog(title, tempPinjam.size(), tempPinjam);
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getContext(), QRBarcodeActivity.class));
            }
        });

        return root;
    }

    private void getDialog(CharSequence[] a, int size, final ArrayList<Buku> tempBuku)
    {
        String temp = "";

        for (int i = 0; i < size; i++)
        {
            if (i < size - 1)
            {
                temp = temp + a[i] + "\n\n";
            }
            else
            {
                temp = temp + a[i];
            }
        }
        new AlertDialog.Builder(getContext()).setTitle("Buku Yang Akan Dipinjam").setMessage(temp)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    updateStatusPinjam(tempBuku);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                }
            }).create().show();
    }

    public static String[] getStringArray(List<Buku> input)
    {
        String[] strings = new String[input.size()];

        for (int j = 0; j < input.size(); j++)
        {
            strings[j] = "Judul Buku: " + input.get(j).getJudul();
        }

        return strings;
    }

    public void getBukuPinjam()
    {
        class getBuku extends AsyncTask<Void, Void, List<Buku>>
        {
            @Override
            protected List<Buku> doInBackground(Void... voids)
            {
                List<Buku> bukuList = DatabaseClient
                        .getInstance(getContext())
                        .getDatabase()
                        .bukuDAO()
                        .getAll("tersedia");
                return bukuList;
            }

            @Override
            protected void onPostExecute(List<Buku> tempBuku)
            {
                super.onPostExecute(tempBuku);
                pModel = new RecyclerViewAdapter(getContext(), tempBuku);
                recyclerView.setAdapter(pModel);
                if (tempBuku.isEmpty())
                {
                    addBukuPinjamAll();
                    getBukuPinjam();
                }
            }
        }
        getBuku get = new getBuku();
        get.execute();
    }

    public void addBukuPinjamAll()
    {
        class addBuku extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    DatabaseClient.getInstance(getContext()).getDatabase().bukuDAO().insert(tempList.get(i));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
            }
        }
        addBuku get = new addBuku();
        get.execute();
    }

    public void deleteBukuPinjamAll(final ArrayList<Buku> tempList)
    {
        class deleteBuku extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                for(int i = 0; i < tempList.size(); i++)
                {
                    DatabaseClient
                            .getInstance(getContext())
                            .getDatabase()
                            .bukuDAO()
                            .delete(tempList.get(i).getNoSeri());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Buku Berhasil Dihapus Semua", Toast.LENGTH_SHORT).show();
            }
        }
        deleteBuku buku = new deleteBuku();
        buku.execute();
    }

    public void updateStatusPinjam(final ArrayList<Buku> tempList)
    {
        class updateStatusPinjam extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                for (int i = 0; i < tempList.size(); i++)
                {
                    DatabaseClient
                            .getInstance(getContext())
                            .getDatabase()
                            .bukuDAO()
                            .updateStatus("dipinjam", tempList.get(i).getNoSeri());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Buku Berhasil Dipinjam", Toast.LENGTH_SHORT).show();
                getBukuPinjam();
            }
        }
        updateStatusPinjam status = new updateStatusPinjam();
        status.execute();
    }
}