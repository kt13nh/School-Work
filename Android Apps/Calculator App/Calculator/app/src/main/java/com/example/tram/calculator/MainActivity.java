package com.example.tram.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    Operators o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textView = (TextView) findViewById(R.id.numberDisplay);
        o = new Operators();
        this.initButtonListeners();
    }

    //initialize the button listeners
    void initButtonListeners() {
        //blank space to keep the distance of buttons consistent
        Button buttonBlank1 = (Button) this.findViewById(R.id.buttonBlank1);
        buttonBlank1.setClickable(false);
        Button buttonBlank2 = (Button) this.findViewById(R.id.buttonBlank2);
        buttonBlank2.setClickable(false);
        Button buttonMS = (Button) this.findViewById(R.id.buttonMS);
        buttonMS.setOnClickListener(this);
        Button buttonMR = (Button) this.findViewById(R.id.buttonMR);
        buttonMR.setOnClickListener(this);
        Button buttonMC = (Button) this.findViewById(R.id.buttonMC);
        buttonMC.setOnClickListener(this);
        Button buttonAC = (Button) this.findViewById(R.id.buttonAC);
        buttonAC.setOnClickListener(this);
        Button buttonBOpen = (Button) this.findViewById(R.id.buttonBOpen);
        buttonBOpen.setOnClickListener(this);
        Button buttonBClose = (Button) this.findViewById(R.id.buttonBClose);
        buttonBClose.setOnClickListener(this);
        Button buttonDel = (Button) this.findViewById(R.id.buttonDel);
        buttonDel.setOnClickListener(this);
        Button button7 = (Button) this.findViewById(R.id.button7);
        button7.setOnClickListener(this);
        Button button8 = (Button) this.findViewById(R.id.button8);
        button8.setOnClickListener(this);
        Button button9 = (Button) this.findViewById(R.id.button9);
        button9.setOnClickListener(this);
        Button buttonDivide = (Button) this.findViewById(R.id.buttonDivide);
        buttonDivide.setOnClickListener(this);
        Button button4 = (Button) this.findViewById(R.id.button4);
        button4.setOnClickListener(this);
        Button button5 = (Button) this.findViewById(R.id.button5);
        button5.setOnClickListener(this);
        Button button6 = (Button) this.findViewById(R.id.button6);
        button6.setOnClickListener(this);
        Button buttonMultiply = (Button) this.findViewById(R.id.buttonMultiply);
        buttonMultiply.setOnClickListener(this);
        Button button1 = (Button) this.findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button) this.findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = (Button) this.findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button buttonSubtract = (Button) this.findViewById(R.id.buttonSubtract);
        buttonSubtract.setOnClickListener(this);
        Button button0 = (Button) this.findViewById(R.id.button0);
        button0.setOnClickListener(this);
        Button buttonCompute = (Button) this.findViewById(R.id.buttonCompute);
        buttonCompute.setOnClickListener(this);
        Button buttonAdd = (Button) this.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
    }

    //onclick method for each listener
    @Override
    public void onClick(View v) {
        Button b = (Button)v;
        if(b.getId()==R.id.buttonAC){
            o.allClear(textView);
        }
        else if(b.getId()==R.id.buttonBOpen){
            o.openBracket(textView);
        }
        else if(b.getId()==R.id.buttonBClose){
            o.closeBracket(textView);
        }
        else if(b.getId()==R.id.buttonDel){
            o.delete(textView);
        }
        else if(b.getId()==R.id.buttonDivide){
            o.divide(textView);
        }
        else if(b.getId()==R.id.buttonMultiply){
            o.multiply(textView);
        }
        else if(b.getId()==R.id.buttonSubtract){
            o.subtract(textView);
        }
        else if(b.getId()==R.id.buttonAdd){
            o.add(textView);
        }
        else if(b.getId()==R.id.buttonCompute){
            o.compute(textView);
        }
        else if (b.getId()==R.id.buttonMS){
            o.setMemory(textView);
        }
        else if(b.getId()==R.id.buttonMR){
            o.getMemory(textView);
        }
        else if(b.getId()==R.id.buttonMC){
            o.clearMemory(textView);
        }
        else{
            o.numberVal(textView, b.getText().toString());
        }
    }
}
