package com.schwappkopf.friendzonechecker;

/**
 * Created by schwappkopf on 9/24/14.
 */
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FriendZoneMain extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> usersList;

    // url to get all products list
    private static String url_all_users = "your service";
    //kütüphanede

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_USERID = "user_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOC = "location";
    private static final String TAG_STA = "status";
    private static final String TAG_UPDATE= "updated_at";


    // products JSONArray
    JSONArray users = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users);

        // Hashmap for ListView
        usersList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllUsers().execute();

        // Get listview
        ListView lv = getListView();

    }

    class LoadAllUsers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FriendZoneMain.this);
            pDialog.setMessage("Loading users. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    users = json.getJSONArray(TAG_USERS);

                    // looping through All Products
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_USERID);
                        String name = c.getString(TAG_NAME);
                        String location = c.getString(TAG_LOC);
                        String status = c.getString(TAG_STA);
                        String update=c.getString(TAG_UPDATE);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        if(location.contentEquals("1"))
                        {
                            map.put(TAG_LOC,"Yer: Yurtta");
                        }
                        else
                        {
                            map.put(TAG_LOC,"Yer: Dışarda");
                        }

                        if(status.contentEquals("1"))
                        {
                            map.put(TAG_STA,"Durum: Yedi");
                        }
                        else
                        {
                            map.put(TAG_STA,"Durum: Yemedi");
                        }

                        // adding each child node to HashMap key => value
                        map.put(TAG_USERID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_UPDATE,"Updated at: " + update);


                        // adding HashList to ArrayList
                        usersList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity

                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            FriendZoneMain.this, usersList,
                            R.layout.activity_friend_zone_main, new String[] { TAG_USERID,
                            TAG_NAME,TAG_LOC,TAG_STA,TAG_UPDATE},
                            new int[] { R.id.pid, R.id.name ,R.id.location,R.id.status,R.id.update});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

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
            Intent a=new Intent(this,Settings.class);
            startActivity(a);
            finish();
            return true;
        }

        if(id == R.id.action_about)
        {
            AlertDialog.Builder  builder = new AlertDialog.Builder(FriendZoneMain.this);
            builder.setMessage("created for friends, version 0.1 , teaddict");
            builder.setCancelable(false);

            builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
