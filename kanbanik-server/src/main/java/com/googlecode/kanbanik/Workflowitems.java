package com.googlecode.kanbanik;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;


public class Workflowitems implements Iterable<Workflowitem> {
	
	private static final String WORKFLOWITEM_CONTAINS_TASKS = "The workflowitem \"{0}\" contains tasks \"{1}\" so it can not be deleted!";
	private Workflow owningWorkflow;

	public Workflowitems(Workflow owningWorkflow) {
		this.owningWorkflow = owningWorkflow;
	}

	public Workflowitem store(Workflowitem item, EntityManager manager) {
		if (item == null) {
			throw new IllegalArgumentException("The given item is null!");
		}
		
		if(isNewItem(item, manager)) {
			return add(item, manager);
		} else {
			Workflowitem storedItem = move(item, manager);
			setupStoredItem(item, storedItem);
			return storedItem;
		}
	}

	private void setupStoredItem(Workflowitem item, Workflowitem storedItem) {
		storedItem.setName(item.getName());
		storedItem.setWipLimit(item.getWipLimit());
	}

	public void update(Workflowitem item, EntityManager manager) {
		checkItem(item, manager);
		
		manager.find(Workflowitem.class, item.getId()).setName(item.getName());
	}

	public ReturnObject delete(Workflowitem item, EntityManager manager) {
		checkItem(item, manager);
		
		Workflowitem realItem = manager.find(Workflowitem.class, item.getId());
		if (realItem == null) {
			throw new IllegalArgumentException("It is not possible to delete this item because it does not exist!");
		}
		
		ReturnObject returnObject = checkIfITemHasTasks(realItem);
		if (!returnObject.isOK()) {
			return returnObject;
		}
		
		moveToEnd(item, manager);
		Workflowitem prevItem = findPrevItem(item, manager);
		if (prevItem != null) {
			prevItem.setNextItem(null);
		}
		owningWorkflow.remove(manager, realItem);
		manager.remove(realItem);
		return new ReturnObject(true, "");
	}

	private ReturnObject checkIfITemHasTasks(Workflowitem realItem) {
		if (realItem instanceof Itemleaf) {
			if (hasTasks(realItem)) {
				return createFalseReturnObject(realItem, realItem);
			}
		} else if (realItem instanceof Itemcomposite) {
			for (Workflowitem item : ((Itemcomposite) realItem).getLeafs()) {
				if (hasTasks(item)) {
					return createFalseReturnObject(realItem, item);
				}
			}
		}
		
		return new ReturnObject(true, "");
	}

	private boolean hasTasks(Workflowitem item) {
		return item.getTasks() != null && item.getTasks().size() > 0;
	}

	private ReturnObject createFalseReturnObject(Workflowitem realItem, Workflowitem taskContainer) {
		String workflowitemName = realItem.getName();
		StringBuilder containingTasksBuilder = new StringBuilder();
		containingTasksBuilder.append("[");
		
		int i = 0;
		int size = taskContainer.getTasks().size();
		for (Task task : taskContainer.getTasks()) {
			containingTasksBuilder.append(task.getTicketId());
			if (i != size - 1) { 
				containingTasksBuilder.append(", ");
			}
			i++;
		}
	
		containingTasksBuilder.append("]");
		
		String msg = MessageFormat.format(WORKFLOWITEM_CONTAINS_TASKS, workflowitemName, containingTasksBuilder.toString());
		
		return new ReturnObject(false, msg);
	}
	
	private void moveToEnd(Workflowitem item, EntityManager manager) {
		item.setNextItem(null);
		move(item, manager);
	}
	
	private Workflowitem move(Workflowitem item, EntityManager manager) {
		// a->b->c->d->e->f
		// the e moves before b
		// so, the result:
		// a->e->b->c->d->f
		// that's where the naming come from
		Workflowitem last = manager.find(Workflowitem.class, last().getId());
		Workflowitem e = manager.find(Workflowitem.class, item.getId());
		
		if (!movesToDifferentPosition(e, item)) {
			return e;
		}
		
		Workflowitem f = e.getNextItem();
		Workflowitem d = findPrevItem(e, manager);
		
		Workflowitem b = null;
		if (item.getNextItem() != null) {
			b = manager.find(Workflowitem.class, item.getNextItem().getId());
		}
		
		Workflowitem a = findPrevItem(b, manager);
	
		if (a != null) {
			a.setNextItem(e);
		}
		
		e.setNextItem(b);
		
		if (d != null) {
			d.setNextItem(f);			
		}
		
		if (b == null && last.getId() != e.getId()) {
			last.setNextItem(e);
		}
		
		manager.refresh(owningWorkflow);
		return e;
	}

	private boolean movesToDifferentPosition(Workflowitem e, Workflowitem item) {
		if (e.getNextItem() != null && item.getNextItem() != null) {
			if (e.getNextItem().getId() == item.getNextItem().getId()) {
				return false;
			}
		}
		
		if (e.getNextItem() == null && item.getNextItem() == null) {
			return false;
		}
		
		return true;
	}

	private Workflowitem add(Workflowitem item, EntityManager manager) {
		if (!isLast(item)) {
			return addToNotEnd(item, manager);
		} else {
			return addToEnd(item, manager);
		}
	}

	private Workflowitem addToNotEnd(Workflowitem item, EntityManager manager) {
		Workflowitem nextItem = item.getNextItem();
		if (!exists(nextItem, manager)) {
			String nextId = nextItem != null ? "" + nextItem.getId() : "The whole item is null";
			throw new IllegalArgumentException("The item set as the next item of the item with id='"+item.getId()+"' does not exist! The not existing item's id is: '"+nextId+"'" );
		}
		
		
		nextItem = manager.find(Workflowitem.class, nextItem.getId());
		item.setNextItem(nextItem);
		Workflowitem prevPrevItem = findPrevItem(nextItem, manager);
		manager.persist(item);
		
		if (prevPrevItem != null) {
			prevPrevItem = manager.find(Workflowitem.class, prevPrevItem.getId());
			prevPrevItem.setNextItem(item);
		}
		
		owningWorkflow.insertItem(manager, item);
		return item;
		
	}

	private Workflowitem findPrevItem(Workflowitem nextItem, EntityManager manager) {
		if (nextItem == null) {
			return null;
		}
		int nextItemId = nextItem.getId();
		for (Workflowitem prevPrevItem : this) {
			if (prevPrevItem.getNextItem() == null) {
				continue;
			}
			if (prevPrevItem.getNextItem().getId() == nextItemId) {
				return manager.find(Workflowitem.class, prevPrevItem.getId());
			}
		}
		
		return null;
	}

	private boolean exists(Workflowitem nextItem, EntityManager manager) {
		if (nextItem == null || manager.find(Workflowitem.class, nextItem.getId()) == null) {
			return false;
		}
		
		return true;
	}

	private Workflowitem addToEnd(Workflowitem item, EntityManager manager) {
		manager.persist(item);
		if (last() != null) {
			Workflowitem prevLastItem = manager.find(Workflowitem.class, last().getId());
			prevLastItem.setNextItem(item);
		}
		owningWorkflow.insertItem(manager, item);
		return item;
	}

	private boolean isLast(Workflowitem item) {
		return item.getNextItem() == null;
	}

	private boolean isNewItem(Workflowitem item, EntityManager manager) {
		return manager.find(Workflowitem.class, item.getId()) == null;
	}
	
	public Workflowitem last() {
		for (Workflowitem item : owningWorkflow.itemsAsList()) {
			if (isLast(item)) {
				return item;
			}
		}
		return null;
	}

	public Workflowitem first() {
		Collection<Workflowitem> workflowitems = owningWorkflow.itemsAsList();
		if (workflowitems.size() == 0) {
			return null;
		}
		
		List<Workflowitem> copy = new ArrayList<Workflowitem>(workflowitems.size());
		for (Workflowitem item : workflowitems) {
			copy.add(item);
		}
		for (Workflowitem item : workflowitems) {
			copy.remove(item.getNextItem());
		}
		
		if (copy.size() == 0) {
			throw new IllegalStateException("There is no first item in the workflow");
		}
		
		if (copy.size() > 1) {
			throw new IllegalStateException("There is more first items in the workflow");
		}
		
		return copy.iterator().next();
	}
	
	public Iterator<Workflowitem> iterator() {
		return new WorkflowItemsSortingIterator(first());
	}

	class WorkflowItemsSortingIterator implements Iterator<Workflowitem> {
		
		private Workflowitem currentItem;
		
		public WorkflowItemsSortingIterator(Workflowitem firstItem) {
			super();
			this.currentItem = firstItem;
		}

		public boolean hasNext() {
			if (currentItem == null) {
				return false;
			}
			
			return true;
		}

		public Workflowitem next() {
			Workflowitem toReturn = currentItem;
			currentItem = currentItem.getNextItem();
			return toReturn;
		}

		public void remove() {
			throw new UnsupportedOperationException("The remove is not supported");
		}
	}

	private void checkItem(Workflowitem item, EntityManager manager) {
		if (item == null) {
			throw new IllegalArgumentException("The given item is null!");
		}
		
		if (manager.find(Workflowitem.class, item.getId()) == null) {
			throw new IllegalArgumentException("The item with id: '" + item.getId() + "' does not exist, so can not be touched in this context!");
		}
		
		if (!owningWorkflow.containsItem(item)) {
			throw new IllegalArgumentException("The item with id: '" + item.getId() + "' is not in the workflow with id: '" + owningWorkflow.getId() + "' so can not be touched in this context!");
		}
	}
	
}
