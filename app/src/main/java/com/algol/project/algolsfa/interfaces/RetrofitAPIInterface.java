package com.algol.project.algolsfa.interfaces;

import com.algol.project.algolsfa.pojos.response.TestModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by swarnavo.dutta on 1/28/2019.
 */

public interface RetrofitAPIInterface {
    @GET("hndlMyTeam.ashx")
    Call<TestModel> getMyTeam(@Header("userid") String userID);
}
