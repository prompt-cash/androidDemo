package com.example.promptcashdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.promptcashdemo.databinding.FragmentSecondBinding;

import java.util.Timer;
import java.util.TimerTask;

public class SecondFragment extends Fragment {
    final String PROMPT_API = "http://prompt-cash.trax.local:2929/demo?amount=0.01";

    private FragmentSecondBinding binding;

    Timer mTimer = new Timer();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.loadUrl(this.PROMPT_API);

        FragmentActivity activity = getActivity();

        SecondFragment This = this;

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public void run() {
                        This.CheckPayment();
                    }
                });
            }
        }, 1000, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CheckPayment(){
        FragmentActivity activity = getActivity();

        String javascript = "promptCfg.payment.status";
        binding.webView.evaluateJavascript(javascript, new ValueCallback<String>() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onReceiveValue(String s) {
                if(s.equals("\"PAID\"")){
                    Toast.makeText(activity.getApplicationContext(), "Payed Successfully", Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_thirdFragment);

                    mTimer.cancel();
                    mTimer.purge();
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            /*if ("prompt-cash.trax.local".equals(request.getUrl().getHost())) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;*/
            return false;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}