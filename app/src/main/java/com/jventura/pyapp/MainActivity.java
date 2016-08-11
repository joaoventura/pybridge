package com.jventura.pyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jventura.pybridge.AssetExtractor;
import com.jventura.pybridge.PyBridge;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Extract python files from assets
        AssetExtractor assetExtractor = new AssetExtractor(this);
        assetExtractor.removeAssets("python");
        assetExtractor.copyAssets("python");

        // Get the extracted assets directory
        String pythonPath = assetExtractor.getAssetsDataDir() + "python";

        // Start the Python interpreter
        PyBridge.start(pythonPath);

        // Call a Python function
        try {
            JSONObject json = new JSONObject();
            json.put("function", "greet");
            json.put("name", "Python 3.5");

            JSONObject result = PyBridge.call(json);
            String answer = result.getString("result");

            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(answer);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Stop the interpreter
        PyBridge.stop();
    }
}
