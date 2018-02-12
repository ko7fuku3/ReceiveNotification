package com.poc.receivenotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by koshiro on 2018/02/08.
 */
public class Pop3 {
    private Socket mailServer;
    private BufferedReader reader;
    private BufferedWriter writer;

    //region アカウント設定
    /**
     * アカウント設定
     * @throws Exception
     */
    public Pop3(ReceptActivity receptActivity) throws Exception{

        // 保存したアカウント情報を取得
        SharedPreferences getPreferences = receptActivity.getSharedPreferences("accountInfo", Context.MODE_PRIVATE);

        // ホスト名の設定
        final String hostName = getPreferences.getString("hostName", "noValue");
        // ポートの設定
        int portNo = getPreferences.getInt("portNo", -1);
        // ユーザー情報の設定
        final String accountID = getPreferences.getString("accountID", "noValue");
        // パスワードの設定
        final String password = getPreferences.getString("password", "noValue");

        // 通信の開始
        mailServer =new Socket(hostName, portNo);
        reader=new BufferedReader(new InputStreamReader(mailServer.getInputStream()));
        writer=new BufferedWriter(new OutputStreamWriter(mailServer.getOutputStream()));

        if(isReady()&&isReady("user "+accountID)&&isReady("pass "+password)){
          Log.i("InfoLog", "通信成功");
        }
        else{
            Log.d("debugLog","通信失敗");
            mailServer.close();
        }
    }
    //endregion

    //region メールサーバーと通信出来ているか判定
    /**
     * メールサーバーと通信出来ているか判定
     * @return OK：通信成功 NG：通信失敗
     * @throws IOException
     */
    private boolean isReady() throws IOException {
        final boolean OK=true;
        final boolean NG=!OK;

        return reader.readLine().matches("^\\+OK.*$")? OK: NG;
    }
    //endregion

    //region メールサーバーにコマンド送信
    /**
     * メールサーバーにコマンド送信
     * @param message
     * @return
     * @throws IOException
     */
    private boolean isReady(String message) throws IOException{
        final String end="\r\n";

        writer.write(message+end);
        writer.flush(); //コマンドをメールサーバに送って
        return isReady(); //結果を判定
    }
    //endregion

    //region メール本文取得
    /**
     * メール本文取得
     * @throws Exception
     */
    public ArrayList<String> transaction() throws Exception{

        ArrayList<String> subjectListmain = new ArrayList<String>();


        // メールサーバと接続が続く限り、ループする
        while(!mailServer.isClosed()){

            // メール本文を取得
            subjectListmain = getMessage();

            // 終了コマンドをセットし、通信を終了する
            isReady("quit");

            // サーバーとの通信終了
            mailServer.close();

            // ログ出力
            Log.i("InfoLog","通信正常終了");
        }
        return subjectListmain;
    }
    //endregion

    /**
     * メール本文取得
     * @return 件名リスト
     * @throws IOException
     */
    private ArrayList<String> getMessage() throws IOException {

        String subject;
        String command;
        String mailCount;
        StringBuffer message = new StringBuffer();
        ArrayList<String> mailList = new ArrayList<String>();
        ArrayList<String> subjectList = new ArrayList<String>();

        // リストコマンドをセット
        isReady("list");

        // メール件数を取得
        while (true) {
            // メールサーバからのレスポンスを取得
            mailCount = reader.readLine();
            // "."がきたら取得終了
            if (mailCount.equals(".")) {
                break;
            } else {
                mailList.add(mailCount);
            }
        }

        // 件名を取得し、デコードする
        for (int i = 1; i < mailList.size(); i++) {
            command = "retr" + " " + i;
            // コマンド送信
            isReady(command);
            while (true) {
                // メールサーバからのレスポンスを取得
                String mailInfo = reader.readLine();
                // "."がきたら取得終了
                if (mailInfo.equals(".")) {
                    if (message.length() > 0) {
                        message.deleteCharAt(message.length() - 1); //つけすぎた改行を削除
                    }
                    break;
                } else if (mailInfo.contains("Subject: ")) {
                    // デバッグ用にログ出力
                    Log.i("デコード前の件名", mailInfo);

                    // 取得した件名から=?UTF-8?B?と?=削除しBase64形式にエンコードされた件名を抽出、デコードしListに格納
                    // utf-8
                    if (mailInfo.contains("utf-8")) {
                        String str2 = mailInfo.replaceAll("Subject: =" + "\\" + "?" + "utf-8" + "\\" + "?" + "B" + "\\" + "?", "");
                        str2 = str2.replaceAll("\\" + "?=", "");

                        // デコード
                        subject = decode(str2);
                    // UTF-8
                    } else if (mailInfo.contains("UTF-8")) {
                        String str2 = mailInfo.replaceAll("Subject: =" + "\\" + "?" + "UTF-8" + "\\" + "?" + "B" + "\\" + "?", "");
                        str2 = str2.replaceAll("\\" + "?=", "");

                        // デコード
                        subject = decode(str2);
                    // ISO-2022-JP
                    } else if (mailInfo.contains("ISO-2022-JP")) {
                        String str2 = mailInfo.replaceAll("Subject: =" + "\\" + "?" + "ISO-2022-JP" + "\\" + "?" + "B" + "\\" + "?", "");
                        str2 = str2.replaceAll("\\" + "?=", "");

                        subject = decode(str2);
                    } else {
                        String str2 = mailInfo.replaceAll("Subject: ", "");
                        subject = str2.replaceAll("\\" + "?=", "");
                    }
                    // リストに格納
                    subjectList.add(subject);
                }
            }
        }

        return subjectList;
    }

    //region デコード実施
    /**
     * デコード実施
     * @param encodeStr
     * @return デコード後の件名
     */
    private String decode(String encodeStr) {

        byte[] decodedBytes = Base64.decodeBase64(encodeStr.getBytes());
        String decodeStr = new String(decodedBytes);

        return decodeStr;
    }
    //endregion
}
