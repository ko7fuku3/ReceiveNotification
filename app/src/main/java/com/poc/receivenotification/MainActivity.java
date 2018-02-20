package com.poc.receivenotification;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button registButton;

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
        registButton = findViewById(R.id.button_regist);

        /**
         * 登録ボタンクリックイベント
         */
        registButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // ホスト名を取得
                EditText inputHostName = findViewById(R.id.editTextHostName);
                String hostName = inputHostName.getText().toString();

                // ポート番号を取得
                EditText inputPortNo = findViewById(R.id.editTextPort);
                String portNo = inputPortNo.getText().toString();

                // アカウントIDを取得
                EditText inputAccountID = findViewById(R.id.editTextAccount);
                String accountID = inputAccountID.getText().toString();

                // パスワードを取得
                EditText inputPassword = findViewById(R.id.editTextPassword);
                String password = inputPassword.getText().toString();

                // 入力値チェック
                int leq = isInputData(hostName, portNo, accountID, password);

                if (leq == 9) {
                } else {
                    // 入力情報を端末ストレージに保存
                    saveToAccount(hostName, portNo, accountID, password);
                    // メール受信画面へ遷移
                    Intent intent = new Intent(getApplication(), ReceptActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //region 入力情報チェック
    /**
     * 入力情報チェック
     * @param hostName ホスト名
     * @param portNo ポート番号
     * @param accountID アカウント情報
     * @param password パスワード
     * @return
     */
    private  int isInputData(String hostName, String portNo, String accountID, String password) {

        int result = 0;

        // ホスト名の入力値チェック
        if (hostName.length() == 0) {
            TextView errMsg = findViewById(R.id.errMsg_HostName);
            errMsg.setText("ホスト名を入力してください");
            result = 9;
        } else {
            TextView errMsg = findViewById(R.id.errMsg_HostName);
            errMsg.setText("");
        }

        // ポート番号の入力値チェック
        if (portNo.length() == 0) {
            TextView errMsg = findViewById(R.id.errMsg_Prot);
            errMsg.setText("ポート番号を入力してください");
            result = 9;
        // 数値以外ならエラー文言表示
        } else if (!isNumber(portNo)) {
            TextView errMsg = findViewById(R.id.errMsg_Prot);
            errMsg.setText("数字を入力してください");
            result = 9;
        } else {
            TextView errMsg = findViewById(R.id.errMsg_Prot);
            errMsg.setText("");
        }

        // アカウントの入力値チェック
        if (accountID.length() == 0) {
            TextView errMsg = findViewById(R.id.errMsg_Account);
            errMsg.setText("アカウントIDを入力してください");
            result = 9;
        } else {
            TextView errMsg = findViewById(R.id.errMsg_Account);
            errMsg.setText("");
        }

        // パスワードの入力値チェック
        if (password.length() == 0) {
            TextView errMsg = findViewById(R.id.errMsg_Password);
            errMsg.setText("パスワードを入力してください");
            result = 9;
        } else {
            TextView errMsg = findViewById(R.id.errMsg_Password);
            errMsg.setText("");
        }

        // 未入力がある場合、9を返却
        if (result == 9) {
            return 9;
        }

        return 0;
    }
    //endregion

    private boolean isNumber(String portNo) {
        //判定する文字列
        String str = portNo;

        //判定するパターンを生成
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(str);

        return m.find();
    }

    //region アカウント情報を端末の内部ストレージに保存
    /**
     * アカウント情報を端末の内部ストレージに保存
     */
    private void saveToAccount(String hostName, String portNo, String accountID, String password) {



        // アカウント情報を保存
        SharedPreferences sharedPreferences=  getSharedPreferences("accountInfo" , MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 端末にアカウント情報を保存
        // ホスト名
        editor.putString("hostName", hostName);
        // ポート番号をInt型に変換
        int convertPort = Integer.parseInt(portNo);
        // ポート番号
        editor.putInt("portNo", convertPort);
        // ユーザーID
        editor.putString("accountID", accountID);
        // パスワード
        editor.putString("password", password);

        editor.commit();
    }
    //endregion
}
