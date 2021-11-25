package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplaySearchResultsActivity extends AppCompatActivity {
    // Algolia
    Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
    Index index = client.initIndex("products");

    private String search_result_keyword;
    private String search_result_product_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        search_result_keyword = intent.getStringExtra("search_keyword");

        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                Log.e("test", content.toString());

                try{
                    JSONArray hits = content.getJSONArray("hits");
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject jsonObject = hits.getJSONObject(i);
                        search_result_product_name = jsonObject.getString("product_name");
                        list.add(search_result_product_name);
                    }

                    Log.e("test", list + "");
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        index.searchAsync(new com.algolia.search.saas.Query(search_result_keyword), completionHandler);


    }
}
