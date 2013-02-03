package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;

public class ListBoxWithAddEditDelete<T> extends Composite {

	@UiField
	Label subjectLabel;

	@UiField
	PushButton addButton;

	@UiField
	PushButton editButton;

	@UiField
	PushButton deleteButton;

	@UiField(provided = true)
	ListBoxWithAddEditDeleteListBox listBox;

	@SuppressWarnings("rawtypes")
	interface MyUiBinder extends UiBinder<Widget, ListBoxWithAddEditDelete> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private static int lastSelectedIndex = 0;

	private Component<T> creatingComponent;

	private Component<T> editingComponent;

	private Component<T> deletingComponent;

	private final Refresher<T> refresher;

	private OnChangeListener<T> onChangeListener;

	private final IdProvider<T> idProvider;

	private final LabelProvider<T> labelProvider;

	public ListBoxWithAddEditDelete(String label, IdProvider<T> idProvider,
			LabelProvider<T> labelProvider, Component<T> creatingComponent,
			Component<T> editingComponent, Component<T> deletingComponent,
			Refresher<T> refresher) {

		this.idProvider = idProvider;
		this.labelProvider = labelProvider;
		this.creatingComponent = creatingComponent;
		this.editingComponent = editingComponent;
		this.deletingComponent = deletingComponent;
		this.refresher = refresher;
		
		listBox = new ListBoxWithAddEditDeleteListBox();
		
		initWidget(uiBinder.createAndBindUi(this));

		subjectLabel.setText(label);
		
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		editButton.setEnabled(true);

		addButton.getUpFace().setImage(
				new Image(KanbanikResources.INSTANCE.addButtonImage()));
		editButton.getUpFace()
				.setImage(
						new Image(KanbanikResources.INSTANCE
								.editButtonDisabledImage()));
		deleteButton.getUpFace().setImage(
				new Image(KanbanikResources.INSTANCE
						.deleteButtonDisabledImage()));

		this.creatingComponent.setup(addButton, "Add");
		this.editingComponent.setup(editButton, "Edit");
		this.deletingComponent.setup(deleteButton, "Delete");

	}

	public void setContent(List<T> content) {
		listBox.setContent(content);
	}

	public T getSelectedDto() {
		return listBox.getSelectedDto();
	}
	
	public void refresh(T dto) {
		listBox.refresh(dto);
		creatingComponent.setDto(dto);
		editingComponent.setDto(dto);
		deletingComponent.setDto(dto);
	}
	
	public void editItem(T dto) {
		listBox.editItem(dto);
	}
	
	public void removeItem(T dto) {
		listBox.removeItem(dto);
	}
	
	public void addNewItem(T dto) {
		listBox.addNewItem(dto);
	}

	public void setSelectedDto(T dto) {
		listBox.setSelectedDto(dto);
	}
	
	public void setOnChangeListener(OnChangeListener<T> onChangeListener) {
		this.onChangeListener = onChangeListener;
	}
	
	class ListBoxWithAddEditDeleteListBox extends ListBox implements ChangeHandler {

		private List<T> items;

		private T selectedDto = null;

		public ListBoxWithAddEditDeleteListBox() {
			addChangeHandler(this);
		}

		public void setContent(List<T> content) {
			if (content == null || content.size() == 0) {
				this.items = new ArrayList<T>();
				return;
			}

			int tmpSelectedBoard = lastSelectedIndex;
			clear();
			this.items = content;
			for (T item : content) {
				addItem(labelProvider.getLabel(item));
			}

			setupSelectedDto();
			lastSelectedIndex = tmpSelectedBoard;
			resetButtonAvailability();
		}

		private void setupSelectedDto() {
			if (items == null) {
				return;
			}

			int index = getSelectedIndex();
			if (items.size() != 0 && index >= 0 && index < items.size()) {
				selectedDto = items.get(index);
			} else {
				selectedDto = null;
			}

			lastSelectedIndex = index;
			if (selectedDto != null) {
				deletingComponent.setDto(selectedDto);
				editingComponent.setDto(selectedDto);
			}
		}

		public void onChange(ChangeEvent event) {
			onChange();
		}

		void onChange() {
			setupSelectedDto();
			if (onChangeListener != null) {
				onChangeListener.onChanged(items, selectedDto);
			}
			resetButtonAvailability();
		}

		private void resetButtonAvailability() {
			editButton.setEnabled(selectedDto != null);
			deleteButton.setEnabled(selectedDto != null);

			if (selectedDto != null) {
				editButton.getUpFace()
						.setImage(
								new Image(KanbanikResources.INSTANCE
										.editButtonImage()));
				deleteButton.getUpFace().setImage(
						new Image(KanbanikResources.INSTANCE
								.deleteButtonImage()));
			} else {
				editButton.getUpFace().setImage(
						new Image(KanbanikResources.INSTANCE
								.editButtonDisabledImage()));
				deleteButton.getUpFace().setImage(
						new Image(KanbanikResources.INSTANCE
								.deleteButtonDisabledImage()));
			}
		}

		public T getSelectedDto() {
			return selectedDto;
		}

		public void setSelectedDto(T dto) {
			selectedDto = dto;
			int toSelect = indexOf(dto);
			setSelectedIndex(toSelect);
			onChange();
		}
		
		private void refresh(T dto) {
			int toRefresh = indexOf(dto);
			refresher.refrehs(items, dto, toRefresh);
			setItemText(indexOf(dto), labelProvider.getLabel(dto));
		}

		private void editItem(T dto) {
			refresh(dto);
			setItemText(indexOf(dto), labelProvider.getLabel(dto));
			onChange();
		}

		private void removeItem(T dto) {
			int toRemove = indexOf(dto);
			items.remove(toRemove);
			removeItem(toRemove);
			if (items.size() > 0) {
				setSelectedIndex(0);
			}
			
			onChange();
		}

		private void addNewItem(T dto) {
			items.add(dto);
			addItem(labelProvider.getLabel(dto));
			setSelectedIndex(items.size() - 1);
			onChange();
		}

		private int indexOf(T dto) {
			int indexOfItem = -1;
			for (int i = 0; i < items.size(); i++) {

				String id = idProvider.getId(items.get(i));
				if (id != null && id.equals(idProvider.getId(dto))) {
					indexOfItem = i;
					break;
				}
			}
			if (indexOfItem == -1) {
				throw new IllegalStateException(
						"Did not find the item which has been deleted");
			}
			return indexOfItem;
		}

	}

	public static interface LabelProvider<T> {
		String getLabel(T t);
	}

	public static interface IdProvider<T> {
		String getId(T t);
	}
	
	public static interface Refresher<T> {
		void refrehs(List<T> items, T newItem, int index);
	}
	
	public static interface OnChangeListener<T> {
		void onChanged(List<T> items, T selectedItem);
	}

}
