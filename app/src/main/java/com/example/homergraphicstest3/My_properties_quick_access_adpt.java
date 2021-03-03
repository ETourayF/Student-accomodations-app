package com.example.homergraphicstest3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class My_properties_quick_access_adpt extends RecyclerView.Adapter<My_properties_quick_access_adpt.ViewHolder> {

    LayoutInflater layoutInflater;
    List<Properties> data;

    public My_properties_quick_access_adpt(Context context, List<Properties> data){
        this.layoutInflater =LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public My_properties_quick_access_adpt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.my_properties_quick_access_adpt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull My_properties_quick_access_adpt.ViewHolder holder, int position) {
        Properties property = data.get(position);
        String propAddress = "";

        if(property.getAddLine_2() != ""){
            propAddress = property.getAddLine_1() + ", " + property.getAddLine_2() + ", " + property.getPostCode();
        }
        else {
            propAddress = property.getAddLine_1() + ", " + property.getPostCode();
        }
        holder.myPropertyAddress_txt.setText(propAddress);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView myPropertyAddress_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myPropertyAddress_txt = itemView.findViewById(R.id.myPropertiesAddress_txt);
        }
    }
}
