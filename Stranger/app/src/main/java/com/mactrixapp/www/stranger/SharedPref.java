package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;

import java.util.Set;

public class SharedPref {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public SharedPref(Context context,String name){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(name,context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setInt(int number){
        editor.putInt("key",number);
        editor.commit();
    }

    public int getInt(){
        return sharedPreferences.getInt("key",0);
    }


    public void setLong(long number){
        editor.putLong("key",number);
        editor.commit();
    }

    public long getLong(){
        return sharedPreferences.getLong("key",0);
    }

    public void setFloat(float number){
        editor.putFloat("key",number);
        editor.commit();
    }

    public float getFloat(){
        return sharedPreferences.getFloat("key",0);
    }





    public void setText(String text){
        editor.putString("keytext",text);
        editor.commit();
    }

    public String getText(){
        return sharedPreferences.getString("keytext","null");
    }

    public void setBoolean(boolean truth){
        editor.putBoolean("booleanKey",truth);
        editor.commit();
    }

    public boolean getBoolean(){
        return sharedPreferences.getBoolean("booleanKey",false);
    }

    public void setStringSet(Set<String> stringSet){
        editor.putStringSet("StringSet",stringSet);
        editor.commit();
    }

    public Set<String> getStringSet(){

        Set<String> defaul = new ArraySet<>();

        return sharedPreferences.getStringSet("StringSet",defaul);
    }

}
