package com.example.labretrofit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.labretrofit.model.ResComic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateComic extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private EditText edName, edNameTg,edDes,edYearXB;
    private Button btn_AnhBia,btnAnhNd,btnThemTruyen;
    private ImageView imgAnhBia,imgAnhNd;
    private Uri mUriImg;
    List<Uri> mUriImgND = new ArrayList<Uri>();
    private Uri uri;
    private String strRealPath1;
    private File file1;
    private  RequestBody requestBodyImgND;

    private List <MultipartBody.Part> partImgNd = new ArrayList<>();
    private String id;



    private ActivityResultLauncher<Intent> mIntentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if (data == null){
                    return;
                }
                Uri uri = data.getData();
                mUriImg = uri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imgAnhBia.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Nếu người dùng đã chọn nhiều ảnh
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Xử lý ảnh đã chọn
                    Log.d("imgnd",imageUri.toString());
                    mUriImgND.add(imageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                        imgAnhNd.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (data.getData() != null) {
                // Nếu người dùng đã chọn một ảnh duy nhất
                Uri imageUri = data.getData();
                mUriImgND.add(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    imgAnhNd.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Xử lý ảnh đã chọn
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comic);
        Anhxa();
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("obj_id");
        String name = bundle.getString("obj_name");
        String nametg = bundle.getString("obj_nametg");
        String des = bundle.getString("obj_des");
        String yearxb = bundle.getString("obj_yearxb");
        String img = bundle.getString("obj_img");
        String[] imgnd = bundle.getStringArray("obj_imgnd");

        edName.setText(name);
        edNameTg.setText(nametg);
        edDes.setText(des);
        edYearXB.setText(yearxb);
        Glide.with(getApplicationContext()).load("http://10.0.2.2:3000"+img).into(imgAnhBia);
        Glide.with(getApplicationContext()).load("http://10.0.2.2:3000"+imgnd).into(imgAnhBia);

        btn_AnhBia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermisson();
            }
        });
        btnAnhNd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImgContent();
            }
        });
        btnThemTruyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUpdateComic();
            }
        });
    }
    private void onClickRequestPermisson() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openImg();
            return;
        }
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openImg();
        }
        else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,MY_REQUEST_CODE);
        }
    }
    private void onImgContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_REQUEST_CODE);
    }
    private void openImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mIntentActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==MY_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openImg();
            }
        }

    }
    private void Anhxa(){
        edName = findViewById(R.id.edNamenews);
        edNameTg = findViewById(R.id.edNameTGnews);
        edDes = findViewById(R.id.edDesnews);
        edYearXB = findViewById(R.id.edYearXBnews);
        btn_AnhBia = findViewById(R.id.btn_AnhBianews);
        btnAnhNd = findViewById(R.id.btn_AnhNdnews);
        imgAnhBia = findViewById(R.id.imgAnhBianews);
        imgAnhNd = findViewById(R.id.imgAnhNdnews);
        btnThemTruyen = findViewById(R.id.btnUpdateTruyennews);
    }
    private void CallUpdateComic(){
        String name = edName.getText().toString();
        String namtg = edNameTg.getText().toString();
        String des = edDes.getText().toString();
        String yearxb = edYearXB.getText().toString();
        RequestBody requestBodyName = RequestBody.create(MediaType.parse("multipart/form-data"),name);
        RequestBody requestBodyNameTG = RequestBody.create(MediaType.parse("multipart/form-data"),namtg);
        RequestBody requestBodyDes = RequestBody.create(MediaType.parse("multipart/form-data"),des);
        RequestBody requestBodyYearxb = RequestBody.create(MediaType.parse("multipart/form-data"),yearxb);

        String strRealPath = RealPathUtil.getRealPath(this,mUriImg);
        File file = new File(strRealPath);
        RequestBody requestBodyImg = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part partImg = MultipartBody.Part.createFormData("img",file.getName(),requestBodyImg);


        for (int i = 0 ; i < mUriImgND.size() ;i++){
            uri = mUriImgND.get(i);
            strRealPath1 = RealPathUtil.getRealPath(getApplicationContext(),uri);
            file1 = new File(strRealPath1);
            requestBodyImgND = RequestBody.create(MediaType.parse("multipart/form-data"),file1);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("imgnd", file1.getName(), requestBodyImgND);
            partImgNd.add(imagePart);
            Log.d("Sau khi lap" , requestBodyImgND.toString());
        }

        ApiService.apiService.updateComic(id,requestBodyName,requestBodyNameTG,requestBodyDes,requestBodyYearxb,partImg,partImgNd).enqueue(new Callback<ResComic>() {
            @Override
            public void onResponse(Call<ResComic> call, Response<ResComic> response) {
                Toast.makeText(UpdateComic.this,response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResComic> call, Throwable t) {
                Toast.makeText(UpdateComic.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Mang anh looix ",t.getMessage());
                Log.d("Dinh vi mang anh looix ",t.getLocalizedMessage());
            }
        });
    }
}