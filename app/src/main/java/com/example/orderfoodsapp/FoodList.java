package com.example.orderfoodsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderfoodsapp.Common.Common;
import com.example.orderfoodsapp.Database.Database;
import com.example.orderfoodsapp.Interface.ItemClickListener;
import com.example.orderfoodsapp.Model.Food;
import com.example.orderfoodsapp.Model.Order;
import com.example.orderfoodsapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search Func
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Gothic.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_food_list);

        //get database from FB
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        //localDatabase
        localDB = new Database(this);

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //swipe to refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null) {
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else {
                        Toast.makeText(FoodList.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null) {
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else {
                        Toast.makeText(FoodList.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });

        //search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search Somethings...");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.startListening();
    }

    private void startSearch(CharSequence text) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("Name").equalTo(text.toString()), Food.class).build(); // compare name

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, int i, @NonNull Food food) {
                foodViewHolder.food_name.setText(food.getName());
                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start activity of food details
                        Intent foodDetails = new Intent(FoodList.this, FoodDetail.class);
                        foodDetails.putExtra("FoodId", searchAdapter.getRef(position).getKey()); //send FoodId to new Activity
                        startActivity(foodDetails);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        searchAdapter.startListening();
        searchAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(searchAdapter); // set adapter for recycle view is search result

    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("MenuId").equalTo(categoryId), Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i, @NonNull final Food food) {
                foodViewHolder.food_name.setText(food.getName());
                foodViewHolder.food_price.setText(String.format("%s đ", food.getPrice()));
                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);

                final Food local = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

                //Quick Cart

                foodViewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExists = new Database(getBaseContext()).checkFoodExits(adapter.getRef(i).getKey(), Common.currentUser.getPhone());
                        if (!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(i).getKey(),
                                    food.getName(),
                                    "1",
                                    food.getPrice(),
                                    food.getDiscount(),
                                    food.getImage()
                            ));
                            Toast.makeText(FoodList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        } else {
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(i).getKey());
                        }

                    }
                });

                //Add Favorites
                if (localDB.isFavorites(adapter.getRef(i).getKey(), Common.currentUser.getPhone()))
                    foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of Favorites
                foodViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!localDB.isFavorites(adapter.getRef(i).getKey(), Common.currentUser.getPhone())) {
                            localDB.addToFavorites(adapter.getRef(i).getKey(), Common.currentUser.getPhone());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "" + food.getName() + " was added to Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(i).getKey(), Common.currentUser.getPhone());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "" + food.getName() + " was removed to Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
