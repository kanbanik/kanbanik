package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage.ChangeTaskSelectionParams;
import com.googlecode.kanbanik.dto.TaskDto;

public class ChangeTaskSelectionMessage extends BaseMessage<ChangeTaskSelectionParams> {

	public ChangeTaskSelectionMessage(ChangeTaskSelectionParams payload, Object source) {
		super(payload, source);
	}
	
	public static ChangeTaskSelectionMessage deselectAll(Object source) {
		ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(false, true, false, null);
		return new ChangeTaskSelectionMessage(params, source);
	}
	
	public static ChangeTaskSelectionMessage selectOne(TaskDto task, Object source) {
		ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(true, false, false, task);
		return new ChangeTaskSelectionMessage(params, source);
	}

	public static class ChangeTaskSelectionParams {
		
		private final boolean select;
		
		private final boolean all;
		
		private final boolean applyToYourself;
		
		private final TaskDto task;

		public ChangeTaskSelectionParams(final boolean select, final boolean all, final boolean applyToYourself, final TaskDto task) {
			super();
			this.select = select;
			this.all = all;
			this.task = task;
			this.applyToYourself = applyToYourself;
		}

		public boolean isSelect() {
			return select;
		}

		public boolean isAll() {
			return all;
		}

		public TaskDto getTask() {
			return task;
		}
		
		public boolean isApplyToYourself() {
			return applyToYourself;
		}
		
	}
	
}
