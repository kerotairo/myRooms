package com.example.android.myrooms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView ;
    private BeaconManager beaconManager;
    public static final String TAG="yeah";
    private String scanId;
    private MyNearableModel myNearable;

    public List<String> NearableInfo= new ArrayList<String>();
    private String topNearableId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), "kerotairo-live-com-s-your--dgj", "c6e950d1ebdec66c30ac40cf086882c8");
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        beaconManager = new BeaconManager(getApplicationContext());

        listView = (ListView) findViewById(R.id.id_list_view);
        writeToFile("Luzon");

        // Should be invoked in #onCreate.
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override public void onNearablesDiscovered(List<Nearable> nearables) {

                if(!nearables.isEmpty()) {
                    Log.d(TAG, "Discovered nearables: " + nearables);
                    myNearable= new MyNearableModel(nearables);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1);
                    List<String> myRooms = new ArrayList<String>();
                    myRooms=myNearable.parseNearableString();
                    adapter.addAll(myRooms);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {

                            String item = ((TextView)view).getText().toString();
                            new LongOperation().doInBackground(item);
                            writeToFile(item);
                            Toast.makeText(getBaseContext(), item + " successfully reserved!", Toast.LENGTH_LONG).show();

                        }
                    });
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    if (!isInRange(myRooms)) {
                    /*    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Do you still need the room?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();*/
                        Toast.makeText(getBaseContext(), "Do you still need the room?", Toast.LENGTH_LONG).show();
                        //}
                    }

                }


            }
        });


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onStop(){
        super.onStop();
        beaconManager.stopNearableDiscovery(scanId);
    }
    @Override
    public void onStart(){
        super.onStart();
        //Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                scanId = beaconManager.startNearableDiscovery();
                Log.d(TAG, "Discovered ScanID: " + scanId);
            }
        });
    }

    private boolean isInRange(List<String> myRooms){
        for(int i=0;i<myRooms.size();i++){
            Log.d(TAG,"File Contains: "+ readFromFile().toString());
            Log.d(TAG,"myRooms Contains: "+ myRooms.get(i));
            if (readFromFile().equals(myRooms.get(i))){
                return true;
            }
            Log.d(TAG,"Verdict is False");
        }
        return false;
    }
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("config.txt", getApplicationContext().MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... item) {

                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    Log.d(TAG,"http://10.252.156.5/ReserveRoom.php?room=" + item[0]);
                    HttpPost httppost = new HttpPost("http://10.252.156.5/ReserveRoom.php?room=" + item[0]);
                    httpclient.execute(httppost);


                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }

}
