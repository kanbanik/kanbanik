package com.googlecode.kanbanik.dto;

public class WorkflowitemDto implements IdentifiableDto {

	private static final long serialVersionUID = 1343045359919670502L;

	private String name;

	private String id;

	private int wipLimit;

	private ItemType itemType;

	private WorkflowitemDto child;

	private WorkflowitemDto nextItem;
	
	private BoardDto board;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getWipLimit() {
		return wipLimit;
	}

	public void setWipLimit(int wipLimit) {
		this.wipLimit = wipLimit;
	}

	public WorkflowitemDto getChild() {
		return child;
	}

	public void setChild(WorkflowitemDto child) {
		this.child = child;
	}

	public WorkflowitemDto getNextItem() {
		return nextItem;
	}

	public void setNextItem(WorkflowitemDto nextItem) {
		this.nextItem = nextItem;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public BoardDto getBoard() {
		return board;
	}

	public void setBoard(BoardDto board) {
		this.board = board;
	}
}
