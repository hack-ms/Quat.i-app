package com.hackms.capibot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.android.AIConfiguration;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ACCESS_TOKEN = "c70a1c8319ec4c808b54c28264608682";

    private EditText consultaEdText;
    private TextView resultadoTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultaEdText = findViewById(R.id.consulta_edText);
        resultadoTxtView = findViewById(R.id.resultado_text);

        final AIConfiguration config = new AIConfiguration(CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
    }

    public void consultar(View view) {
        resultadoTxtView.setText(consultaEdText.getText().toString());
    }
}
