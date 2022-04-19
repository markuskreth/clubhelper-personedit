package de.kreth.clubhelper.personedit.ui.components;

public class StoreConfirmedEvent<T> {

	private final T storedItem;
	
	public StoreConfirmedEvent(T storedItem) {
		super();
		this.storedItem = storedItem;
	}

	public T getStoredItem() {
		return storedItem;
	}
	
}
