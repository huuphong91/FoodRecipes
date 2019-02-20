package com.example.foodrecipes;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.example.foodrecipes.util.Testing;
import com.example.foodrecipes.viewmodels.RecipeListViewModel;


public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        findViewById(R.id.btnTest).setOnClickListener(v -> testRetrofitRequest());

        subscribeObservers();
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                Testing.printRecipes(recipes, TAG);
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
