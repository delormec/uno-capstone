package org.habitatomaha.HOST.Model;

public class SpinnerData {
	
	String spinnerText;
    int value;	
    
	public SpinnerData( String spinnerText, int value ) {
        this.spinnerText = spinnerText;
        this.value = value;
    }

    public String getSpinnerText() {
        return spinnerText;
    }

    public int getValue() {
        return value;
    }

    @Override
	public String toString() {
        return spinnerText;
    }

    

}