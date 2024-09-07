package com.rkant.bhajanapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.rkant.bhajanapp.FirstActivities.DataHolder;
import com.rkant.bhajanapp.FirstActivities.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MyFragment extends Fragment {
    ArrayList<com.rkant.bhajanapp.FirstActivities.DataHolder> nepaliNumbers;
    AdapterView.OnItemSelectedListener listener;
    ArrayList<com.rkant.bhajanapp.secondActivities.DataHolder> arrayList;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerCustomAdapter;


    public static MyFragment newInstanceOfFragment(){

       return new MyFragment();
    }
    public void Toster(){
        Toast.makeText(getContext(), "HEHE", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList=new ArrayList<>();
        nepaliNumbers=new ArrayList<>();

        try {
            addData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_my, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView=getActivity().findViewById(R.id.recyclerView_frag);
        settingAdapter();
    }
    private void settingAdapter() {
        recyclerCustomAdapter=new RecyclerAdapter(arrayList,listener,getContext(),nepaliNumbers);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerCustomAdapter);


    }
    public void addData() throws IOException, JSONException {
        StringBuffer buffer=new StringBuffer();
        buffer.append(readDataFromFile(R.raw.bhajan_list));
        JSONArray jsonArray=new JSONArray(buffer.toString());
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            String nepali_bhajan=jsonObject.getString("bhajan_nepali");
            String bhajan_english_for_search=jsonObject.getString("bhajan_english");
            String id=jsonObject.getString("id");
            arrayList.add( new com.rkant.bhajanapp.secondActivities.DataHolder(nepali_bhajan,bhajan_english_for_search,id));
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
    public void onTextChange_my(String s){
        ArrayList<com.rkant.bhajanapp.secondActivities.DataHolder> filteredList=new ArrayList<>();
        for(int a=0; a< arrayList.size(); a++){
            com.rkant.bhajanapp.secondActivities.DataHolder item=arrayList.get(a);
            if (item.getBhajan_name_nepali().toLowerCase().contains(s.toString().toLowerCase()) || item.getBhajan_name_english().toLowerCase().contains(s.toString().toLowerCase()) ){
                filteredList.add(item);
            }
        }
        recyclerCustomAdapter.filterList(filteredList);
    }
}