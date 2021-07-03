package tw.org.iii.iiiandroid10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private MyAdpter myAdpter;
    //private RequestQueue queue;
    private LinkedList<HashMap<String,String>> data;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Downlaoading..");

        data = new LinkedList<>();
        //queue = Volley.newRequestQueue(this);
        listView = findViewById(R.id.listView);
        initListView();
        fetchRemoteData();
    }

    private void fetchRemoteData(){
        progressDialog.show();

//        String url1 = "https://data.coa.gov.tw/Service/OpenData/HuaLienFarm.aspx";
        String url1 = "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvAttractions.aspx";
        String url2 = "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvAttractions.aspx";
        StringRequest request= new StringRequest(Request.Method.
                GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad",response);
                        parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("brad",error.toString());
                        progressDialog.dismiss();
                    }
                });
        MainApp.queue.add(request);
    }

    private  void parseJSON(String json){
        try{
            JSONArray root = new JSONArray(json);
            for (int i = 0;i<root.length();i++){
                HashMap<String,String> dd = new HashMap<>();
                JSONObject row = root.getJSONObject(i);
//                dd.put("catid",row.getString("catid"));
                dd.put("name",row.getString("Name"));
//                dd.put("address",row.getString("Address"));
                dd.put("tel",row.getString("Tel"));
//                dd.put("HostWords",row.getString("HostWords"));
//                dd.put("FoodFeature",row.getString("FoodFeature"));
//                dd.put("Coordinate",row.getString("Coordinate"));
                dd.put("Photo",row.getString("Photo"));
                dd.put("Heart","xx");
                data.add(dd);
            }
            myAdpter.notifyDataSetChanged();
        }catch (Exception e){
            Log.v("brad",e.toString());
        }
        progressDialog.dismiss();
    }

    private  void initListView(){
        myAdpter = new MyAdpter();
        listView.setAdapter(myAdpter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoDetail(position);
            }
        });
    }

    private  void gotoDetail(int index){
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("data",data.get(index));
        startActivity(intent);
    }

    private class MyAdpter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
            View view=inflater.inflate(R.layout.item,null);

            TextView name = view.findViewById(R.id.item_name);
            name.setText(data.get(position).get("name"));

            TextView tel = view.findViewById(R.id.item_tel);
            tel.setText(data.get(position).get("tel"));

            TextView address = view.findViewById(R.id.item_address);
            address.setText(data.get(position).get("Address"));

            ImageView heart = view.findViewById(R.id.item_heart);
            heart.setImageResource(position%4==0?R.drawable.heart:R.drawable.heart_no);
            heart.setImageResource(data.get(position).get("Heart").equals("ok")?R.drawable.heart:R.drawable.heart_no);
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.get(position).put("Heart",data.get(position).get("Heart").equals("ok")?"xx":"ok");
                    ((ImageView)v).setImageResource(data.get(position).get("Heart").equals("ok")?R.drawable.heart:R.drawable.heart_no);
                    Log.v("brad","pos:" + position);
                }
            });

            return view;
        }
    }
}
