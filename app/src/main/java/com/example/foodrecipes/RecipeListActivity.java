package com.example.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;


import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApi;
import com.example.foodrecipes.requests.ServiceGenerator;
import com.example.foodrecipes.requests.responses.RecipeResponse;
import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.util.Testing;
import com.example.foodrecipes.viewmodels.RecipeListViewModel;

import java.io.IOException;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRetrofitRequest();
            }
        });

        subscribeObservers();
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                    Testing.printRecipes(recipes, TAG);
                }
            }
        });
    }

    private void searchRecipeApi(String query, int pageNumber) {
        mRecipeListViewModel.searchRecipeApi(query, pageNumber);
    }

    private void testRetrofitRequest() {
        searchRecipeApi("chicken breast", 0);
    }

}
