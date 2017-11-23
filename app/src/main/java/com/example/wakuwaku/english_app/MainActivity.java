package com.example.wakuwaku.english_app;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.wakuwaku.english_app.pojo.DicItem;
import com.example.wakuwaku.english_app.pojo.SearchDicItemResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    private Handler mHandler;
    private EditText editText;
    ListView list;
    private ArrayAdapter<DicItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundResource(R.color.action_bar);
        toolbar.setNavigationIcon(R.mipmap.ic_search);
        toolbar.setTitleTextColor(Color.WHITE);

        mHandler = new android.os.Handler();
        editText = (EditText) findViewById(R.id.editText);
        list = (ListView) findViewById(R.id.listView);


        adapter = new ArrayAdapter<DicItem>(this, android.R.layout.simple_expandable_list_item_1, new ArrayList<DicItem>());

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                DicItem item = (DicItem) listView.getItemAtPosition(position);
                Log.d("answer2", item.toString());
                //intent start
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                intent.putExtra("DicItemId", item.getItemId());
                intent.putExtra("DicItemTitle", item.getTitle());
                startActivity(intent);
            }
        });
    }

    public void clickSearch(View v) {
        String word = editText.getText().toString();
        if(TextUtils.isEmpty(word)) return;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("public.dejizo.jp");
        builder.appendPath("NetDicV09.asmx/SearchDicItemLite");
        builder.appendQueryParameter("Dic", "EJdict");
        builder.appendQueryParameter("Word", word);
        builder.appendQueryParameter("Scope", "HEADWORD");
        builder.appendQueryParameter("Match", "STARTWITH");
        builder.appendQueryParameter("Merge", "AND");
        builder.appendQueryParameter("Prof", "XHTML");
        builder.appendQueryParameter("PageSize", "30");
        builder.appendQueryParameter("PageIndex", "0");
        translator(builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void translator(Uri uri) {
        // リクエストオブジェクトを作って
        Request request = new Request.Builder()
                // URLを生成
                .url(uri.toString())
                .get()
                .build();
        // クライアントオブジェクトを作成する
        OkHttpClient client = new OkHttpClient();
        // 新しいリクエストを行う
        client.newCall(request).enqueue(new Callback() {
            // 通信が失敗した時
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            // 通信が成功した時
            @Override
            public void onResponse(final Response response) throws IOException {
                Log.d("onResponse", response.toString());
                String responseBody = response.body().string();
                // 不要なSpanタグを除去
                //Pattern pattern = Pattern.compile("<span class=\"NetDicTitle\" xmlns=\"\">");
                Pattern pattern = Pattern.compile("<span .+?>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(responseBody);
                responseBody = matcher.replaceAll("");
                Pattern patternEnd = Pattern.compile("</span>");
                Matcher matcherEnd = patternEnd.matcher(responseBody);
                responseBody = matcherEnd.replaceAll("");
                Pattern patternSpace = Pattern.compile(" ");
                Matcher matcherSpace = patternSpace.matcher(responseBody);
                responseBody = matcherEnd.replaceAll("");
                Log.d(MainActivity.class.getSimpleName(), responseBody);
                final String xml = responseBody;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Persister persister = new Persister();
                        SearchDicItemResult searchDicItemResult = null;
                        try {
                            // 読み込む
                            searchDicItemResult = persister.read(SearchDicItemResult.class, xml);
                            adapter.clear();
                            adapter.addAll(searchDicItemResult.getDicItemList());

                            if(adapter.getCount() == 0) {
                                Toast.makeText(getApplicationContext(),"Not found this word!", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}


