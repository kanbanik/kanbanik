package com.googlecode.kanbanik;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"test-context.xml"})
public class WorkflowItemsMoveIntegrationTest extends AbstractIntegrationTest {
	
	private static final String DATASET_XML = "/WorkflowItemsMoveIntegrationTest_dataset.xml";
	
	@Transactional
	@Test
	public void move_fromBeginningToMiddle() {
		Workflowitem item = leaf(1, "name1");
		item.setNextItem(leaf(3, "name3"));
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name2", "name1", "name3");
	}
	
	@Transactional
	@Test
	public void move_fromMiddleToBeginning() {
		Workflowitem item = leaf(2, "name2");
		item.setNextItem(leaf(1, "name1"));
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name2", "name1", "name3");
	}
	
	@Transactional
	@Test
	public void move_fromMiddleToEnd() {
		Workflowitem item = leaf(2, "name2");
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name1", "name3", "name2");
	}
	
	@Transactional
	@Test
	public void move_insideBigger() {
		Workflowitem item = leaf(8, "name8");
		item.setNextItem(leaf(6, "name6"));
		manager.find(Workflow.class, 2).getWorkflowitems().store(item, manager);
		check(2, "name4", "name5", "name8", "name6", "name7", "name9");
	}
	
	@Transactional
	@Test
	public void move_toSamePosition_beginning() {
		Workflowitem item = leaf(1, "name1");
		item.setNextItem(leaf(2, "name2"));
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name1", "name2", "name3");
	}
	
	@Transactional
	@Test
	public void move_toSamePosition_middle() {
		Workflowitem item = leaf(2, "name2");
		item.setNextItem(leaf(3, "name3"));
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name1", "name2", "name3");
	}
	
	@Transactional
	@Test
	public void move_toSamePosition_end() {
		Workflowitem item = leaf(3, "name3");
		item.setNextItem(null);
		manager.find(Workflow.class, 1).getWorkflowitems().store(item, manager);
		check(1, "name1", "name2", "name3");
	}
	
	private void check(int workflowId, String... names) {
		int i = 0;
		for (Workflowitem item : manager.find(Workflow.class, workflowId).getWorkflowitems()) {
			assertThat(item.getName(), equalTo(names[i]));
			i++;
		}
		
		assertThat(i, equalTo(names.length));
	}

	private Workflowitem leaf(int id, String name) {
		Workflowitem item = new Itemleaf();
		item.setId(id);
		item.setName(name);
		return item;
	}

	@Override
	protected String getDataset() {
		return DATASET_XML;
	}
}
