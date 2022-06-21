package com.clipandbooks.sample.sampledualsim;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) { //Build.VERSION_CODES.LOLLIPOP_MR1
            // API 22 부터 SubscriptionManager를 지원하나 부가 API 사용을 위해서 24으로 기준잡음
            SubscriptionManager clsSM = SubscriptionManager.from(mContext);
            List<SubscriptionInfo> clsList = clsSM.getActiveSubscriptionInfoList();

            int listCount = clsList.size();

            if (clsList != null && listCount > 1){
                int index = 0;
                for( SubscriptionInfo clsInfo : clsList ) {
                    String strNumber = clsInfo.getNumber();
                    if( strNumber != null && strNumber.isEmpty() == false )
                    {
                        resultString.append("-<").append(index).append(">----------\n");
                        resultString.append(" - CTN:").append(":").append(correctionPhoneNumber(strNumber))
                                .append(" - CardId:").append(clsInfo.getCardId()).append("\n")  // API 29
                                .append(" - IccId:").append(clsInfo.getIccId()).append("\n")
                                .append(" - CarrierId:").append(clsInfo.getCarrierId()).append("\n")
                                .append(" - CarrierName:").append(clsInfo.getCarrierName()).append("\n")
                                .append(" - DisplayName:").append(clsInfo.getDisplayName()).append("\n")
                                .append(" - SubScriptionId:").append(clsInfo.getSubscriptionId()).append("\n")
                                .append(" - SubScriptionType:").append(clsInfo.getSubscriptionType()).append("\n");

                    }
                    index++;
                }
                index = 0;

                Log.d("TAG", "resultString" + resultString);
                Log.d("TAG", "resultString.toString()" + resultString);

                resultString.append("---------\n");

                int defDataSubScriptionId;
                int defSmsSubScriptionId;
                int defVoiceSubScriptionId;
                int defSubscriptionId;

                // API 24 이상 부터
                defSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
                defDataSubScriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
                defSmsSubScriptionId = SubscriptionManager.getDefaultSmsSubscriptionId();
                defVoiceSubScriptionId = SubscriptionManager.getDefaultVoiceSubscriptionId();

                resultString.append("- def SubScriptionId :").append(defSubscriptionId).append("\n");
                resultString.append("- def Data SubScriptionId :").append(defDataSubScriptionId).append("\n");
                resultString.append("- def SMS SubScriptionId :").append(defSmsSubScriptionId).append("\n");
                resultString.append("- def Voice SubScriptionId :").append(defVoiceSubScriptionId).append("\n");


                mResult.setText(resultString.toString());
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