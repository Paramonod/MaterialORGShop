package com.example.android.materialdesigncodelab;

/**
 * Created by Dmitry Paramonov on 12/29/2016.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.android.materialdesigncodelab.JSONParser;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllProductsActivity {

    private ProgressDialog pDialog;

    // Создаем JSON парсер
    JSONParser jParser = new JSONParser();

    public static ArrayList<HashMap<String, String>> productsList;

    // url получения списка всех продуктов
    private static String url_all_products = "http://192.168.0.101/get_all_products.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Shops";
    private static final String TAG_PID = "idShops";
    private static final String TAG_NAME = "ShopName";

    // тут будет хранится список продуктов
    JSONArray products = null;

    public void Create() {
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Загружаем продукты в фоновом потоке
        new LoadAllProducts().execute();

        // получаем ListView
        //ListView lv = getListView();

        // на выбор одного продукта
        // запускается Edit Product Screen
        /*lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Запускаем новый intent который покажет нам Activity
                Intent in = new Intent(getApplicationContext(), EditProductActivity.class);
                // отправляем pid в следующий activity
                in.putExtra(TAG_PID, pid);

                // запуская новый Activity ожидаем ответ обратно
                startActivityForResult(in, 100);
            }
        });*/

    }

    // Ответ из Edit Product Activity
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если результующий код равен 100
        if (resultCode == 100) {
            // если полученный код результата равен 100
            // значит пользователь редактирует или удалил продукт
            // тогда мы перезагружаем этот экран
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }*/

    /**
     * Фоновый Async Task для загрузки всех продуктов по HTTP запросу
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         * */
        //@Override
        /*protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllProductsActivity.this);
            pDialog.setMessage("Загрузка продуктов. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }*/

        /**
         * Получаем все продукт из url
         * */
        protected String doInBackground(String... args) {
            // Будет хранить параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // получаем JSON строк с URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            Log.d("All Products: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт найден
                    // Получаем масив из Продуктов
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // перебор всех продуктов
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Сохраняем каждый json елемент в переменную
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);

                        // Создаем новый HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // добавляем каждый елемент в HashMap ключ => значение
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);

                        // добавляем HashList в ArrayList
                        productsList.add(map);
                    }
                } else {
                    // продукт не найден
                    // Запускаем Add New Product Activity
                   /* Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Закрытие всех предыдущие activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            /*runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                        /*ListAdapter adapter = new SimpleAdapter(
                                AllProductsActivity.this, productsList,
                                R.layout.list_item, new String[] { TAG_PID,
                                TAG_NAME},
                                new int[] { R.id.pid, R.id.name });
                        // обновляем listview
                        setListAdapter(adapter);

                }
            });*/

        }

    }

}