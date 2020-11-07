package de.kreth.clubhelper.personedit.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ConfirmDialog {

    private String title;
    private String message;

    private Button confirm;
    private Button reject;
    private Button cancel;
    private final Dialog dlg;

    public ConfirmDialog() {
	dlg = new Dialog();
    }

    public ConfirmDialog withTitle(String titleText) {
	this.title = titleText;
	return this;
    }

    public ConfirmDialog withMessage(String message) {
	this.message = message;
	return this;
    }

    public ConfirmDialog withRejectButton(String rejectText,
	    ComponentEventListener<ClickEvent<Button>> clickListener) {
	reject = new Button(rejectText, new CloseDialogAndDelegate(dlg, clickListener));
	reject.getStyle().set("color", "red");
	return this;
    }

    public ConfirmDialog withConfirmButton(String confirmText,
	    ComponentEventListener<ClickEvent<Button>> clickListener) {
	confirm = new Button(confirmText, new CloseDialogAndDelegate(dlg, clickListener));
	return this;
    }

    public ConfirmDialog withCancelButton(String cancelText,
	    ComponentEventListener<ClickEvent<Button>> clickListener) {
	cancel = new Button(cancelText, new CloseDialogAndDelegate(dlg, clickListener));
	cancel.getStyle().set("color", "blue");
	return this;
    }

    public void open() {
	if (message == null) {
	    throw new IllegalStateException("A message must be given for a " + getClass().getSimpleName());
	}
	if (confirm == null && reject == null && cancel == null) {
	    throw new IllegalStateException("At least one Button must be set for a " + getClass().getSimpleName());
	}

	if (title != null) {
	    dlg.add(new H1(title));
	}

	dlg.add(new Text(message));

	HorizontalLayout buttonLayout = new HorizontalLayout();
	if (reject != null) {
	    buttonLayout.add(reject);
	}
	if (cancel != null) {
	    buttonLayout.add(cancel);
	}
	if (confirm != null) {
	    buttonLayout.add(confirm);
	}
	dlg.add(buttonLayout);
	dlg.open();
    }

    class CloseDialogAndDelegate implements ComponentEventListener<ClickEvent<Button>> {

	private static final long serialVersionUID = 1L;

	private final Dialog dlg;
	private final ComponentEventListener<ClickEvent<Button>> clickListener;

	public CloseDialogAndDelegate(Dialog dlg, ComponentEventListener<ClickEvent<Button>> clickListener) {
	    super();
	    this.dlg = dlg;
	    this.clickListener = clickListener;
	}

	@Override
	public void onComponentEvent(ClickEvent<Button> event) {
	    dlg.close();
	    this.clickListener.onComponentEvent(event);
	}

    }
}
