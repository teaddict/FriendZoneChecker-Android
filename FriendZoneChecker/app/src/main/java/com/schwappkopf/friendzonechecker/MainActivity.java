package com.schwappkopf.friendzonechecker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;



public class MainActivity extends Activity {
    boolean network;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        network=isNetworkOnline();
        if(!network)
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bağlantı Hatası!")
                    .setMessage("İnternet bağlantınızda bir sorun var...")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        }
        else {

            SharedPreferences settings = getSharedPreferences("prefs", 0);
            boolean firstRun = settings.getBoolean("firstRun", false);
            if (firstRun == false)//if running for first time
            //Splash will load for first time
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("firstRun", true);
                editor.commit();
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                finish();
            } else {
                Intent a = new Intent(this, FriendZoneMain.class);
                startActivity(a);
                finish();
            }
        }


        setContentView(R.layout.activity_main);
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
