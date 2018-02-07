package com.poc.receivenotification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * メール受信アクティビィティ
 */
public class ReceptActivity extends AppCompatActivity {

    private  AsyncPop3 pop3;
    private ListView listView;
    private Button btnRecept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // レイアウトファイルを設定
        setContentView(R.layout.activity_recept);

        // ボタンIDの取得
        btnRecept = (Button) findViewById(R.id.button);
        // テキストIDの取得
        listView = (ListView)findViewById(R.id.listViewId);

        /**
         * 受信ボタンクリックイベント
         */
        btnRecept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                 pop3 = new AsyncPop3(listView,ReceptActivity.this);
                // 通信処理を行う際は、AsncTaskクラスを作成し非同期で別スレッドで処理を行う
                pop3.execute("通信開始");
                // インスタンスのクリア
                pop3 = null;
            }
        });
    }
}
