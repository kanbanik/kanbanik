package com.googlecode.kanbanik.server;


import org.apache.log4j.Logger;

import com.googlecode.kanbanik.client.services.KanbanikService;
import com.googlecode.kanbanik.shared.KanbanikDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public class KanbanikServiceImpl extends AbstractServiceServlet implements KanbanikService {

	private static final String WORKER = "kanbanikServiceWorker";

	private static final long serialVersionUID = -1910860713850928925L;

	private static final Logger logger = Logger.getLogger(KanbanikServiceImpl.class);	

	public KanbanikDTO loadKanbanikData() {
		KanbanikServiceWorker worker = getBean(WORKER);
		return worker.fillKanbanik();
	}

	public TaskDTO saveTask(TaskDTO task) {
		logger.info("going to save task: " + task.getId());
		KanbanikServiceWorker worker = getBean(WORKER);
		TaskDTO dto = worker.saveTask(task);
		logger.info("saved task: " + task.getId());
		return dto;
	}

	public void deleteTask(TaskDTO task) {
		logger.info("going to delete task: " + task.getId());
		KanbanikServiceWorker worker = getBean(WORKER);
		worker.deleteTask(task);
		logger.info("saved task: " + task.getId());
	}	
}
