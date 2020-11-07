package de.kreth.clubhelper.personedit.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the startpaesse database table.
 * 
 */
public class Startpass implements Serializable {

    private static final long serialVersionUID = -3913623704796977167L;
    private int id;
    private String startpassNr;

    private List<StartpassStartrechte> startpassStartrechte;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getStartpassNr() {
	return startpassNr;
    }

    public void setStartpassNr(String startpassNr) {
	this.startpassNr = startpassNr;
    }

    public List<StartpassStartrechte> getStartpassStartrechte() {
	return startpassStartrechte;
    }

    public void setStartpassStartrechte(List<StartpassStartrechte> startpassStartrechte) {
	this.startpassStartrechte = startpassStartrechte;
    }

    public StartpassStartrechte addStartpassStartrechte(StartpassStartrechte startpassStartrechte) {
	if (this.startpassStartrechte == null) {
	    this.startpassStartrechte = new ArrayList<>();
	}
	this.startpassStartrechte.add(startpassStartrechte);
	startpassStartrechte.setStartpaesse(this);

	return startpassStartrechte;
    }

    public StartpassStartrechte removeStartpassStartrechte(StartpassStartrechte startpassStartrechte) {
	if (this.startpassStartrechte == null) {
	    this.startpassStartrechte = new ArrayList<>();
	}
	this.startpassStartrechte.remove(startpassStartrechte);
	startpassStartrechte.setStartpaesse(null);

	return startpassStartrechte;
    }

    @Override
    public int hashCode() {
	final int prime = 79;
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
	return "Startpass [startpassNr=" + startpassNr + "]";
    }

}
