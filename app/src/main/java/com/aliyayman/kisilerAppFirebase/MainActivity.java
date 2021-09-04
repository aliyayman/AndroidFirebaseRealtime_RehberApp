package com.aliyayman.kisilerAppFirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private RecyclerView rv;
    private FloatingActionButton fab;
    private ArrayList<Kisiler> kisilerList;
    private KisilerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        rv=findViewById(R.id.rv);
        fab=findViewById(R.id.fab);

         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("kisiler");





        toolbar.setTitle("Kişiler Uygulması");
        setSupportActionBar(toolbar);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        kisilerList=new ArrayList<>();

        adapter=new KisilerAdapter(MainActivity.this,kisilerList,myRef);
        rv.setAdapter(adapter);
        tumKisiler();




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertGoster();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_ara);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("aranan kelime",query);
        kisiAra(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.e("anlık kelime",newText);
        kisiAra(newText);
        return false;
    }
    public void alertGoster(){
        LayoutInflater layout=LayoutInflater.from(this);
        View view=layout.inflate(R.layout.alert_tasarim,null);

        EditText edtAd=view.findViewById(R.id.edtAd);
        EditText edtTel=view.findViewById(R.id.edtTel);

        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Kişi ekle");
        ad.setView(view);
        ad.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kisi_ad=edtAd.getText().toString().trim();
                String kisi_tel=edtTel.getText().toString().trim();

                String key=myRef.push().getKey();

                Kisiler kisi=new Kisiler(key,kisi_ad,kisi_tel);
                myRef.push().setValue(kisi);
                //Toast.makeText(getApplicationContext(),kisi_ad+"-"+kisi_tel,Toast.LENGTH_SHORT).show();


            }
        });
        ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.create().show();

    }
    public void tumKisiler(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                kisilerList.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    Kisiler kisi=d.getValue(Kisiler.class);
                    kisi.setKisi_id(d.getKey());


                    kisilerList.add(kisi);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void kisiAra(final String aramaKelime){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                kisilerList.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    Kisiler kisi=d.getValue(Kisiler.class);

                    //Like'lı arama firebase
                    if(kisi.getKisi_ad().contains(aramaKelime)){
                        kisi.setKisi_id(d.getKey());
                        kisilerList.add(kisi);
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}