package ysnows.ysnowssidebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<>();
    private SideBar sideBar;
    private TextView tipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideBar = (SideBar) findViewById(R.id.sideBar);
        tipView = (TextView) findViewById(R.id.tv_tip);
        sideBar.attachTipView(tipView);

        data = new ArrayList<>();
        data.add("A");
        data.add("B");
        data.add("C");
        data.add("D");
        data.add("E");
        data.add("F");
        data.add("G");
        data.add("H");
        data.add("I");
        data.add("J");
        data.add("K");
        data.add("L");
        data.add("M");
        data.add("N");
        data.add("O");
        data.add("P");
        data.add("Q");
        data.add("R");
        data.add("S");
        data.add("T");
        data.add("U");
        data.add("V");
        data.add("W");
        data.add("X");
        data.add("Y");
        data.add("Z");

        sideBar.setData(data);
        sideBar.setOnSelectedListener(new SideBar.OnSelectedListener() {
            @Override
            public void onSelected(int position) {
                Log.d("MainActivity", "position:" + position);
            }
        });

    }
}
