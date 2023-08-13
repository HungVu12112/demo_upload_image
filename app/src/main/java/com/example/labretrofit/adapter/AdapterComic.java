package com.example.labretrofit.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.labretrofit.ApiService;
import com.example.labretrofit.R;
import com.example.labretrofit.UpdateComic;
import com.example.labretrofit.model.Comics;
import com.example.labretrofit.model.ResComic;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterComic extends RecyclerView.Adapter<AdapterComic.ComicAdapterHolder> {
    private List<Comics> mList;
    private Context mContext;

    public AdapterComic(List<Comics> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ComicAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comic, parent, false);
        return new ComicAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicAdapterHolder holder, int position) {
        Comics comics = mList.get(position);
        if (comics == null) {
            return;
        }
        Glide.with(mContext).load("http://10.0.2.2:3000" + comics.getImg()).into(holder.img);
        holder.tvName.setText(comics.getName());
        holder.tvNameTg.setText(comics.getNametg());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateComic.class);
                Bundle bundle = new Bundle();
                bundle.putString("obj_id", comics.get_id());
                bundle.putString("obj_name", comics.getName());
                bundle.putString("obj_nametg", comics.getNametg());
                bundle.putString("obj_des", comics.getDes());
                bundle.putString("obj_yearxb", comics.getYearxb());
                bundle.putString("obj_img" ,comics.getImg());
                bundle.putStringArray("obj_imgnd" ,comics.getImgnd());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Thông báo !");
                builder.setMessage("Bạn đã chắc chắn với quyết định này chưa ! ");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /// xử lý xóa trong này
                        CallDeleteComic(comics.get_id());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ///Xử lý cancel
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public void setData(List<Comics> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public class ComicAdapterHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView tvName, tvNameTg;

        public ComicAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvnamecomic);
            img = itemView.findViewById(R.id.imgcomic);
            tvNameTg = itemView.findViewById(R.id.tvnametg);
        }
    }
    private void CallDeleteComic(String idcomic){
        ApiService.apiService.deleteComic(idcomic).enqueue(new Callback<ResComic>() {
            @Override
            public void onResponse(Call<ResComic> call, Response<ResComic> response) {
                Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResComic> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
