package de.kreth.clubhelper.personedit.data;

import java.io.Serializable;

/**
 * The persistent class for the adress database table.
 * 
 */
public class Adress implements Serializable {

    private static final long serialVersionUID = 8216273166570667412L;

    private int id;
    private String adress1;

    private String adress2;

    private String city;

    private String plz;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getAdress1() {
	return adress1;
    }

    public void setAdress1(String adress1) {
	this.adress1 = adress1;
    }

    public String getAdress2() {
	return adress2;
    }

    public void setAdress2(String adress2) {
	this.adress2 = adress2;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getPlz() {
	return plz;
    }

    public void setPlz(String plz) {
	this.plz = plz;
    }

    @Override
    public int hashCode() {
	final int prime = 109;
	int result = super.hashCode();
	result = prime * result;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	return super.equals(obj);
    }

    @Override
    public String toString() {
	return "Adress [adress1=" + adress1 + ", adress2=" + adress2 + ", plz=" + plz + ", city=" + city + "]";
    }

}
