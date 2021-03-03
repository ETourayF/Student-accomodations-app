package com.example.homergraphicstest3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Home_Rec_View_Adpt extends RecyclerView.Adapter<Home_Rec_View_Adpt.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Properties> data;
    private OnItemClickListener clickListener;
    private OnLongClickListener longClickListener;

    public Home_Rec_View_Adpt(Context context, List<Properties> data){
        this.layoutInflater =LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.home_rec_view_adpt, parent, false);
        return new ViewHolder(view, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Properties property = data.get(position);
        String propAddress = "";

        if(property.getAddLine_2() != ""){
            propAddress = property.getAddLine_1() + ", " + property.getAddLine_2()  + ", " + property.getPostCode();
        }
        else {
            propAddress = property.getAddLine_1() + ", " + property.getPostCode();
        }
        holder.txt_Address.setText(propAddress);
        holder.txt_Rent.setText(String.format("£%s %s", property.getPrice(), property.getPayScheme()));
        Picasso.get().load(property.getImgUrl()).into(holder.propertyImg);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface OnLongClickListener{
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
    public   void setOnItemLongClickListener(OnLongClickListener listener) {longClickListener = listener;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_Address, txt_Rent;
        ImageView propertyImg;
        ConstraintLayout displayProp;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener, final OnLongClickListener longClickListener) {
            super(itemView);
            propertyImg = itemView.findViewById(R.id.display_img);
            txt_Address = itemView.findViewById(R.id.address_txt);
            txt_Rent = itemView.findViewById(R.id.rent_txt);
            displayProp = itemView.findViewById(R.id.displayProp_constraint);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            longClickListener.onLongClick(position);
                        }
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
