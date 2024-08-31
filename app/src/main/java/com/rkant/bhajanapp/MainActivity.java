package com.rkant.bhajanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.rkant.bhajanapp.Favourites.FavouriteBookmarked;
import com.rkant.bhajanapp.FirstActivities.RecyclerAdapter;
import com.rkant.bhajanapp.secondActivities.DataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MenuItem menuItem,favourite_bhajan_menuItem;
    SearchView searchView;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerCustomAdapter;
    Boolean backPressed=false;
   ArrayList<DataHolder> arrayList;
   ActionBarDrawerToggle toggle;
   DrawerLayout drawerLayout;
   NavigationView navigationView;
ArrayList<com.rkant.bhajanapp.FirstActivities.DataHolder> nepaliNumbers;
AdapterView.OnItemSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavigationView();


        recyclerView=findViewById(R.id.recyclerView);
        arrayList=new ArrayList<>();
        nepaliNumbers=new ArrayList<>();
        try {
            addData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        settingAdapter();



        // Changing Action Bar colour
       getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff6200ed));
    }

    private void setNavigationView() {
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_navigation, R.string.close_navigation);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Handle Home action
                        break;
                    case R.id.nav_gallery:
                        // Handle Gallery action
                        break;
                    case R.id.nav_slideshow:
                        // Handle Slideshow action
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }




    private void settingAdapter() {
        recyclerCustomAdapter=new RecyclerAdapter(arrayList,listener,MainActivity.this,nepaliNumbers);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerCustomAdapter);


    }

    public void addData() throws IOException, JSONException {
        String jsonDataString=readDataFromFile(R.raw.bhajan_list);
        JSONArray jsonArray=new JSONArray(jsonDataString);
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            String nepali_bhajan=jsonObject.getString("bhajan_nepali");
            String bhajan_english_for_search=jsonObject.getString("bhajan_english");
            String id=jsonObject.getString("id");
            arrayList.add( new DataHolder(nepali_bhajan,bhajan_english_for_search,id));
        }
        String jsonData=readDataFromFile(R.raw.nepali_numbers);
        JSONArray array=new JSONArray(jsonData);
        for (int j=0;j<array.length();j++){
            String strr=array.getString(j);
            nepaliNumbers.add(new com.rkant.bhajanapp.FirstActivities.DataHolder(strr));
           // Toast.makeText(this, ""+strr, Toast.LENGTH_SHORT).show();

        }


    }

    public String readDataFromFile(int i) throws IOException {

        InputStream inputStream=null;
        StringBuilder builder=new StringBuilder();
        try{
            String jsonString=null;
            inputStream=getResources().openRawResource(i);
            BufferedReader bufferedReader=new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"));
            while ((jsonString=bufferedReader.readLine()) !=null){
                builder.append(jsonString);
            }
        }
        finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return new String(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        menuItem =menu.findItem(R.id.search_bar);
        favourite_bhajan_menuItem=menu.findItem(R.id.favourite_bhajan);
        searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        favourite_bhajan_menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent=new Intent(MainActivity.this, FavouriteBookmarked.class);
                startActivity(intent);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                menuItem.collapseActionView();
                searchView.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<DataHolder> filteredList=new ArrayList<>();
                for(int a=0; a< arrayList.size(); a++){
                    DataHolder item=arrayList.get(a);
                    if (item.getBhajan_name_nepali().toLowerCase().contains(s.toString().toLowerCase()) || item.getBhajan_name_english().toLowerCase().contains(s.toString().toLowerCase()) ){
                        filteredList.add(item);
                    }
                }
                recyclerCustomAdapter.filterList(filteredList);
                return false;
            }

        });





        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        menuItem.collapseActionView();
        searchView.onActionViewCollapsed();
        if (backPressed) {
            super.onBackPressed();
        } else {
            backPressed = true;

        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backPressed = false;
            }
        }, 2500);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




