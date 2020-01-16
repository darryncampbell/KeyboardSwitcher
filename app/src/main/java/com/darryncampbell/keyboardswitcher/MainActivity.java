package com.darryncampbell.keyboardswitcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

public class MainActivity extends AppCompatActivity implements EMDKManager.EMDKListener {

    private ProfileManager profileManager = null;
    private EMDKManager emdkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        //Check the return status of getEMDKManager
        if (results.statusCode == EMDKResults.STATUS_CODE.SUCCESS) {
            // EMDKManager object creation success
        } else {
            // EMDKManager object creation failed
            showDialog("Error", "Could not create EMDK Manager");
        }

        Button btnEnterpriseKeyboard = findViewById(R.id.btnEnterpriseKeyboard);
        btnEnterpriseKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyProfile("KeyboardEKB");
            }
        });

        Button btnGBoard = findViewById(R.id.btnGBoard);
        btnGBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyProfile("KeyboardGBoard");
            }
        });

        Button btnAOSPKeyboard = findViewById(R.id.btnAOSP);
        btnAOSPKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyProfile("KeyboardAOSP");
            }
        });
    }


    private void applyProfile(String profileName) {
            if (profileManager != null)
            {
                String[] modifyData = new String[1];
                modifyData[0] = "";
                EMDKResults results = profileManager.processProfile(profileName,
                        ProfileManager.PROFILE_FLAG.SET, modifyData);

                if (results.statusCode == EMDKResults.STATUS_CODE.CHECK_XML)
                {
                    String responseXML = results.getStatusString();
                    Log.d("KeyboardSwitch", responseXML);
                    if (responseXML.toLowerCase().contains("error"))
                    {
                        showDialog("Error", "Failed to apply profile");
                    }
                    else
                    {
                        //  it worked
                        Toast.makeText(getApplicationContext(), "" + profileName + " successfully applied", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    showDialog("Error", "Failed to apply profile" + results.getStatusString());
                }
            }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        // Get the ProfileManager object to process the profiles
        profileManager = (ProfileManager) emdkManager
                .getInstance(EMDKManager.FEATURE_TYPE.PROFILE);
    }

    @Override
    public void onClosed() {
        profileManager = null;
    }

    public void showDialog(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick
                                    (DialogInterface dialog,
                                     int id) {
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
