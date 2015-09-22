package com.example.samriddhi.reader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ListActivity {

    protected JSONObject blogData;
    protected ProgressBar progressbar;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressbar= (ProgressBar) findViewById(R.id.progressBar);

        if(isNetworkAvailable()) {
            progressbar.setVisibility(View.VISIBLE);
            GetPosts posts = new GetPosts();
            posts.execute();
        }else{
            Toast.makeText(this,"Network is unavailable", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        boolean isAvail=false;
        if(networkInfo!=null && networkInfo.isConnected()) {
            isAvail = true;
        }

        return isAvail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            JSONObject arrResult=blogData.getJSONObject("responseData");
            JSONArray jsonpost = arrResult.getJSONArray("results");
            JSONObject jobj=jsonpost.getJSONObject(position);
            String blogURL=jobj.getString("clusterUrl");

            Intent intent =new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(blogURL));
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*public void updateList() {
        progressbar.setVisibility(View.INVISIBLE);

        if(blogData==null){
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Error message");
            builder.setMessage("Error in getting data");
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog=builder.create();
            dialog.show();

            TextView emptyTextView= (TextView) getListView().getEmptyView();
            emptyTextView.setText("No items to display");
        }else{

            try {
                JSONArray jsonpost = blogData.getJSONArray("posts");
                ArrayList<HashMap<String,String>> blogPosts=new ArrayList<HashMap<String, String>>();

                for(int i=0;i<jsonpost.length();i++){
                    JSONObject obj=jsonpost.getJSONObject(i);

                    String title=obj.getString("title");
                    title= Html.fromHtml(title).toString();
                    String author=obj.getString("author");
                    author= Html.fromHtml(author).toString();

                    HashMap<String, String> post=new HashMap<String,String>();
                    post.put("title",title);
                    post.put("author",author);

                    blogPosts.add(post);

                }

                String[] keys={"title","author"};
                int[] id={android.R.id.text1,android.R.id.text2};
                SimpleAdapter adapter= new SimpleAdapter(this, blogPosts,android.R.layout.simple_list_item_2,keys, id);
                setListAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }*/

    public void updateListgoogle() {
        progressbar.setVisibility(View.INVISIBLE);

        if(blogData==null){
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Error message");
            builder.setMessage("Error in getting data");
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog=builder.create();
            dialog.show();

            TextView emptyTextView= (TextView) getListView().getEmptyView();
            emptyTextView.setText("No items to display");
        }else{

            try {
                JSONObject arrResult=blogData.getJSONObject("responseData");
                JSONArray jsonpost = arrResult.getJSONArray("results");
                ArrayList<HashMap<String,String>> blogPosts=new ArrayList<HashMap<String, String>>();

                for(int i=0;i<jsonpost.length();i++){
                    JSONObject obj=jsonpost.getJSONObject(i);

                    String title=obj.getString("title");
                    title= Html.fromHtml(title).toString();
                    String date=obj.getString("publishedDate");
                    date= Html.fromHtml(date).toString();

                    HashMap<String, String> post=new HashMap<String,String>();
                    post.put("title",title);
                    post.put("date",date);

                    blogPosts.add(post);

                }

                String[] keys={"title","date"};
                int[] id={android.R.id.text1,android.R.id.text2};
                SimpleAdapter adapter= new SimpleAdapter(this, blogPosts,android.R.layout.simple_list_item_2,keys, id);
                setListAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private class GetPosts extends AsyncTask<Object, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Object... args) {
            int responseCode=-1;
            JSONObject jsonResponse=null;

            try{
                //URL blogFeedUrl=new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=20");
                URL blogFeedUrl=new URL("https://ajax.googleapis.com/ajax/services/search/news?" +
                        "v=1.0&q=barack%20obama&userip=INSERT-USER-IP&count=1");
                HttpURLConnection connection=(HttpURLConnection)blogFeedUrl.openConnection();
                connection.connect();

                responseCode=connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream =connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String json = reader.readLine();
                    JSONTokener tokener = new JSONTokener(json);
                    jsonResponse = new JSONObject(tokener);

                    Log.i(TAG, "successful Code: " + jsonResponse);

                }else {
                    Log.i(TAG, "Unsuccessful Code: " + responseCode);
                }
            }catch(MalformedURLException e){
                Log.e(TAG,"Exception caught: ",e);
            }catch(IOException e){
                Log.e(TAG,"Exception caught: ",e);
            }catch(Exception e){
                Log.e(TAG,"Exception caught: ",e);
            }

            return jsonResponse;
        }


        @Override
        protected void onPostExecute(JSONObject response){

            blogData=response;
            updateListgoogle();

        }

    }

}
