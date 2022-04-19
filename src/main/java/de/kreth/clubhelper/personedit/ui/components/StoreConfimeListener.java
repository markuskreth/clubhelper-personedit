package de.kreth.clubhelper.personedit.ui.components;

public interface StoreConfimeListener<T> {

	void storeConfirmed(StoreConfirmedEvent<T> ev);
}
