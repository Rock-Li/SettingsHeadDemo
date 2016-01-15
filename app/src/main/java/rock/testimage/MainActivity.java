package rock.testimage;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView iv_1;
    public static final int SET_ZOOM_BY_PHOTO = 16;
    public static final int SET_HEADER_CODE = 14;
    private Uri currUri;// 当前手机拍摄图片的uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                currUri = getOutputUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currUri);
                startActivityForResult(intent, SET_HEADER_CODE);
            }
        });
        iv_1 = (ImageView) findViewById(R.id.iv_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE) {
            Uri selectedImage = data.getData();
            cropImageUri(selectedImage);
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            iv_1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        } else if (requestCode == SET_ZOOM_BY_PHOTO) {
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rock.testimage/photo_headpic.jpg");
            iv_1.setImageBitmap(bitmap);
        } else if (requestCode == SET_HEADER_CODE) {
            String path = currUri.getEncodedPath();
            Log.d("pb", "path : " + path);

            File mFile = new File(currUri.getEncodedPath());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, mFile.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION, mFile.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, mFile.getAbsolutePath());
            values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, (new File(mFile.getParent())).getName());
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Uri uri = Uri.fromFile(new File(path));
            cropImageUri(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void cropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri());
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, SET_ZOOM_BY_PHOTO);
    }

    /**
     * 获得输出路径
     *
     * @return
     */
    private Uri getOutputUri() {
        Uri uri = null;
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {

        }
        File eFile = Environment.getExternalStorageDirectory();
        File mDirectory = new File(eFile.toString() + File.separator + getPackageName());
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
        File imageFile = new File(mDirectory, "photo_headpic.jpg");
        uri = Uri.fromFile(imageFile);
        Log.d("aaaa", uri.toString());
        return uri;
    }
}
