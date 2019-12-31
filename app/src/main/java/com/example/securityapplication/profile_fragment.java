package com.example.securityapplication;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.securityapplication.Helper.FirebaseHelper;
import com.example.securityapplication.Helper.InternalStorage;
import com.example.securityapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import static com.example.securityapplication.R.layout.spinner_layout;
import static com.google.firebase.storage.StorageException.ERROR_OBJECT_NOT_FOUND;

public class profile_fragment extends Fragment {

    private EditText textName,textEmail,textPhone,textDob;
    private AutoCompleteTextView textAddress;
    private Button btn_edit;
    private Button btn_logout;
    private TextView text_changePassword;
    SQLiteDBHelper mydb ;
    User user;
    private TelephonyManager telephonyManager;
    private String mImeiNumber;
    private int RC;
    private String TAG = "ProfileActivity";
    Spinner spinner;
    DatePickerDialog datePickerDialog;
    private FirebaseHelper firebaseHelper;

    private CircleImageView profile_pic;
    private ImageButton chooseImgBtn;
    private Uri filePath = null;
    private  Bitmap bitmappic =null;
    File f;
    private  Intent CropIntent;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int TAKE_PICTURE = 81;
    private  ProgressDialog progressDialog;

    // InternalStorage
    private InternalStorage internalStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_profile,container,false);

        String [] values =
                {"Male","Female","Others"};
        spinner = v.findViewById(R.id.text_Gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), spinner_layout, values);
        adapter.setDropDownViewResource(spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initObjects();
        initviews();
//        FetchAllData();
        DisplayData();
        initListeners();
        deviceId();




    }

    private void initObjects() {

//        user = getIntent().getParcelableExtra("User");
        user = UserObject.user;
        mydb = SQLiteDBHelper.getInstance(getContext());
        /**  Get FirebaseHelper Instance **/
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
        firebaseHelper.initContext(getActivity());
        firebaseHelper.initGoogleSignInClient(getString(R.string.server_client_id));

        deviceId();
        internalStorage = InternalStorage.getInstance();
        internalStorage.initContext(getContext());
    }

    private void initListeners() {


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    /*Dialog builder = new Dialog(this);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    builder.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //nothing;
                        }
                    });

                    ImageView imageView = new ImageView(this);
                    imageView.setImageURI(imageUri);
                    builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.show();*/
                final ImagePopup imagePopup = new ImagePopup(getContext());
                imagePopup.setWindowHeight(800); // Optional
                imagePopup.setWindowWidth(800); // Optional
                imagePopup.setBackgroundColor(Color.BLACK);  // Optional
                imagePopup.setFullScreen(true); // Optional
                imagePopup.setHideCloseIcon(true);  // Optional
                imagePopup.setImageOnClickClose(true);  // Optional
                imagePopup.setFadingEdgeLength(1000);
                imagePopup.setClickable(true);
                imagePopup.setKeepScreenOn(true);
                imagePopup.setPressed(true);
                imagePopup.setTop(100);
                imagePopup.setBottom(100);
                imagePopup.setEnabled(true);


                imagePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pictureChoice();                    }
                });




                imagePopup.initiatePopup(profile_pic.getDrawable()); // Load Image from Drawable
                imagePopup.viewPopup();


            }
        });




        textDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= java.util.Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                textDob.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
                                        @Override public void onClick(View view) {
                                            Animation edit_anim= AnimationUtils.loadAnimation(getContext(),R.anim.btn_anim);
                                            btn_edit.startAnimation(edit_anim);

                                            if (IsInternet.isNetworkAvaliable(getContext())) {

                                                chooseImgBtn.setVisibility(View.VISIBLE);

                                                if(btn_edit.getText().equals("edit"))
                                                {btn_edit.setText("Save");
                                                    enable();
                                                    btn_edit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                                alphaa(1.0f);}
                                                else {
                                                    if(!validate())
                                                    {
                                                        Toasty.error(getContext(), "Please Enter Valid Information", Toast.LENGTH_SHORT, true).show();

                                                        //Toast.makeText(getContext(), "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        // start progress bar
                                                        progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
                                                        progressDialog.setTitle("Saving data...");
                                                        progressDialog.show();
                                                        progressDialog.setMessage("validating....");
                                                        progressDialog.setCancelable(false);

                                                        //save code will come here
                                                        user.setName(textName.getText().toString().trim());
                                                        textName.setText(textName.getText().toString().trim());
                                                        user.setDob(textDob.getText().toString());
                                                        user.setLocation(textAddress.getText().toString().trim());
                                                        textAddress.setText(textAddress.getText().toString().trim());
                                                        if (spinner.getSelectedItemPosition() == 0)
                                                            user.setGender("male");
                                                        else if (spinner.getSelectedItemPosition() == 1)
                                                            user.setGender("female");
                                                        else
                                                            user.setGender("others");
                                                        user.setMobile(textPhone.getText().toString());
                                                        updateUser();
                                                    }
                                                }
                                            }//Sending Data to EditProfileActivity
                                            else {
                                                Toasty.error(getContext(), "Please check your Internet Connectivity", Toast.LENGTH_LONG, true).show();

                                                //Toast.makeText(getContext(), "Please check your Internet Connectivity", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }
        );

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();

                Animation log_anim=AnimationUtils.loadAnimation(getContext(),R.anim.btn_anim);
                btn_logout.startAnimation(log_anim);
                Toasty.info(getContext(), "clicked", Toast.LENGTH_SHORT, true).show();

                Log.d("signout","signout happen");
                signOut();
            }
        });

        text_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(firebaseHelper.getFirebaseAuth().getCurrentUser().getEmail());
            }
        });

        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(checkCameraPermission() && checkReadExterPermission() && checkWriteExterPermission() ) {
                checkCameraPermission();
                checkWriteExterPermission();
                checkReadExterPermission();

                pictureChoice();
                // choose img from gallery
                chooseImg("storage");

            }
            else {
                Toasty.info(getContext(),"Please give Camera and File access Permissions");
            }
            }
        });
    }

    private boolean validate() {
        if(textName.getText().toString().trim().length()>1 && textAddress.getText().toString().length()>1 && textPhone.getText().toString().length()==10) {
            return Pattern.matches("[ a-zA-Z]+", textName.getText().toString().trim());
        }
        else
            return false;
    }

//    private void FetchAllData(){
//        int i =0;
//        Cursor res;
//        res = mydb.getAllData();
//        if (res.getCount() == 0){
//            Toast toast = Toast.makeText(getContext(),
//                    "No User Data Found",
//                    Toast.LENGTH_LONG);
//            toast.show();
//            Log.d("Profile","No Data found");
//        }
////        StringBuffer buffer = new StringBuffer();
//        while (res.moveToNext()){
//            user.setName(res.getString(1));
//            user.setEmail(res.getString(2));
//            user.setGender(res.getString(3));
//           user.setMobile(res.getString(4));
////            ansAadhaar = res.getString(6);
//            user.setLocation(res.getString(5));
//            user.setDob(res.getString(6));
//            i++;
//            Log.d("Profile Activity","User Object set in Profile activity successfully" +i);
//        }
//    }

    private void DisplayData() {

        textName.setText(user.getName());
//        textAadhaar.setText(ansAadhaar);
        textDob.setText(user.getDob());
        int kk=0;
        if(user.getGender().equalsIgnoreCase("male"))
            kk=0;
        else if(user.getGender().equalsIgnoreCase("female"))
            kk=1;
        else
            kk=2;
        spinner.setSelection(kk);
        textAddress.setText(user.getLocation());
        textEmail.setText(user.getEmail());
        textPhone.setText(user.getMobile());

        // display image from internal storage
        File imgPath = internalStorage.getImagePathFromStorage(user.getEmail());
        //f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Trata/"+user.getEmail()+"/");
        //File imgPath = new File(f.getAbsolutePath(), "profile.jpeg");
       // File imgPath = new File(f.getAbsolutePath(), "profile.jpeg");
        try{
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(imgPath));
            profile_pic = getActivity().findViewById(R.id.profile_pic);
            //ImageButton imageButton = (ImageButton) profile_pic;
            profile_pic.setImageBitmap(b);
        }catch (IOException e){
            //Toast.makeText(getContext(), "Profile picture not found", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Profile picture not found");
        }

        Log.d("Profile","DATA displayed on profile Successfully");
    }
    private void disable(){
        textName.setEnabled(false);
        spinner.setEnabled(false);
        textEmail.setEnabled(false);
        textPhone.setEnabled(false);
        textAddress.setEnabled(false);
        textDob.setEnabled(false);
        textName.setBackgroundColor(Color.TRANSPARENT);
        textEmail.setBackgroundColor(Color.TRANSPARENT);
        textPhone.setBackgroundColor(Color.TRANSPARENT);
        textAddress.setBackgroundColor(Color.TRANSPARENT);
        textDob.setBackgroundColor(Color.TRANSPARENT);
    }
    private void alphaa(float k){
        spinner.setAlpha(k);
        textName.setAlpha(k);
        textPhone.setAlpha(k);
        textAddress.setAlpha(k);
        textDob.setAlpha(k);
        textName.setBottom(Color.BLACK);
    }
    private void enable(){
        spinner.setEnabled(true);
        textName.setEnabled(true);
        //chooseImgBtn.setVisibility(View.VISIBLE);
        textPhone.setEnabled(true);
        textAddress.setEnabled(true);
        textDob.setEnabled(true);


        textEmail.setBackgroundResource(R.drawable.blackborder);
        textPhone.setBackgroundResource(R.drawable.blackborder);
        textAddress.setBackgroundResource(R.drawable.blackborder);
        textDob.setBackgroundResource(R.drawable.blackborder);
    }

    private void initviews() {
        textName =getActivity().findViewById(R.id.text_Name);
        textEmail = getActivity().findViewById(R.id.text_Email);
        textPhone = getActivity().findViewById(R.id.text_Phone);
        textAddress = getActivity().findViewById(R.id.text_Address);
        //textGender = getActivity().findViewById(R.id.text_Gender);
        textDob = getActivity().findViewById(R.id.text_DOB);
        btn_edit = getActivity().findViewById(R.id.btn_Edit);
        btn_logout = getActivity().findViewById(R.id.btn_Logout);
        Resources res = getResources();
        String[] Locality = res.getStringArray(R.array.Locality);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,Locality);
        textAddress.setAdapter(adapter);
        text_changePassword = getActivity().findViewById(R.id.text_changePassword);
        profile_pic = getActivity().findViewById(R.id.profile_pic);
        chooseImgBtn = getActivity().findViewById(R.id.btn_choose_img);

        disable();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"resultcode:"+resultCode+"requestcode:"+requestCode);
        if (requestCode == 1){
            if (resultCode == 110){
                user = Objects.requireNonNull(data).getParcelableExtra("ResultUser");
                Log.d("Profile","User object returned"+user.getEmail());
                mydb.updateUser(user);
                DisplayData();
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null ) {
            filePath = data.getData();

            if (filePath == null) {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                return;
            }
            cropimage();
        }





           /* Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), result.getBitmap());


            try {
                //CropImage.ActivityResult result = CropImage.getActivityResult(data);

                internalStorage.saveImageToInternalStorage(result.getBitmap(), user.getEmail());
                profile_pic.setImageBitmap(result.getBitmap());
                deleteExistingProfilePic();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to store image",Toast.LENGTH_SHORT).show();
            }*/


        if(requestCode == 201 && resultCode == getActivity().RESULT_OK) {
            checkCameraPermission();


            filePath = null;
            Bitmap cameraphoto = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            Long image_size2 = imagesizecheck(cameraphoto);
            Log.d("tag", String.valueOf(image_size2));


            filePath = getImageUri(Objects.requireNonNull(getContext()), Objects.requireNonNull(cameraphoto));




/*
            Log.d("tag", String.valueOf(filePath));

            filePath = (Uri) data.getData();

            Log.d("tag", String.valueOf(filePath));


            //bitmappic = (Bitmap) data.getExtras().get("data");

            if (filePath == null) {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                return;
            }*/


            cropimage();
        }


           /* Log.d("tag", String.valueOf(bitmappic));

            try {
                internalStorage.saveImageToInternalStorage(bitmappic, user.getEmail());
                profile_pic.setImageBitmap(bitmappic);
                deleteExistingProfilePic();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to store image",Toast.LENGTH_SHORT).show();
            }*/

           
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK)
        {

            Bundle bundle = Objects.requireNonNull(data).getExtras();
            bitmappic = Objects.requireNonNull(bundle).getParcelable("data");

            Long image_size = imagesizecheck(bitmappic);





            Log.d("tag", String.valueOf(image_size));
            try {
                //CropImage.ActivityResult result = CropImage.getActivityResult(data);
               // Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), bitmappic);

                /*if (!f.exists()) {
                    Log.d(TAG, "Folder doesn't exist, creating it...");
                    boolean rv = f.mkdir();
                    Log.d(TAG, "Folder creation " + ( rv ? "success" : "failed"));
                } else {
                    Log.d(TAG, "Folder already exists.");
                }*/

                    //internalStorage.saveImageToInternalStorage(bitmappic, user.getEmail());
                //File mypath=new File(f.getAbsolutePath(),"profile.jpeg");




                //FileOutputStream fos1 = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
               // bitmappic.compress(Bitmap.CompressFormat.JPEG, 100, bitmappic);


                profile_pic.setImageBitmap(bitmappic);
                Log.d("tag", String.valueOf(image_size));




                deleteExistingProfilePic();
                Log.d("tag", String.valueOf(image_size));

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to store image",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public long  imagesizecheck(Bitmap bitmapOrg)
    {
        Bitmap bitmap2 = bitmapOrg;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;
        //Toasty.info(getContext(), (int) lengthbmp);


        Log.d("tag", String.valueOf(lengthbmp));

        return  lengthbmp;
    }


    private void deviceId() {
        telephonyManager = (TelephonyManager) getActivity().getSystemService(getContext().TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mImeiNumber = telephonyManager.getImei(0);
                Log.d("IMEI", "IMEI Number of slot 1 is:" + mImeiNumber);
            }
            else {
                mImeiNumber = telephonyManager.getDeviceId();
            }
        }

        //Log.d("MAinActivity","SMS intent");
        //check permissions
        checkCameraPermission();
        checkSMSPermission();
        checkWriteExterPermission();
        checkReadExterPermission();
    }

    public  boolean checkSMSPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            Toasty.error(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_SHORT, true).show();

            //Toast.makeText(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, RC);
        }

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED;

    }

    public  boolean checkCameraPermission(){

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;

    }

    public  boolean checkReadExterPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC);
        }

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;

    }


    public  boolean checkWriteExterPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC);
        }

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceId();
                } else {
                    getActivity().finish();
                    Toast.makeText(getContext(), "Without permission we check", Toast.LENGTH_LONG).show();
                }
                break;

            case 0:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        chooseImgBtn.setEnabled(true);
                    }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void signOut(){
        Log.d(TAG,"Inside signout");
        if (mImeiNumber == null) {
            deviceId();
            return;
        }

        if (!IsInternet.checkInternet(getContext()))
            return;

        firebaseHelper.firebaseSignOut(mImeiNumber);
        firebaseHelper.googleSignOut(getActivity());
        //delete user records from SQLite
        mydb.deleteDatabase(Objects.requireNonNull(getContext()).getApplicationContext());

        try{
            Intent mStopSosPlayer=new Intent(getContext(),SosPlayer.class);
            mStopSosPlayer.putExtra("stop",1);
            getActivity().startService(mStopSosPlayer); //previously was stopService(). Now using startService() to use the stop extra in onStartCommand()
            Log.d("Profile Fragment","Service sosplayer new startIntent...");

            //to stop GetGPS Service upon logout
            Intent MStopGPSService = new Intent(getContext(),GetGPSCoordinates.class);
            getActivity().stopService(MStopGPSService);
            Log.d("Profile Fragment","GPS Service Stopped");
            Toasty.warning(getContext(), "Services Stopped", Toast.LENGTH_SHORT, true).show();

           // Toast.makeText(getContext(),"Service sosplayer stopping...",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            Log.d("Profile Fr","Service SOSplayer is not running");
        }
        //finishing the navigation activity
        getActivity().finish();
        //Clear the back stack and re-directing to the sign-up page
        Intent mLogOutAndRedirect= new Intent(getContext(),MainActivity.class);
        mLogOutAndRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mLogOutAndRedirect);
    }

    private void updateUser(){

        Log.d(TAG,"Updating user...");
        mydb.updateUser(user);
        firebaseHelper.updateuser_infirebase(FirebaseAuth.getInstance().getUid(),user);

        // stop progress bar
        progressDialog.dismiss();

        btn_edit.setText("edit");
        alphaa(0.6f);
        btn_edit.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_edit_black_24dp, 0, 0, 0);
        disable();
    }

    private void changePassword(String email){
        if (!IsInternet.checkInternet(getContext()))
            return;

        progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Sending Email...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        try {
            firebaseHelper.getFirebaseAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        try{Toasty.success(getActivity(), "EMAIL SENT. PLEASE CHECK YOUR MAIL TO CHANGE PASSWORD", Toast.LENGTH_SHORT, true).show();}
                        catch(Exception e){Log.d(TAG,"toast exception:"+e.getMessage());}
                    }
                    else
                    {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Log.d(TAG,e.getMessage());
                            try{Toasty.info(getActivity(), "You need to sign in again to change password", Toast.LENGTH_LONG, true).show();}
                            catch(Exception e1){Log.d(TAG, "toast exception:"+e1.getMessage());}
                        }
                    }
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            Log.d(TAG, e.getMessage());
            Toasty.info(getActivity(), "You need to sign in again to change password", Toast.LENGTH_LONG, true).show();

//            Toast.makeText(getActivity(),"You need to sign in again to change password",Toast.LENGTH_LONG).show();
        }
    }

    private void pictureChoice(){
        final AlertDialog.Builder a_builder = new AlertDialog.Builder(getContext());
        a_builder.setTitle("Profile Photo")
                .setIcon(R.drawable.ic_camera_icon)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chooseImg("camera");

                    }
                })
                .setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chooseImg("gallery");
                    }
                })
                .setNegativeButton("Crop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cropimage();
                    }
                })
        ;
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    private void chooseImg(String choice){
        switch (choice) {
            case "gallery":
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                pickImageIntent.setType("image/*");

                //CropImage.startPickImageActivity(MainActivity.this);

                startActivityForResult(Intent.createChooser(pickImageIntent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;

            case "camera":
                checkCameraPermission();

                Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraintent,201);
        }
    }

    private void uploadProfilePicToFirebase(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        // Get the data from an ImageView as bytes
        profile_pic.setDrawingCacheEnabled(true);
        profile_pic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = firebaseHelper.getStorageReference().child("images/profile_pic");
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to upload image"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100f*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void deleteExistingProfilePic(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Processing...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        // Create a storage reference from our app
        StorageReference storageRef = firebaseHelper.getStorageReference();

        // Create a reference to the file to delete
        StorageReference imgRef = storageRef.child("images/profile_pic");

        // Delete the file
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                progressDialog.dismiss();
                uploadProfilePicToFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                if (((StorageException) exception).getErrorCode() == ERROR_OBJECT_NOT_FOUND){
                    progressDialog.dismiss();
                    uploadProfilePicToFirebase();
                }
                else
                    Toasty.error(getContext(), "Failed to upload image"+exception.getMessage(), Toast.LENGTH_LONG, true).show();
                //Toast.makeText(getContext(), "Failed to upload image"+exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cropimage()
    {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(filePath, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 3);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
            startActivityForResult(CropIntent, 0);
        }catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            try {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }catch (Exception e){Log.d(TAG,e.getMessage());}
        }
    }
}