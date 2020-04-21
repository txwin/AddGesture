package com.example.addgesture;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class GestureFragment extends Fragment implements GestureOverlayView.OnGestureListener {
    private String TAG_LISTENER = "";
    private View view, view_gesture;
    private EditText editText;
    private ImageView imageView;
    private Gesture gesture;
    private Bitmap bitmap;
    private TextView textView;
    private GestureLibrary gestureLibraries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gesture, container, false);
        textView = view.findViewById(R.id.textView4);
        //初始化
        choose("Add");
        return view;
    }

    public void choose(String TAG) {
        switch (TAG) {
            case "Add":
                textView.setText("请绘制需要添加的手势");
                TAG_LISTENER = TAG;
                //获取GestureOverlayView绘制区域对象
                GestureOverlayView gestureOverlayView = view.findViewById(R.id.gestureview);
                //设置绘制线条粗细
                gestureOverlayView.setGestureStrokeWidth(20);
                //设置监听器，监听手势是否绘制结束
                gestureOverlayView.addOnGestureListener(this);
                break;
            case "Distinguish":
                textView.setText("请绘制需要识别的手势");
                TAG_LISTENER = TAG;
                //动态请求读写权限；注意在Fragment中不能使用ActivityCompat
                // .requestPermissions动态获取，否则无法回调onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (gesture != null) {
                        gestureLibraries = GestureLibraries.fromFile("/sdcard" +
                                "/mygestures");//设置存储手势位置
                        gestureLibraries.addGesture(editText.getText().toString(), gesture);
                        //添加手势到手势库
                        boolean result = gestureLibraries.save();//保存手势
                        if (result) {
                            Toast.makeText((MainActivity) getActivity(), "保存手势成功",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText((MainActivity) getActivity(), "保存手势失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else if (requestCode == 2) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    gestureLibraries = GestureLibraries.fromFile("/sdcard" +
                            "/mygestures");//设置存储手势位置
                    GestureOverlayView gestureOverlayView = view.findViewById(R.id.gestureview);
                    gestureOverlayView.addOnGestureListener(this);
                }
            }
        }
    }

    @Override
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

    }

    @Override
    public void onGesture(GestureOverlayView overlay, MotionEvent event) {

    }

    @Override
    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
        switch (TAG_LISTENER) {
            case "Add":
                gesture = overlay.getGesture(); //获取手势
                view_gesture = getLayoutInflater().inflate(R.layout.save_gesture, null);
                imageView = view_gesture.findViewById(R.id.imageView);
                editText = view_gesture.findViewById(R.id.editText);

                //根据gesture对象包含的手势信息创建位图并显示
                bitmap = gesture.toBitmap(128, 128, 20, Color.GREEN);
                imageView.setImageBitmap(bitmap);
//                Log.e("TAG", "onClick: ");
                new AlertDialog.Builder((MainActivity) getActivity()).setView(view_gesture).setPositiveButton("Save"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //动态申请存储权限
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                Log.e("TAG", "onClick: ");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case "Distinguish":
                //加载手势库
                if (gestureLibraries.load()) {
                    //识别绘制的手势，Prediction为相似度对象，相似度从高到低排序
                    ArrayList<Prediction> pre = gestureLibraries.recognize(overlay.getGesture());
                    if (!pre.isEmpty()) {
                        Prediction prediction = pre.get(0);//获取相似度最高的对象

                        //相似度大于某个值就做出相应的判断
                        if (prediction.score > 4) {
                            Toast.makeText((MainActivity) getActivity(),
                                    prediction.name, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText((MainActivity) getActivity(),
                                    "手势匹配不成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText((MainActivity) getActivity(), "手势库加载失败",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

    }
}
