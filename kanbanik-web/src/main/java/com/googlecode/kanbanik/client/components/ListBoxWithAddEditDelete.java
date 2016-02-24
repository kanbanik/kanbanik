package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

	public ListBoxWithAddEditDelete(
            String label,
            IdProvider<T> idProvider,
			LabelProvider<T> labelProvider,
            Component<T> creatingComponent,
			Component<T> editingComponent,
            Component<T> deletingComponent,
			Refresher<T> refresher) {

		this.idProvider = idProvider;
		this.labelProvider = labelProvider;
		this.creatingComponent = creatingComponent;
		this.editingComponent = editingComponent;
		this.deletingComponent = deletingComponent;
		this.refresher = refresher;
		
		listBox = new ListBoxWithAddEditDeleteListBox(this);
		
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

    public List<T> getContent() {
        return listBox.getContent();
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

        private ListBoxWithAddEditDelete<T> parent;

		public ListBoxWithAddEditDeleteListBox(ListBoxWithAddEditDelete<T> parent) {
            this.parent = parent;
            addChangeHandler(this);
		}

		public void setContent(List<T> content) {
			if (content == null || content.size() == 0) {
				clear();
				this.items = new ArrayList<>();
				resetButtonAvailability();
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

        public List<T> getContent() {
            return items;
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

        private void sortItems() {
            Comparator<T> comparator = new Comparator<T>() {
                @Override
                public int compare(T t, T t2) {
                    return labelProvider.getLabel(t).compareTo(labelProvider.getLabel(t2));
                }
            };

            Collections.sort(items, comparator);
            while (getItemCount() > 0) {
                removeItem(0);
            }

            for (T dto : items) {
                addItem(labelProvider.getLabel(dto));
            }

        }

		private void resetButtonAvailability() {
			boolean enabled = parent.isEnabled(selectedDto);

			editButton.setEnabled(parent.isEditEnabled(selectedDto));
			deleteButton.setEnabled(parent.isDeleteEnabled(selectedDto));

            if (parent.isEditEnabled(selectedDto)) {
                editButton.getUpFace()
                        .setImage(
                                new Image(KanbanikResources.INSTANCE
                                        .editButtonImage()));
            } else {
                editButton.getUpFace().setImage(
                        new Image(KanbanikResources.INSTANCE
                                .editButtonDisabledImage()));
            }

			if (parent.isDeleteEnabled(selectedDto)) {
				deleteButton.getUpFace().setImage(
                        new Image(KanbanikResources.INSTANCE
                                .deleteButtonImage()));
			} else {
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
            sortItems();
            setSelectedIndex(indexOf(dto));
			onChange();
		}

		private void removeItem(T dto) {
			int toRemove = indexOf(dto);
			items.remove(toRemove);
			removeItem(toRemove);
            sortItems();
			if (items.size() > 0) {
				setSelectedIndex(0);
			}
			
			onChange();
		}

		private void addNewItem(T dto) {
			items.add(dto);
			addItem(labelProvider.getLabel(dto));
            sortItems();
			setSelectedIndex(indexOf(dto));
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

    protected boolean isDeleteEnabled(T selectedDto) {
        return isEnabled(selectedDto);
    }

    protected boolean isEditEnabled(T selectedDto) {
        return isEnabled(selectedDto);
    }

    protected boolean isEnabled(T selectedDto) {
        return selectedDto != null;
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
