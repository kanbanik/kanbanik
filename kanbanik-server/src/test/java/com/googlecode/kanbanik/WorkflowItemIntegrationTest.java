package com.googlecode.kanbanik;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.dbunit.dataset.DataSetException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"test-context.xml"})
public class WorkflowItemIntegrationTest extends AbstractIntegrationTest {

	private static final String DATASET_XML = "/WorkflowItemIntegrationTest_dataset.xml";

	@Transactional
	@Test(expected=IllegalArgumentException.class)
	public void update_notExisting() {
		updateId(1, 2312);
	}
	
	@Transactional
	@Test(expected=IllegalArgumentException.class)
	public void update_existing_notInCorrectWorkflow() {
		updateId(1, 3);
	}

	@Transactional
	@Test
	public void update_allOK() {
		updateId(1, 1);
		assertThat("changedName", equalTo(manager.find(Workflowitem.class, 1).getName()));
	}
	
	private void updateId(int workflowId, int itemId) {
		Workflowitems items = manager.find(Workflow.class, workflowId).getWorkflowitems();
		Workflowitem leaf = new Itemleaf();
		leaf.setId(itemId);
		leaf.setName("changedName");
		items.update(leaf, manager);
	}
	
	@Transactional
	@Test(expected=IllegalArgumentException.class)
	public void delete_notExisting() {
		Workflowitems items = manager.find(Workflow.class, 1).getWorkflowitems();
		Workflowitem leaf = new Itemleaf();
		leaf.setId(521);
		items.delete(leaf, manager);
	}
	
	@Transactional
	@Test
	public void delete_first() {
		runAndCheckDelete(3, "name2", "name3");
	}
	
	@Transactional
	@Test
	public void delete_middle() {
		runAndCheckDelete(4, "name1", "name3");
	}
	
	@Transactional
	@Test
	public void delete_last_composite() {
		Workflowitems items = manager.find(Workflow.class, 2).getWorkflowitems();
		Workflowitem leaf = new Itemcomposite();
		leaf.setId(5);
		ReturnObject ret = items.delete(leaf, manager);
		assertThat(ret.isOK(), is(true));
		assertItemsInOrder(2, "name1", "name2");
	}
	
	@Transactional
	@Test
	public void delete_containsTasks_shouldReturnFailMessage() {
		Workflowitems items = manager.find(Workflow.class, 3).getWorkflowitems();
		Workflowitem leaf = new Itemleaf();
		leaf.setId(6);
		ReturnObject ret = items.delete(leaf, manager);
		assertThat(ret.isOK(), is(false));
	}
	
	@Transactional
	@Test
	public void delete_composite_containsTasks_shouldReturnFailMessage() {
		Workflowitems items = manager.find(Workflow.class, 3).getWorkflowitems();
		Workflowitem composite = new Itemcomposite();
		composite.setId(7);
		ReturnObject ret = items.delete(composite, manager);
		assertThat(ret.isOK(), is(false));
	}

	private void runAndCheckDelete(int toDelete, String firstExpected, String secondExpected) {
		Workflowitems items = manager.find(Workflow.class, 2).getWorkflowitems();
		Workflowitem leaf = new Itemleaf();
		leaf.setId(toDelete);
		ReturnObject ret = items.delete(leaf, manager);
		assertThat(ret.isOK(), is(true));
		assertItemsInOrder(2, firstExpected, secondExpected);
	}
	
	@Transactional
	@Test
	public void getNext_exists() throws DataSetException, Exception {
		Itemleaf leaf = manager.find(Itemleaf.class, 1);
		assertThat(leaf.getName(), equalTo("name1"));
		assertThat(leaf.getNextItem().getName(), equalTo("name2"));
	}

	@Transactional
	@Test
	public void getNext_notExists() throws DataSetException, Exception {
		Itemleaf leaf = manager.find(Itemleaf.class, 2);
		assertThat(leaf.getNextItem(), is(nullValue()));
	}

	@Transactional
	@Test
	public void store_newItem_end() {
		Workflow workflow = manager.find(Workflow.class, 1);
		Workflowitems items = workflow.getWorkflowitems();
		
		Itemleaf leaf = new Itemleaf();
		// has no id set, so it is a new one. 
		// Has no next item set, so it is the last one
		leaf.setName("added");
		items.store(leaf, manager);
		assertItemsInOrder("name1", "name2", "added");
	}
	
	@Transactional
	@Test
	public void store_newItem_beginning() {
		Workflow workflow = manager.find(Workflow.class, 1);
		Workflowitems items = workflow.getWorkflowitems();
		
		Itemleaf leaf = new Itemleaf();
		leaf.setName("added");
		Workflowitem prevFirstItem = new Itemleaf();
		prevFirstItem.setId(items.iterator().next().getId());
		leaf.setNextItem(prevFirstItem);
		items.store(leaf, manager);
		assertItemsInOrder("added", "name1", "name2");
	}
	
	@Transactional
	@Test
	public void store_newItem_middle() {
		Workflow workflow = manager.find(Workflow.class, 1);
		Workflowitems items = workflow.getWorkflowitems();
		
		Itemleaf leaf = new Itemleaf();
		leaf.setName("added");
		Workflowitem secondItem = new Itemleaf();
		secondItem.setId(2);
		leaf.setNextItem(secondItem);
		items.store(leaf, manager);
		assertItemsInOrder("name1","added","name2");
	}
	
	private void assertItemsInOrder(int workflowId, String... expectedIdOrdered) {
		Workflow workflow = manager.find(Workflow.class, workflowId);
		
		int i = 0;
		for (Workflowitem item : workflow.getWorkflowitems()) {
			assertThat(item.getName(), equalTo(expectedIdOrdered[i]));
			i++;
		}
		
		assertThat(i, equalTo(expectedIdOrdered.length));
	}
	
	private void assertItemsInOrder(String... expectedIdOrdered) {
		assertItemsInOrder(1, expectedIdOrdered);
	}
	
	protected String getDataset() {
		return DATASET_XML;
	}
}

