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

import org.json.JSONObject;

public class DisplaySearchResultsActivity extends AppCompatActivity {
    // Algolia
    Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
    Index index = client.initIndex("products");

    private String keyword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("search_keyword");

        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                Log.e("test", content.toString());
            }
        };
        index.searchAsync(new com.algolia.search.saas.Query(keyword), completionHandler);

    }
}
