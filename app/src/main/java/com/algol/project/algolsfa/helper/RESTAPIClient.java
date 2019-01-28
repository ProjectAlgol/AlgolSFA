package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.util.Log;

import com.algol.project.algolsfa.interfaces.APIInvocationListener;
import com.algol.project.algolsfa.interfaces.RetrofitAPIInterface;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.others.Constants.API;
import com.algol.project.algolsfa.pojos.response.TestModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by swarnavo.dutta on 1/28/2019.
 */

public class RESTAPIClient {
    private Context context;
    private API api;
    private APIInvocationListener apiInvocationListener;
    private String request;
    private Retrofit retrofitClient;
    private RetrofitAPIInterface apiInterface;

    public RESTAPIClient(Context context, API api, APIInvocationListener apiInvocationListener, String request) {
        this.context = context;
        this.api = api;
        this.apiInvocationListener= apiInvocationListener;
        this.request = request;
        this.apiInterface= getApiInterface();
    }

    private RetrofitAPIInterface getApiInterface() {
        Retrofit retrofitClient= new Retrofit.Builder().baseUrl(Constants.apiBase).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofitClient.create(RetrofitAPIInterface.class);
    }

    public void exec() {
            Call<TestModel> call= apiInterface.getMyTeam(request);
            call.enqueue(new Callback<TestModel>() {
                @Override
                public void onResponse(Call<TestModel> call, Response<TestModel> response) {
                    TestModel model= response.body();
                }

                @Override
                public void onFailure(Call<TestModel> call, Throwable t) {
                    Log.v("Test","Testing...");
                }
            });
    }

}
