package com.clipandbooks.sample.sampledualsim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Button mBtn;
    private TextView mResult;
    private View mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        mMainLayout = findViewById(R.id.main_layout);
        mBtn = findViewById(R.id.btn);
        mResult = findViewById(R.id.result);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                getCtn();
                break;
        }
    }

    private void getCtn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mMainLayout, "전화권한을 허용해 주세요", Snackbar.LENGTH_SHORT).show();
            return;
        }

        TelephonyManager clsTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder resultString = new StringBuilder();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            // API 22 이상인 경우에 동작한다.
            SubscriptionManager clsSM = SubscriptionManager.from(mContext);
            List<SubscriptionInfo> clsList = clsSM.getActiveSubscriptionInfoList();

            int listCount = clsList.size();

            if (clsList != null && listCount > 1){
                int index = 0;
                for( SubscriptionInfo clsInfo : clsList ) {
                    String strNumber = clsInfo.getNumber();
                    if( strNumber != null && strNumber.isEmpty() == false )
                    {
                        resultString.append("CTN").append(index).append(":").append(correctionPhoneNumber(strNumber)).append("\n");
                    }
                    index++;
                }
                index = 0;
                mResult.setText(resultString.toString());
                Log.d("TAG", "resultString" + resultString);
                Log.d("TAG", "resultString.toString()" + resultString);
            } else {
                mResult.setText("CTN:"+ clsTM.getLine1Number());
            }

        } else {
            mResult.setText("CTN:"+ clsTM.getLine1Number());
        }
    }

    private String correctionPhoneNumber(String rawNumber) {
        String phoneNumber;
        phoneNumber = rawNumber.replaceFirst("\\+[8][2]", "0");
        return phoneNumber;
    }
}