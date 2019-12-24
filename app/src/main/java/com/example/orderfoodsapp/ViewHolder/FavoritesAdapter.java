package com.example.orderfoodsapp.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodsapp.Common.Common;
import com.example.orderfoodsapp.Database.Database;
import com.example.orderfoodsapp.FoodDetail;
import com.example.orderfoodsapp.FoodList;
import com.example.orderfoodsapp.Interface.ItemClickListener;
import com.example.orderfoodsapp.Model.Favorites;
import com.example.orderfoodsapp.Model.Food;
import com.example.orderfoodsapp.Model.Order;
import com.example.orderfoodsapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favovites_item, parent, false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder foodViewHolder, final int position) {
        foodViewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        foodViewHolder.food_price.setText(String.format("%s $", favoritesList.get(position).getFoodPrice().toString()));
        Picasso.get().load(favoritesList.get(position).getFoodImage()).into(foodViewHolder.food_image);

        //Quick Cart
        foodViewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).checkFoodExits(favoritesList.get(position).getFoodId().toString(), Common.currentUser.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                            "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()
                    ));

                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(), favoritesList.get(position).getFoodId());
                }
                Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();

            }
        });

        final Favorites local = favoritesList.get(position);
        foodViewHolder.setItemClickListener(new ItemClickListener() {

            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("FoodId", favoritesList.get(position).getFoodId());
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position){
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(int position,Favorites item){
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favorites getItem(int position){
        return favoritesList.get(position);
    }
}
