package de.kreth.clubhelper.personedit.data;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the startpass_startrechte database table.
 * 
 */
public class StartpassStartrechte implements Serializable {

    private static final long serialVersionUID = -5305964867846642236L;

    private int id;

    private String fachgebiet;

    private Date startrechtBeginn;

    private Date startrechtEnde;

    private String vereinName;

    private Startpass startpaesse;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Date getStartrechtBeginn() {
	return new Date(this.startrechtBeginn.getTime());
    }

    public Date getStartrechtEnde() {
	return new Date(this.startrechtEnde.getTime());
    }

    public void setStartrechtEnde(Date startrechtEnde) {
	this.startrechtEnde = startrechtEnde;
    }

    public String getFachgebiet() {
	return fachgebiet;
    }

    public void setFachgebiet(String fachgebiet) {
	this.fachgebiet = fachgebiet;
    }

    public String getVereinName() {
	return vereinName;
    }

    public void setVereinName(String vereinName) {
	this.vereinName = vereinName;
    }

    public Startpass getStartpaesse() {
	return startpaesse;
    }

    public void setStartpaesse(Startpass startpaesse) {
	this.startpaesse = startpaesse;
    }

    public void setStartrechtBeginn(Date startrechtBeginn) {
	this.startrechtBeginn = startrechtBeginn;
    }

    @Override
    public int hashCode() {
	final int prime = 97;
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
	return "StartpassStartrechte [fachgebiet=" + fachgebiet + ", startrechtBeginn=" + startrechtBeginn
		+ ", startrechtEnde=" + startrechtEnde + ", vereinName=" + vereinName + ", startpaesse=" + startpaesse
		+ "]";
    }

}
