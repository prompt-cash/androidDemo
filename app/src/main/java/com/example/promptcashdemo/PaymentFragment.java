package com.example.promptcashdemo;

import android.annotation.TargetApi;
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

public class PaymentFragment extends Fragment {

    final String PROMPT_API = "https://prompt.cash/demo?amount=";

    public static double mAmount = 0.01;

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
                NavHostFragment.findNavController(PaymentFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        // set up web view for javascript execution
        binding.webView.getSettings().setJavaScriptEnabled(true);
        // ensure we will stay on the page and dont open a exernal browser
        binding.webView.setWebViewClient(new MyWebViewClient());
        // and load the payment page
        binding.webView.loadUrl(this.PROMPT_API + mAmount);

        // set up a timer to check every second if the payment was successfull
        FragmentActivity activity = getActivity();
        PaymentFragment This = this;
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

        //
        // We are checkign if the payment was successfull by executing some javascript and checking the returned value
        //

        String javascript = "promptCfg.payment.status";
        binding.webView.evaluateJavascript(javascript, new ValueCallback<String>() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onReceiveValue(String s) {
                if(s.equals("\"PAID\"")){
                    Toast.makeText(activity.getApplicationContext(), "Payed Successfully", Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(PaymentFragment.this)
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