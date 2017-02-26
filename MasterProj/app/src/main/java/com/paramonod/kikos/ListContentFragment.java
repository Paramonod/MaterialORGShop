/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paramonod.kikos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.android.materialdesigncodelab.R;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_POSITION, getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_PRODUCTS = "Shops";
        private static final String TAG_PID = "idShops";
        private static final String TAG_NAME = "ShopName";
        JSONParser jParser = new JSONParser();
        private static String url_all_products = "http://192.168.0.111/products.php";
        JSONArray products = null;
        private static final int LENGTH = 18;

        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final Drawable[] mPlaceAvators;
        private ArrayList<String> Places = new ArrayList<>();
        private ArrayList<String> PlaceDesc = new ArrayList<>();
        private ArrayList<String> Ava = new ArrayList<>();

        class LoadAllProducts /* extends AsyncTask<String, String, String> */{


            /**
             * Получаем все продукт из url
             */
            protected String Load() /*String doInBackground(String... args)*/ {
                // Будет хранить параметры
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // получаем JSON строк с URL
                JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

                //Log.d("All Products: ", json.toString());

                try {
                    // Получаем SUCCESS тег для проверки статуса ответа сервера
                    int success = 1/*json.getInt(TAG_SUCCESS)*/;

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
                            //HashMap<String, String> map = new HashMap<String, String>();

                            // добавляем каждый елемент в HashMap ключ => значение
                            // map.put(TAG_PID, id);
                            //map.put(TAG_NAME, name);
                            Places.add(id);
                            PlaceDesc.add(name);


                            // добавляем HashList в ArrayList
                            //productsList.add(map);
                        }
                    }/* else {
                        // продукт не найден
                        // Запускаем Add New Product Activity
                        Intent i = new Intent(getApplicationContext(),
                                NewProductActivity.class);
                        // Закрытие всех предыдущие activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            /*mPlaces = resources.getStringArray(R.array.places);
            mPlaceDesc = resources.getStringArray(R.array.place_desc);*/
            new LoadAllProducts().Load();
            mPlaces = new String[Places.size()];
            mPlaceDesc = new String[PlaceDesc.size()];
            for (int i = 0; i < Places.size(); i++) {
                mPlaces[i] = Places.get(i);
                mPlaceDesc[i] = PlaceDesc.get(i);
            }
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
            mPlaceAvators = new Drawable[a.length()];
            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            a.recycle();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            holder.name.setText(mPlaces[position % mPlaces.length]);
            holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

}
