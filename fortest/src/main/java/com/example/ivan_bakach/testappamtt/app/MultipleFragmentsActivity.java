package com.example.ivan_bakach.testappamtt.app;

import  android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ivan_bakach.testappamtt.R;
import com.example.ivan_bakach.testappamtt.fragment.InvisibleFragment;
import com.example.ivan_bakach.testappamtt.fragment.NullpointerFragment;
import com.example.ivan_bakach.testappamtt.fragment.RuntimeFragment;

/**
 @author Ivan_Bakach
 @version on 04.06.2015
 */

public class MultipleFragmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_multiple_fragments_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        RuntimeFragment runtimeFragment = (RuntimeFragment)RuntimeFragment.newInstance();
        NullpointerFragment nullpointerFragment = new NullpointerFragment();
        InvisibleFragment invisibleFragment = new InvisibleFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_runtime, runtimeFragment);
        fragmentTransaction.add(R.id.container_nullpointer, nullpointerFragment);
        fragmentTransaction.add(R.id.container_invisible, invisibleFragment);
        fragmentTransaction.commit();
    }
}