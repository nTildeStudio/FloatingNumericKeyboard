package floatingnumerickeyboard.ntilde.com.floatingnumerickeyboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FloatingNumericKeyboard.setBackgroundColor(Color.TRANSPARENT);

        FloatingNumericKeyboard.setFloatingNumericKeyboard(this, R.id.editText1, new FloatingNumericKeyboard.FNKConfig(null,Color.argb(100,255,100,0)));
        FloatingNumericKeyboard.setFloatingNumericKeyboard(this, R.id.editText2, new FloatingNumericKeyboard.FNKConfig(R.layout.fnk_decimal,null));
        FloatingNumericKeyboard.setFloatingNumericKeyboard(this, R.id.editText3);
        FloatingNumericKeyboard.setFloatingNumericKeyboard(this, R.id.editText4);
        FloatingNumericKeyboard.setFloatingNumericKeyboard(this, R.id.editText5);

    }

}
