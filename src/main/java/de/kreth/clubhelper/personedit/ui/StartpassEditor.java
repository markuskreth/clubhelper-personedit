package de.kreth.clubhelper.personedit.ui;

import com.vaadin.flow.component.dialog.Dialog;

import de.kreth.clubhelper.data.Startpass;

public class StartpassEditor extends Dialog {

    private static final long serialVersionUID = 7214663805933067011L;
    private final Startpass startpass;

    public StartpassEditor(Startpass startpass) {
	super();
	this.startpass = startpass;
    }

}
