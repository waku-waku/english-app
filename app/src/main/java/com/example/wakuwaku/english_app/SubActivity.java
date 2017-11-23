package com.example.wakuwaku.english_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.wakuwaku.english_app.pojo.GetDicItemResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubActivity extends ActionBarActivity {

    TextView titleTextView;
    TextView discriptionView;

    private String itemId;
    private String itemTitle;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.color.action_bar);
        toolbar.setNavigationIcon(R.mipmap.ic_search);
        toolbar.setTitleTextColor(Color.WHITE);

        titleTextView = (TextView) findViewById(R.id.textTitle);
        discriptionView = (TextView)findViewById(R.id.discriptionView);

        Intent intent = getIntent();
        if(intent != null) {
            itemId = intent.getStringExtra("DicItemId");
            itemTitle = intent.getStringExtra("DicItemTitle");
        }

        itemTitle = "<u>"+ itemTitle +"</u>";
        titleTextView.setText(Html.fromHtml(itemTitle));
    }

    @Override
    protected void onResume() {
        super.onResume();
        translator();
    }

    private void translator() {
        //builderにいれる
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("public.dejizo.jp");
        builder.appendPath("NetDicV09.asmx/GetDicItemLite");
        builder.appendQueryParameter("Dic", "EJdict");
        builder.appendQueryParameter("Item", itemId);
        builder.appendQueryParameter("Prof", "XHTML");
        builder.appendQueryParameter("Loc", "hoge");

        // リクエストオブジェクトを作って
        Request request = new Request.Builder()
                // URLを生成
                .url(builder.build().toString())
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

                //マッチング
                String responseBody = response.body().string();
                Pattern pattern = Pattern.compile("<[a-z]* .+?>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(responseBody);
                responseBody = matcher.replaceAll("");
                Pattern patternTagOnly = Pattern.compile("<[a-z]*>", Pattern.DOTALL);
                Matcher matcherTagOnly = patternTagOnly.matcher(responseBody);
                responseBody = matcherTagOnly.replaceAll("");
                Pattern patternEnd = Pattern.compile("</[a-z]*>");
                Matcher matcherEnd = patternEnd.matcher(responseBody);
                responseBody = matcherEnd.replaceAll("");
                Log.d(MainActivity.class.getSimpleName(), responseBody);

                final String xml = responseBody;
                Log.d("ResponseBody", xml);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Persister persister = new Persister();
                        GetDicItemResult getDicItemResult = null;
                        try {
                            // 読み込む
                            getDicItemResult = persister.read(GetDicItemResult.class, xml);
                            // titleTextView.setText(getDicItemResult.getHead());
                            discriptionView.setText(getDicItemResult.getBody());
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
        getMenuInflater().inflate(R.menu.menu_sub, menu);
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
