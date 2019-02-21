package com.example.user.bogartsbentelogmobile;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.bogartsbentelogmobile.Model.Food;
import com.example.user.bogartsbentelogmobile.Model.Order;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.example.user.bogartsbentelogmobile.Common.Common.currUser;

public class FoodDetail extends AppCompatActivity {

    TextView food_Name, food_Desc, food_price;
    ImageView food_Image;
    Food currFood;

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton cartButton;
    ElegantNumberButton elegantNumberButton;

    String foodID = "";

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        elegantNumberButton = findViewById(R.id.number_button);

        food_Desc = (TextView)findViewById(R.id.food_detail_desc);
        food_Name = (TextView)findViewById(R.id.food_detail_name);
        food_price = (TextView)findViewById(R.id.food_detail_price);
        food_Image = (ImageView)findViewById(R.id.food_detail_img);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedBar);

        if(getIntent() != null){
            foodID = getIntent().getStringExtra("FoodID");
            Toast.makeText(FoodDetail.this,"id " +foodID,Toast.LENGTH_SHORT).show();
        }

        getDetailFood(foodID);

        cartButton = findViewById(R.id.btnCart);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FoodDetail.addOrderToCart(new Order(
                        foodID,
                        currFood.getName(),
                        elegantNumberButton.getNumber(),        // quantity
                        currFood.getPrice()
                ));
                Toast.makeText(FoodDetail.this,"Successfully Added to cart!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDetailFood (String foodID) {

        CollectionReference foodCollection = db.collection("Food");
        DocumentReference docRef = foodCollection.document(foodID);


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                currFood = documentSnapshot.toObject(Food.class);
                Picasso.get().load(currFood.getImage()).into(food_Image);

                food_price.setText(currFood.getPrice());
                food_Desc.setText(currFood.getDescription());
                food_Name.setText(currFood.getName());
                collapsingToolbarLayout.setTitle(currFood.getName());

            }
        });

    }

        private static void addOrderToCart(Order order) {
//        Random random = new Random();

            // order.getproductID if else

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("ProductID", order.getProductID());
            dataMap.put("productName", order.getProductName());
            dataMap.put("quantity", order.getQuantity());
            dataMap.put("price", order.getPrice());
            dataMap.put("latestUpdateTimestamp", FieldValue.serverTimestamp());

            CollectionReference ref = db.collection("Users").document(currUser.getID()).collection("Cart");

            db.collection("Users").document(currUser.getID()).collection("Cart").document()
                    .set(dataMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
    }
}
