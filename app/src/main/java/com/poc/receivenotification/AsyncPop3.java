package com.poc.receivenotification;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;



/**
 * AsyncTask<型1, 型2,型3>
 *
 *   型1 … Activityからスレッド処理へ渡したい変数の型
 *          ※ Activityから呼び出すexecute()の引数の型
 *          ※ doInBackground()の引数の型
 *
 *   型2 … 進捗度合を表示する時に利用したい型
 *          ※ onProgressUpdate()の引数の型
 *
 *   型3 … バックグラウンド処理完了時に受け取る型
 *          ※ doInBackground()の戻り値の型
 *          ※ onPostExecute()の引数の型
 *
 *   ※ それぞれ不要な場合は、Voidを設定すれば良い
 */
public class AsyncPop3 extends AsyncTask<String, Integer, String>{

    private ListView subjectListView;

    private static ArrayList<String> subjectList;

    private ReceptActivity receptActivity;

    private  ArrayAdapter<String> arrayAdapter;

    //region コンストラクタ
    /**
     * コンストラクタ
     * @param listView　リストビュー
     * @param receptActivity 受信画面アクティビティ
     */
    public AsyncPop3(ListView listView, ReceptActivity receptActivity) {
        super();
        this.subjectListView = listView;
        this.receptActivity = receptActivity;
    }
    //endregion

    //region doInBackground関数
    /**
    * バックグラウンドで実行する処理
    *
    *  @return onPostExecute()へ受け渡すデータ
    */
    @Override
    protected String doInBackground(String... integer) {

        String result = null;

        try {
            Pop3 pop3 = new Pop3(receptActivity);
            subjectList = pop3.transaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "UIスレッドへ";
    }
    //endregion

    //region onPostExecute関数
    /*
     * メインスレッドで実行する処理
     *
     *  @param param: doInBackground()から受け渡されるデータ
     */
    @Override
    protected void onPostExecute(String str) {

        arrayAdapter = new ArrayAdapter<String>(receptActivity, android.R.layout.simple_list_item_1, subjectList);

        // デバッグログ表示
        Log.i("取得件名", Arrays.toString(subjectList.toArray()));

        subjectListView.setAdapter(arrayAdapter);
    }
    //endregion
}
