package com.darker.bmr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivityBMR extends AppCompatActivity {

    private String message, bmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bmr);

        Intent intent = getIntent();
        bmr = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        message = getResources().getString(R.string.bmr);
        message += " " + bmr + " ";
        message += getResources().getString(R.string.calories);

        TextView textView= (TextView) findViewById(R.id.bmr_result);
        textView.setTextSize(25);
        textView.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.menu_display_message, menu);
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_display_message, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_share:
                sendMessage();
                return true;
            case R.id.menu_save:
                BMRDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendMessage(){
        Uri sms = Uri.parse("sms:5556");
        Intent calIntent = new Intent(Intent.ACTION_VIEW, sms);
        //calIntent.putExtra("address", "5556");
        calIntent.putExtra("sms_body", message);
        //calIntent.setType("vnd.android-dir/mms-sms");

        PackageManager manager = getPackageManager();
        List<ResolveInfo> activities = manager.queryIntentActivities(calIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe)
            startActivity(calIntent);
    }

    public void showMessage(String title, String message){
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void BMRDatabase(){
        DatabaseHandler db = new DatabaseHandler(this);
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String time = String.valueOf(sf.format(new Date()));
        db.addBMR(new BMR(time, bmr));

        String string = new String();
        int size = db.getBMRCount();
        BMR b = new BMR();

        while (size > 0){
            b = db.getBMR(size);
            string += "TIME: " + b.getTime() + "\n";
            string += "BMR: " + b.getBmr() + "\n\n";
            size--;
        }

        /* List<BMR> bmrList = db.getAllBMR();
        for(BMR cn : bmrList){
            string += "ID: " + cn.getId() + "\n";
            string += "TIME: " + cn.getTime() + "\n";
            string += "BMR: " + cn.getBmr() + "\n\n";
        }*/

        showMessage("History Details", string);
    }
}
