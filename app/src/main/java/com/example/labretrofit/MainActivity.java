package com.example.labretrofit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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

import com.example.labretrofit.adapter.AdapterComic;
import com.example.labretrofit.model.Comics;
import com.example.labretrofit.model.ListComics;
import com.example.labretrofit.model.ResComic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private RecyclerView recyclerView;
    private AdapterComic adapterComic;
    private List<Comics> mComicsList = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
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

    private ProgressDialog mProgressDialog;

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
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.idrecyclerview);
        refreshLayout = findViewById(R.id.load);
        Anhxa();
        /// Loading
        mProgressDialog = new ProgressDialog(getApplicationContext());
        mProgressDialog.setMessage("Vui lòng đợi một xíu !!!");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                GetComic();
            }
        });
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
                CalladdComic();
            }
        });


    }

    private void onImgContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_REQUEST_CODE);
    }


    private void onClickRequestPermisson() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openImg();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openImg();
        }
        else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,MY_REQUEST_CODE);
        }
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

    private void openImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mIntentActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));

    }

    private void GetComic() {
        ApiService.apiService.getComics().enqueue(new Callback<ListComics>() {
            @Override
            public void onResponse(Call<ListComics> call, Response<ListComics> response) {
                mComicsList = Arrays.asList(response.body().getListComic());
                adapterComic = new AdapterComic(mComicsList, getApplicationContext());
                recyclerView.setAdapter(adapterComic);

            }

            @Override
            public void onFailure(Call<ListComics> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void Anhxa(){
        edName = findViewById(R.id.edName);
        edNameTg = findViewById(R.id.edNameTG);
        edDes = findViewById(R.id.edDes);
        edYearXB = findViewById(R.id.edYearXB);
        btn_AnhBia = findViewById(R.id.btn_AnhBia);
        btnAnhNd = findViewById(R.id.btn_AnhNd);
        imgAnhBia = findViewById(R.id.imgAnhBia);
        imgAnhNd = findViewById(R.id.imgAnhNd);
        btnThemTruyen = findViewById(R.id.btnThemTruyen);
    }
    private void CalladdComic(){
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

        ApiService.apiService.addComic(requestBodyName,requestBodyNameTG,requestBodyDes,requestBodyYearxb,partImg,partImgNd).enqueue(new Callback<ResComic>() {
            @Override
            public void onResponse(Call<ResComic> call, Response<ResComic> response) {
                Toast.makeText(MainActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResComic> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Mang anh looix ",t.getMessage());
                Log.d("Dinh vi mang anh looix ",t.getLocalizedMessage());
            }
        });
    }
}