package com.darker.bmr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.darker.bmr.MESSAGE";
    private final static String keyR = "keyRadio", keyA = "keyAge",
            keyH = "keyHieght", keyW = "keyWeight", keyE = "keyExersice", key = "keySharedPreferences";


    private double A;
    private int age, height, weight, indexExercise;
    private String str, message, BMR;
    private boolean male;

    private EditText editText;
    private Spinner spinner;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private RadioButton radioButton;

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private final static int REQUEST_TAKE_PHOTO = 1;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Last input
        sharedPref = getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        boolean getR = sharedPref.getBoolean(keyR, true);
        radioButton = getR? (RadioButton) findViewById(R.id.radio_male) : (RadioButton) findViewById(R.id.radio_female);
        radioButton.setChecked(true);

        editText = (EditText) findViewById(R.id.age);
        editText.setText(sharedPref.getString(keyA, ""));

        editText = (EditText) findViewById(R.id.height);
        editText.setText(sharedPref.getString(keyH, ""));

        editText = (EditText) findViewById(R.id.weight);
        editText.setText(sharedPref.getString(keyW, ""));


        spinner = (Spinner) findViewById(R.id.exercise);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exercise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /*int e = sharedPref.getInt(keyE, 0);
        CharSequence text = String.valueOf(e);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                indexExercise = pos;
                editor.putInt(keyE, indexExercise);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner = (Spinner) findViewById(R.id.exercise);
        spinner.setSelection(sharedPref.getInt(keyE, 3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_history:
                BMRDatabase();
                return true;
            case R.id.menu_clear:
                editor.clear();
                editor.commit();
                delTable();
                finish();
                startActivity(getIntent());
                return true;
            case R.id.menu_setting:
                Intent intent = new Intent(this, Preference.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    male = true;
                break;
            case R.id.radio_female:
                if (checked)
                    male = false;
                break;
        }
        editor.putBoolean(keyR, male);
        editor.commit();
    }

    public void onCalculateClicked(View view){
        editText = (EditText) findViewById(R.id.age);
        str = editText.getText().toString();
        age = Integer.parseInt(str);
        editor.putString(keyA, str);
        editor.commit();

        editText = (EditText) findViewById(R.id.height);
        str = editText.getText().toString();
        height = Integer.parseInt(str);
        editor.putString(keyH, str);
        editor.commit();

        editText = (EditText) findViewById(R.id.weight);
        str = editText.getText().toString();
        weight = Integer.parseInt(str);
        editor.putString(keyW, str);
        editor.commit();

        A = (10 * weight) + (6.25 * height) + (5 * age);
        A = male? A + 5 : A - 161;

        double num;
        switch (indexExercise){
            case 0: num = 1.2;      break;
            case 1: num = 1.375;    break;
            case 2: num = 1.55;     break;
            case 3: num = 1.725;    break;
            case 4: num = 1.9;      break;
            default: num = 1;       break;
        }

        A *= num;
        BMR = String.format("%.2f", A);
        message = getResources().getString(R.string.bmr);
        message += " " + BMR + " ";
        message += getResources().getString(R.string.calories);

        sendMessage();
    }

    public void sendMessage(){
        Intent intent = new Intent(this, MainActivityBMR.class);
        intent.putExtra(EXTRA_MESSAGE, BMR);
        startActivity(intent);
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void BMRDatabase(){

        SharedPreferences share = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String title = share.getString(getResources()
                .getString(R.string.pref_history), "History Details");
        int limit = Integer.parseInt(share.getString(getString(R.string.pref_limit), "-1"));

        DatabaseHandler db = new DatabaseHandler(this);
        String string = new String();
        int size = db.getBMRCount();
        int count = 1;
        BMR b = new BMR();

        while (size > 0){
            b = db.getBMR(size);
            string += "TIME: " + b.getTime() + "\n";
            string += "BMR: " + b.getBmr() + "\n\n";
            size--;

            if(count++ == limit)
                break;
        }

        showMessage(title, string);
    }

    public void delTable(){
        DatabaseHandler db = new DatabaseHandler(this);
        db.delTable();
    }

    public void onTakePictureClicked(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photo = null;
            try{
                photo = createImageFile();
                SharedPreferences preferences = getSharedPreferences("photo", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("photoPath", photoPath);
                edit.commit();
            }catch (IOException e){
                Log.e("CreateImage", e.getMessage());
            }

            if(photo != null)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            try {
                FileOutputStream out = new FileOutputStream(photoPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }catch (Exception e){
                Log.e("Bitmap", e.getMessage());
            }
        }
    }

    private File createImageFile() throws IOException{
        String time = new SimpleDateFormat("ddMMyyyy_HHmmss_").format(new Date());
        String imageFileName = "PNG_" + time + "_";
        File storage = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".png", storage);
        photoPath = image.getAbsolutePath();

        return image;
    }
}
