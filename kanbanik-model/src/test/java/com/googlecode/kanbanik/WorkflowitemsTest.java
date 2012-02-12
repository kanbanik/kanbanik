package com.googlecode.kanbanik;

import java.util.ArrayList;
import java.util.Collection;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import org.junit.Test;
import static org.junit.Assert.fail;

public class WorkflowitemsTest {
	
	@Test
	public void last_emptyCollection() {
		Workflowitems items = new Workflowitems(new TestingWorkflow(null));
		assertThat(items.last(), is(nullValue()));
	}
	
	@Test
	public void last_oneItem() {
		assertThat(createItems(1).last().getId(), equalTo(1));
	}
	
	@Test
	public void last_twoItems() {
		assertThat(createItems(1,2,3,4,5,6,7,8,9).last().getId(), equalTo(9));
	}
	
	@Test
	public void first_moreItems() {
		assertThat(createItems(1,2,3,4,5,6,7,8,9).first().getId(), equalTo(1));
	}
	
	@Test
	public void first_oneItem() {
		assertThat(createItems(1).first().getId(), equalTo(1));
	}
	
	@Test
	public void first_noItem() {
		assertThat(new Workflowitems(new TestingWorkflow(null)).first(), is(nullValue()));
	}
	
	@Test
	@SuppressWarnings("unused")
	public void iteration_noItem() {
		for (Workflowitem item : new Workflowitems(new TestingWorkflow(null))) {
			fail("There are no workflowitems, so the cycle should not been entered");
		}
	}
	
	@Test
	public void iteration_oneItem() {
		checkIteration(createItems(1), 1);
	}
	
	@Test
	public void iteration_twoItems() {
		checkIteration(createItems(1,2), 1,2);
	}
	
	@Test
	public void iteration_moreItems() {
		checkIteration(createItems(1,2,3,4,5,6,7,8,9), 1,2,3,4,5,6,7,8,9);
	}
	
	private void checkIteration(Workflowitems items, int... ids) {
		int i = 0;
		
		
		for (Workflowitem item : items) {
			assertThat(ids[i], equalTo(item.getId()));
			i++;
		}
		
		assertThat(ids.length, equalTo(i));
	}
	
	private Workflowitems createItems(int... ids) {
		
		Collection<Workflowitem> items = new ArrayList<Workflowitem>();
		
		Itemleaf prevLeaf = null;
		
		for (int id : ids) {
			Itemleaf leaf = new Itemleaf();
			leaf.setId(id);
			if (prevLeaf != null) {
				prevLeaf.setNextItem(leaf);
			}
			prevLeaf = leaf;
			items.add(leaf);
		}
		
		Workflowitems workflowitems = new Workflowitems(new TestingWorkflow(items));
		return workflowitems;
	}
	
	class TestingWorkflow extends Workflow {
		
		private static final long serialVersionUID = -1901518440068048014L;

		private Collection<Workflowitem> items;
		
		public TestingWorkflow(Collection<Workflowitem> items) {
			super();
			this.items = items;
		}

		@Override
		Collection<Workflowitem> itemsAsList() {
			if (items == null) {
				return new ArrayList<Workflowitem>();
			}
			return items;
		}
	}
}
