package com.excercise.nns.switter.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.excercise.nns.switter.R;
import com.excercise.nns.switter.contract.OAuthContract;
import com.excercise.nns.switter.databinding.ActivityOauthBinding;
import com.excercise.nns.switter.utils.TwitterUtils;
import com.excercise.nns.switter.viewmodel.OAuthViewModel;

import twitter4j.Twitter;

public class OAuthActivity extends AppCompatActivity implements OAuthContract {

    public static void start(Context context) {
        Intent intent = new Intent(context, OAuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOauthBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_oauth);
        Twitter twitter = TwitterUtils.getTwitterInstance(this);
        // set ViewModel
        OAuthViewModel viewModel = new OAuthViewModel(this, twitter);
        viewModel.setPin(binding.pinEditor.getText().toString());
        binding.setViewmodel(viewModel);
    }

    @Override
    public void RequestSuccessful(Intent intent) {
        Toast.makeText(this, "request success.", Toast.LENGTH_SHORT).show();
        // Twitterの認証ブラウザーに接続
        startActivity(intent);
    }

    @Override
    public void RequestFailure() {
        Toast.makeText(this, "request failure.\nplease try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OAuthSuccessful() {
        // タイムライン画面へ移動
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        HomeActivity.start(this);
        finish();
    }

    @Override
    public void OAuthFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
