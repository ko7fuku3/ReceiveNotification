package com.poc.receivenotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private  AsyncPop3 pop3;

    private ListView listView;
    private  Button registButton;

    private static ArrayList<String> resultList;

    /**
     * Activityの初期処理
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO　デバッグ用にアカウント情報をクリア
        SharedPreferences sharedPreferences=  getSharedPreferences("accountInfo" , MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

        // 保存したアカウント情報を取得
        SharedPreferences getPreferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        // ホスト名の設定
        final String hostName = getPreferences.getString("hostName", "noValue");

        // アカウント情報が登録済みの場合は、受信画面へ遷移
        if (!"noValue".equals(hostName)) {
            // メール受信画面へ遷移
            Intent intent = new Intent(getApplication(), ReceptActivity.class);
            startActivity(intent);
        }

        // アカウント情報登録画面のレイアウトファイルを設定
        setContentView(R.layout.activity_main);
        // 登録ボタンIDの取得
        registButton = (Button) findViewById(R.id.button_regist);

        /**
         * 登録ボタンクリックイベント
         */
        registButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // アカウント情報を保存
                saveToAccount();

                // メール受信画面へ遷移
                Intent intent = new Intent(getApplication(), ReceptActivity.class);
                startActivity(intent);
            }

        });
    }

    //region アカウント情報を端末の内部ストレージに保存
    /**
     * アカウント情報を端末の内部ストレージに保存
     */
    private void saveToAccount() {
        // 登録画面の入力情報を取得
        // ホスト名
        EditText hostName = (EditText) findViewById(R.id.editTextHostName);
        // ポート番号
        EditText portNo = (EditText) findViewById(R.id.editTextPort);
        // アカウントID
        EditText accountID = (EditText) findViewById(R.id.editTextAccount);
        // password
        EditText password = (EditText) findViewById(R.id.editTextPassword);

        // アカウント情報を保存
        SharedPreferences sharedPreferences=  getSharedPreferences("accountInfo" , MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // ホスト名を保存
        editor.putString("hostName", hostName.getText().toString());
        // ポート番号をInt型に変換
        int convertPort = Integer.parseInt(portNo.getText().toString());
        // ポート番号を保存
        editor.putInt("portNo", convertPort);
        // アカウント情報を保存
        editor.putString("accountID", accountID.getText().toString());
        // パスワードを保存する
        editor.putString("password", password.getText().toString());

        editor.commit();
    }
    //endregion
}
