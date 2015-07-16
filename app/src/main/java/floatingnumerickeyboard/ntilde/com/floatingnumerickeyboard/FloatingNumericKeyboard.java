package floatingnumerickeyboard.ntilde.com.floatingnumerickeyboard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class FloatingNumericKeyboard {

    private static Integer etSelected;
    private static Activity activity;

    //Configuracion
    private static FNKConfig defaultConfig=new FNKConfig(R.layout.fnk_default, Color.argb(100,100,100,100));
    private static Map<Integer, FNKConfig> specificConfig=new HashMap<Integer, FNKConfig>();

    public static void setFloatingNumericKeyboard(Activity act, int id, FNKConfig config){

        activity=act;
        View v=activity.findViewById(id);
        if(v!=null && v instanceof EditText){
            specificConfig.put(v.getId(), config==null?new FNKConfig():config);
            EditText et=(EditText)v;
            et.setInputType(EditorInfo.TYPE_NULL);
            et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(etSelected==null||v.getId()!=etSelected) {
                        drawFloatingNumericKeyboard(v);
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Metodo encargado de asignar el teclado numerico flotante a una vista
     * @param act Actividad sobre la que trabajamos
     * @param id Identificador de la vista (Edittext) sobre el que asignar el teclado
     */
    public static void setFloatingNumericKeyboard(Activity act, int id){
        setFloatingNumericKeyboard(act, id, null);
    }

    /**
     * Clase encargada de mostrar el teclado en pantalla
     * @param v Vista sobre la que se usará el teclado
     */
    private static void drawFloatingNumericKeyboard(View v){

        etSelected=v.getId();

        ViewGroup rootView=(ViewGroup)activity.getWindow().getDecorView().findViewById(android.R.id.content);
        RelativeLayout rlKbContainer=new RelativeLayout(activity);
        RelativeLayout.LayoutParams lpRlKbContainer=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlKbContainer.setLayoutParams(lpRlKbContainer);
        rlKbContainer.setBackgroundColor(specificConfig.get(v.getId()).getBackgroundColor());
        rootView.addView(rlKbContainer);

        rlKbContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((ViewGroup)v.getParent()).removeView(v);
                etSelected=null;
                return false;
            }
        });

        View keyboard=activity.getLayoutInflater().inflate(specificConfig.get(v.getId()).getKeyboard(),rlKbContainer);

        setKey(keyboard, v, R.id.fnk_key_0, "0");
        setKey(keyboard, v, R.id.fnk_key_1, "1");
        setKey(keyboard, v, R.id.fnk_key_2, "2");
        setKey(keyboard, v, R.id.fnk_key_3, "3");
        setKey(keyboard, v, R.id.fnk_key_4, "4");
        setKey(keyboard, v, R.id.fnk_key_5, "5");
        setKey(keyboard, v, R.id.fnk_key_6, "6");
        setKey(keyboard, v, R.id.fnk_key_7, "7");
        setKey(keyboard, v, R.id.fnk_key_8, "8");
        setKey(keyboard, v, R.id.fnk_key_9, "9");
        setKey(keyboard, v, R.id.fnk_key_comma, ".");
        setKey(keyboard, v, R.id.fnk_key_inv, "inv");
        setKey(keyboard, v, R.id.fnk_key_del, "del");
        setKey(keyboard, v, R.id.fnk_key_clean, "clean");

        Button invKey=(Button)keyboard.findViewById(R.id.fnk_key_inv);
        if(invKey!=null){
            String etValue=((EditText)v).getText().toString();
            if(etValue.length()>0){
                if(etValue.charAt(0)=='-'){
                    invKey.setText("+");
                }
            }
        }

        keyboard.post(new MoveKeyboard(keyboard, v));

    }

    /**
     * Metodo que se encarga de asignar el listener a las distintas teclas
     * @param keyboard
     * @param et
     * @param key
     * @param value
     */
    private static void setKey(View keyboard, View et, int key, String value){
        View vKey=keyboard.findViewById(key);
        if(vKey!=null){
            vKey.setOnClickListener(new fnkKeyListener((EditText)et,value));
        }
    }

    /**
     * Clase encargada de posicionar el teclado en pantalla
     */
    private static class MoveKeyboard implements Runnable{

        View keyboard;
        View et;

        public MoveKeyboard(View keyboard, View et){
            this.keyboard=keyboard;
            this.et=et;
        }

        @Override
        public void run() {
            keyboard.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            int[] etLocation=new int[2];
            et.getLocationInWindow(etLocation);
            Point displaySize=new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            int leftPadding=displaySize.x>=etLocation[0]+keyboard.getMeasuredWidth()?etLocation[0]:etLocation[0]-keyboard.getMeasuredWidth()+et.getWidth();
            leftPadding+=displaySize.y>=etLocation[1]+keyboard.getMeasuredHeight()+et.getWidth()?0:displaySize.x>=etLocation[0]+keyboard.getMeasuredWidth()?et.getWidth():et.getWidth()*-1;
            int topPadding=displaySize.y>=etLocation[1]+keyboard.getMeasuredHeight()?etLocation[1]-et.getHeight():etLocation[1]-keyboard.getMeasuredHeight()-et.getHeight();

            keyboard.setPadding(leftPadding,topPadding,0,0);
            ((ViewGroup)keyboard).getChildAt(0).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Clase encargada de gestionar las pulsaciones en las distintas teclas del teclado
     */
    private static class fnkKeyListener implements View.OnClickListener{

        EditText et;
        String value;

        public fnkKeyListener(EditText et, String value){
            this.et=et;
            this.value=value;
        }

        @Override
        public void onClick(View v) {
            String etValue=et.getText().toString();
            switch (value){
                case "del":
                    if(etValue.length()>0) {
                        et.setText(etValue.substring(0, etValue.length() - 1));
                    }
                    break;
                case "clean":
                    et.setText("");
                    break;
                case "inv":
                    String sign=((Button)v).getText().toString();
                    if(etValue.length()==0){
                        et.setText("+".equals(sign)?"":"-");
                    }
                    else{
                        if('-'==etValue.charAt(0)){
                            et.setText(etValue.replace("-",""));
                        }
                        else{
                            et.setText(sign+etValue);
                        }
                    }
                    ((Button)v).setText("-".equals(sign)?"+":"-");
                    break;
                default:
                    et.setText(et.getText() + value);
            }
        }
    }

    /**
     * Fija el fondo de pantalla generico para cuando se muestre el teclado
     * @param color Color de fondo
     */
    public static void setBackgroundColor(int color){
        defaultConfig.setBackgroundColor(color);
    }

    /**
     * Fija el layout generico de teclado
     * @param keyboard Layout de teclado
     */
    public static void setKeyboard(int keyboard){
        defaultConfig.setKeyboard(keyboard);
    }

    /**
     * Clase encargada de guardar la configuracion del teclado
     */
    public static class FNKConfig{

        private Integer backgroundColor;
        private Integer keyboard;

        public FNKConfig(){

        }

        public FNKConfig(Integer keyboard, Integer backgroundColor){
            this.keyboard=keyboard;
            this.backgroundColor=backgroundColor;
        }

        public Integer getBackgroundColor() {
            return backgroundColor!=null?backgroundColor:defaultConfig.getBackgroundColor();
        }

        public void setBackgroundColor(Integer backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Integer getKeyboard() {
            return keyboard!=null?keyboard:defaultConfig.getKeyboard();
        }

        public void setKeyboard(Integer keyboard) {
            this.keyboard = keyboard;
        }
    }


}
