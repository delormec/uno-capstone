package com.example.habitathumanityapp;

public class MyData {
	public MyData( String spinnerText, int value ) {
        this.spinnerText = spinnerText;
        this.value = value;
    }

    public String getSpinnerText() {
        return spinnerText;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return spinnerText;
    }

    String spinnerText;
    int value;	

}