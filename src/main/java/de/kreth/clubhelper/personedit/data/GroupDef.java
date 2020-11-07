package de.kreth.clubhelper.personedit.data;

import java.io.Serializable;

/**
 * The persistent class for the groupDef database table.
 */
public class GroupDef implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	GroupDef other = (GroupDef) obj;
	if (id != other.id)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "GroupDef [id=" + getId() + ", name=" + name + "]";
    }
}
