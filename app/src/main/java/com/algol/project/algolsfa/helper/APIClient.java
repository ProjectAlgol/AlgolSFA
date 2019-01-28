package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.util.Log;

import com.algol.project.algolsfa.interfaces.APIInvocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swarnavo.dutta on 1/28/2019.
 */

public class APIClient {
    private Context context;
    private String apiURL;
    private HashMap<String,String> request;
    private APIInvocationListener apiInvocationListener;

    public APIClient(Context context, String apiURL, APIInvocationListener apiInvocationListener, HashMap<String,String> request) {
        this.context = context;
        this.apiURL = apiURL;
        this.request = request;
        this.apiInvocationListener= apiInvocationListener;
    }

    public void exec() {
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, apiURL, response -> {
            Log.v("Test",response);
        }, error -> {
            Log.v("Test","Error");
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return request;
            }
        };
        requestQueue.add(stringRequest);
    }
}
