package io.github.ornelasf1.soundboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView title = (TextView)findViewById(R.id.title);
        Typeface solid_font = Typeface.createFromAsset(getAssets(), "fonts/FREEDOM.otf");
        title.setTypeface(solid_font);




    }

    public void clickSoundsButt(View v){
        Intent intent = new Intent(this, SoundsActivity.class);
        startActivity(intent);

    }

    public void clickCustomButt(View v){
        Intent intent = new Intent(this, customActivity.class);
        startActivity(intent);
    }

}
