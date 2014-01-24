package com.platforms.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.async.ATPhotoUpload;
import com.platforms.async.ATProfile;
import com.platforms.R;
import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;
import com.platforms.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class ProfileActivity extends Activity{

    String tag;
    Button profileSegment;
    Button photoSegment;
    RelativeLayout loading;
    RelativeLayout photo_layout;
    ImageView privatePhoto;
    ImageView publicPhoto;
    TextView privatePhotoLabel;
    TextView publicPhotoLabel;

    LinearLayout profile_layout;
    SlideHolder mSlideHolder;
    NavMenu navMenu;
    EditText nameField;
    EditText lastField;
    EditText genderField;
    EditText birthField;
    EditText homePhoneField;
    EditText cellPhoneField;
    EditText addressField;
    EditText cityField;
    EditText stateField;
    private static final int CAMERA_REQUEST = 1888;
    private static final int ACTIVITY_SELECT_IMAGE = 1889;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        navMenu = new NavMenu(this, mSlideHolder, 10);

        profileSegment = (Button)findViewById(R.id.leftSegment);
        photoSegment = (Button)findViewById(R.id.rightSegment);
        profile_layout = (LinearLayout)findViewById(R.id.profile_layout);
        photo_layout = (RelativeLayout)findViewById(R.id.photo_layout);
        privatePhotoLabel = (TextView)findViewById(R.id.private_photo_label);
        publicPhotoLabel = (TextView)findViewById(R.id.public_photo_label);

        privatePhoto = (ImageView)findViewById(R.id.private_photo);
        publicPhoto = (ImageView)findViewById(R.id.public_photo);
        nameField = (EditText)findViewById(R.id.nameField);
        lastField = (EditText)findViewById(R.id.lastField);
        genderField = (EditText)findViewById(R.id.genderField);
        birthField = (EditText)findViewById(R.id.birthField);
        homePhoneField = (EditText)findViewById(R.id.homePhoneField);
        cellPhoneField = (EditText)findViewById(R.id.cellPhoneField);
        addressField = (EditText)findViewById(R.id.adressField);
        cityField = (EditText)findViewById(R.id.cityField);
        stateField = (EditText)findViewById(R.id.stateField);
        loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);

        ATProfile ATP = new ATProfile(Endpoints.getProfile, this, null, loading);
        ATP.execute();

        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ATPhotoUpload ATPU = new ATPhotoUpload(Endpoints.getPhoto, this, null, null,loading);
            ATPU.execute();
        }

        privatePhotoLabel.setText(Html.fromHtml("<b>Private Photo</b><br>Shown only to your private community"));
        publicPhotoLabel.setText(Html.fromHtml("<b>Public Photo</b><br>Shown to the public community"));
    }

    public void onProfile(View view){
        profile_layout.setVisibility(View.VISIBLE);
        photo_layout.setVisibility(View.GONE);
        Utils.setBackgroundBySDK(photoSegment, this.getResources().getDrawable(R.drawable.right_segment_button_un));
        Utils.setBackgroundBySDK(profileSegment, this.getResources().getDrawable(R.drawable.left_segment_button_sel));
        photoSegment.setTextColor(Color.parseColor("#a5aaac"));
        profileSegment.setTextColor(Color.parseColor("#ffffff"));
    }

    public void onPhoto(View view){
        profile_layout.setVisibility(View.GONE);
        photo_layout.setVisibility(View.VISIBLE);
        Utils.setBackgroundBySDK(photoSegment, this.getResources().getDrawable(R.drawable.right_segment_button_sel));
        Utils.setBackgroundBySDK(profileSegment, this.getResources().getDrawable(R.drawable.left_segment_button_un));
        photoSegment.setTextColor(Color.parseColor("#ffffff"));
        profileSegment.setTextColor(Color.parseColor("#a5aaac"));
    }

    public void onUpdateProfile(View view){
        preparePostData();
    }

    public void getProfileResponse(JSONObject json){
        int result = 0;
        try {
            result = json.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 1){
            try {
                nameField.setText(json.getString("first_name"));
                lastField.setText(json.getString("last_name"));
                genderField.setHint(guessGender(json.getInt("gender")));
                birthField.setHint(fromUnixToString(json.getLong("date_of_birth")));
                homePhoneField.setText(json.getString("home_phone"));
                cellPhoneField.setText(json.getString("cell_phone"));
                addressField.setText(json.getString("street"));
                cityField.setText(json.getString("city"));
                stateField.setText(json.getString("state"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void preparePostData(){
        JSONObject data = new JSONObject();
        try {
            data.put("first_name", nameField.getText().toString());
            data.put("last_name", lastField.getText().toString());
            data.put("home_phone", homePhoneField.getText().toString());
            data.put("cell_phone", cellPhoneField.getText().toString());
            data.put("street", addressField.getText().toString());
            data.put("city", cityField.getText().toString());
            data.put("state", stateField.getText().toString());

            Reachability a = new Reachability(this);
            if(a.isOnline()){
                ATProfile ATP = new ATProfile(Endpoints.postProfile, this, data, loading);
                ATP.execute();
            }
        } catch (JSONException e) {
            Log.v("CL", "exception " + e);
            Utils.alertView("Somthing went wrong, try again", this);
            e.printStackTrace();
        }

    }

    public void postProfileResponse(JSONObject json){
        int result = 0;
        try {
            result = json.getInt("result");
            Log.v("CL", "result " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == 0){
            Utils.alertView("Error occurred, try again", this);
        }
    }

    public void getPhoto(View view){
        tag = view.getTag().toString();
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Dialog Button");
        alertDialog.setMessage("This is a three-button dialog!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getPhotoFromCamera();
                alertDialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getPhotoFromGallery();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void getPhotoFromCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    public void getPhotoFromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap photo = Utils.scaleBitmap(BitmapFactory.decodeFile(filePath), 400, 400);
                    if (tag.equals("private_image")){
                        privatePhoto.setImageBitmap(photo);
                    }else if(tag.equals("public_image")){
                        publicPhoto.setImageBitmap(photo);
                    }

                    String endPoint = Endpoints.postPhoto;
                    Reachability a = new Reachability(this);
                    if(a.isOnline()){
                        ATPhotoUpload ATPU = new ATPhotoUpload(endPoint, this, photo, tag.toString(), loading);
                        ATPU.execute();
                    }
                }
            case CAMERA_REQUEST : if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap photo = Utils.scaleBitmap((Bitmap) data.getExtras().get("data"), 400, 400);
                if (tag.equals("private_image")) {
                    privatePhoto.setImageBitmap(photo);

                }else if(tag.equals("public_image")){
                    publicPhoto.setImageBitmap(photo);

                }

                String endPoint = Endpoints.postPhoto;
                Reachability a = new Reachability(this);
                if(a.isOnline()){
                    ATPhotoUpload ATPU = new ATPhotoUpload(endPoint, this, photo, tag.toString(), loading);
                    ATPU.execute();
                }
            }
        }

    }


//    public void initSpinner(String[] data){
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner cancerType = (Spinner) findViewById(R.id.cancerType);
//        cancerType.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }

    public String fromUnixToString(long unixTime){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy ':' h:mm a");
        return sdf.format(unixTime*1000);
    }

    public String guessGender(int gender){
        if (gender == 1) return "Male";
        return "Female";
    }

    public void photoResponse(Bitmap publicImage, Bitmap privateImage){
           publicPhoto.setImageBitmap(Utils.scaleBitmap(publicImage, 400, 400));
           privatePhoto.setImageBitmap(Utils.scaleBitmap(privateImage, 400, 400));
    }


    public void showMenu(View view){
        mSlideHolder.toggle();
    }


}
