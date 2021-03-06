package com.example.user.bogartsbentelogmobile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.user.bogartsbentelogmobile.Interface.ItemClickListener;
import com.example.user.bogartsbentelogmobile.Model.Category;
import com.example.user.bogartsbentelogmobile.Model.Food;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class FoodMenu extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerFoodAdapter adapter;
    private RecyclerFoodAdapter searchAdapter;
    private MaterialSearchBar materialSearchBar;
    RecyclerView recylerView;
    List<String> searchList =  new ArrayList<>();
    String categoryID, categName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        if(getIntent() != null){
            categoryID = getIntent().getStringExtra("CategID");
            categName = getIntent().getStringExtra("CategName");
            Toast.makeText(FoodMenu.this,"id " +categoryID,Toast.LENGTH_SHORT).show();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_foodmenu);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categName);
        }
        setUpRecyclerView();
        loadFoodData(categoryID);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){

        Intent i = new Intent(FoodMenu.this, Home.class);
        startActivity(i);

    }

    private void setUpRecyclerView() {

        recylerView = findViewById(R.id.recycle_food_menu);
        recylerView.setHasFixedSize(true);
        recylerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadFoodData(String categoryID) {

        Query query = db.collection("Food").whereEqualTo("CategID", categoryID);

        FirestoreRecyclerOptions<Food> options = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();


        adapter = new RecyclerFoodAdapter(options, this);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClickItemListener(DocumentSnapshot snapshot, int position) {
                String id = snapshot.getId();

                Intent foodDetail = new Intent(FoodMenu.this,FoodDetail.class);
                foodDetail.putExtra("FoodID", id);
                Toast.makeText(FoodMenu.this, "foodid"+ id, Toast.LENGTH_SHORT).show();
                startActivity(foodDetail);

            }
        });
        recylerView.setAdapter(adapter);
    }

    @Override


    protected void onStart (){
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop (){
        super.onStop();
        adapter.stopListening();

    }
}

