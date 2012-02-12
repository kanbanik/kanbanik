package com.googlecode.kanbanik;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"test-context.xml"})
public class ModelIntegrationTest extends AbstractIntegrationTest {

	private static final String DATASET_XML = "/ModelIntegrationTest_dataset.xml";
	
	@Transactional
	@Test
	public void workflowItemForTask_oneTaskOnOneWorkflowItem() {
		checkTaskOnWorkflow(1, 1);
	}
	
	@Transactional
	@Test
	public void workflowItemForTask_twoTasksOnOneWorkflowItem() {
		checkTaskOnWorkflow(2, 4);
		checkTaskOnWorkflow(3, 4);
	}
	
	private void checkTaskOnWorkflow(int taskId, int workflowId) {
		Task task = manager.find(Task.class, 3);
		assertThat(task.getWorkflowitem().getId(), is(4));
	}
	
	@Transactional
	@Test
	public void tasksForProject_oneTaskPerProject() {
		Project project = manager.find(Project.class, 1);
		assertThat(project.getTasks().size(), is(1));
	}
	
	@Transactional
	@Test
	public void tasksForProject_twoTasksPerProject() {
		Project project = manager.find(Project.class, 2);
		assertThat(project.getTasks().size(), is(2));
	}
	
	@Transactional
	@Test
	public void getBoards() throws Exception {
		List<String> boardNames = new ArrayList<String>();
		for (Board board : kanbanik.getBoards().all()) {
			boardNames.add(board.getName());
		}
		
		assertThat(boardNames, containsInAnyOrder("first board", "second board"));
	}
	
	@Transactional
	@Test
	public void allProjects() {
		List<String> projectNames = new ArrayList<String>();
		for (Project project : kanbanik.getProjects().all()) {
			projectNames.add(project.getName());
		}
		
		assertThat(projectNames, containsInAnyOrder("Project 1", "Project 2"));
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Test
	public void boardsForProject() {
		Map<String, String> projectToBoard = new HashMap<String, String>(); 
		for (Project project : kanbanik.getProjects().all()) {
			projectToBoard.put(project.getName(), project.getBoard().iterator().next().getName());
		}
		
		assertThat(projectToBoard, allOf(
				hasEntry("Project 1", "first board"),
				hasEntry("Project 2", "first board")
		));
	}
	
	@Transactional
	@Test
	public void projectsForBoard_noProject() {
		runAndCheckProjectsForBoard(2);
	}
	
	@Transactional
	@Test
	public void projectsForBoard_twoProjects() {
		runAndCheckProjectsForBoard(1, "Project 1", "Project 2");
	}
	
	private void runAndCheckProjectsForBoard(int boardId, String... expectedProjectNames) {
		List<String> projectNames = new ArrayList<String>();
		for (Project project : manager.find(Board.class, boardId).getProjects().all()) {
			projectNames.add(project.getName());
		}
		assertThat(projectNames, containsInAnyOrder(expectedProjectNames));
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Test
	public void workflowForBoard() {
		Map<String, Integer> boardToWorkflow = new HashMap<String, Integer>();
		for (Board board : kanbanik.getBoards().all()) {
			boardToWorkflow.put(board.getName(), board.getWorkflow().getId());
		}
		
		assertThat(boardToWorkflow, allOf(
				hasEntry("first board", 1),
				hasEntry("second board", 2)
		));
	}

	@Transactional
	@Test
	public void workflowItemForWorkflow() {
		checkWorkflowItems(manager.find(Workflow.class, 1), 1,2);
		checkWorkflowItems(manager.find(Workflow.class, 2), 3,4,5);
	}
	
	private void checkWorkflowItems(Workflow workflow, Integer... expectedItems) {
		List<Integer> items = new ArrayList<Integer>();
		for (Workflowitem item : workflow.getWorkflowitems()) {
			items.add(item.getId());
		}
		assertThat(items, containsInAnyOrder(expectedItems));
	}
	
	@Transactional
	@Test
	public void leafsFromComposite() throws SQLException {
		 Collection<Itemleaf> leafs = manager.find(Itemcomposite.class, 3).getLeafs();
		 List<Integer> leafIds = new ArrayList<Integer>();
		 for (Itemleaf leaf : leafs) {
			 leafIds.add(leaf.getId());
		 }
		 assertThat(leafIds, containsInAnyOrder(4, 5));
	}
	
	@Transactional
	@Test
	public void itemleaf_isSubitem_yes() {
		assertThat(manager.find(Itemleaf.class, 4).isSubitem(), is(true));
	}
	
	@Transactional
	@Test
	public void itemleaf_isSubitem_no() {
		assertThat(manager.find(Itemleaf.class, 1).isSubitem(), is(false));
	}

	@Override
	protected String getDataset() {
		return DATASET_XML;
	}
}
