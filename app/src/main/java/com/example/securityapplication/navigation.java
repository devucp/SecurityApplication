package com.example.securityapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class navigation extends AppCompatActivity {

    int count=0,aa;
    static User newUser=new User();
    Boolean is_home=true;

    SQLiteDBHelper db=new SQLiteDBHelper(navigation.this);
    public static Boolean test=false;

    Menu optionsMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_continer,new home_fragment()).commit();
        //sqlite db code here
        if((aa=db.numberOfRows())==0)
        {  getData(1);
        Log.d("SQL","No. of rows in navigation "+aa);}
        else
        {
            Log.d("checking","oncreate option menu 3 is running");
            getData(2);
            if(db.getTestmode())
            {
                Log.d("checking","oncreate option menu 2 is running");
                test=true;
            }
        }
    }


    public void getData(final int check){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference();
        DatabaseReference childref = databaseReference.child("Users").child(uid);
        databaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newUser=dataSnapshot.getValue(User.class);

                if(check==1) {
                    db.addUser(newUser);
                    Log.d("FirebaseUsername",newUser.getName()+" 1 "+newUser.getEmail());
                    db.addsosContacts(newUser.getSosContacts()); //to fetch SOSContacts from Firebase
                }
                else if(check==2) {
                    Log.d("FirebaseUsername",newUser.getName()+" 2 "+newUser.getEmail());
                    db.updateUser(newUser);}
                    db.addsosContacts(newUser.getSosContacts()); //to fetch SOSContacts from Firebase even if tablepresent
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        optionsMenu=menu;
        MenuItem titem=optionsMenu.findItem(R.id.testmode);
        test=db.getTestmode();
        Log.d("checking","oncreate option menu is running"+db.getTestmode());
        if(test)
            titem.setChecked(true);
        else
            titem.setChecked(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.testmode:
                    if (item.isChecked()) {
                        item.setChecked(false);
                        test = false;
                        db.updatetestmode(test);
                        //Log.d("checking1", String.valueOf(db.getTestmode()) + "home" + is_home);
                        Toast.makeText(this, "Test mode Off", Toast.LENGTH_SHORT).show();
                        if (is_home) {
                            TextView tv = (TextView) findViewById(R.id.textView3);
                            tv.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        item.setChecked(true);
                        test = true;
                        db.updatetestmode(test);
                        //Log.d("checking2", String.valueOf(db.getTestmode()));
                        Toast.makeText(this, "Test mode On", Toast.LENGTH_SHORT).show();
                        if (is_home) {
                            TextView tv = (TextView) findViewById(R.id.textView3);
                            tv.setVisibility(View.VISIBLE);
                        }
                    }


                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        catch (Exception e)
        {
            item.setChecked(db.getTestmode());
            Toast.makeText(this, "Loading.....please wait for a second", Toast.LENGTH_LONG).show();
        }
        finally {
            return true;
        }

    }
    Fragment selectedFragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch(menuItem.getItemId()){
                        case R.id.home:
                            is_home=true;
                            selectedFragment = new home_fragment();
                            break;
                        case R.id.setting:
                            is_home=false;
                            selectedFragment = new setting_fragment();
                            break;
                        case R.id.save:
                            is_home=false;
                            selectedFragment = new saviour_fragment();
                            break;
                        case R.id.profile:
                            is_home=false;
                            selectedFragment = new profile_fragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_continer,
                            selectedFragment).commit();

                    return true;
                }
            };
    @Override
    public void onBackPressed(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(navigation.this);
        a_builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Message");
        alert.show();
    }

    public void sos(View view) {
        startActivity(new Intent(this,sos_page.class));
    }
}
