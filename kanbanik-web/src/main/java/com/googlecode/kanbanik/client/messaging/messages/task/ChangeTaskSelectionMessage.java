package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage.ChangeTaskSelectionParams;

import java.util.Arrays;
import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class ChangeTaskSelectionMessage extends BaseMessage<ChangeTaskSelectionParams> {

	public ChangeTaskSelectionMessage(ChangeTaskSelectionParams payload, Object source) {
		super(payload, source);
	}

    public static ChangeTaskSelectionMessage selectAll(Object source) {
        ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(true, true, false, null);
        return new ChangeTaskSelectionMessage(params, source);
    }

	public static ChangeTaskSelectionMessage deselectAll(Object source) {
		ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(false, true, false, null);
		return new ChangeTaskSelectionMessage(params, source);
	}
	
	public static ChangeTaskSelectionMessage selectOne(TaskDto task, Object source) {
		ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(true, false, false, Arrays.asList(task));
		return new ChangeTaskSelectionMessage(params, source);
	}

    public static ChangeTaskSelectionMessage selectList(List<TaskDto> tasks, Object source) {
        ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(true, false, false, tasks);
        return new ChangeTaskSelectionMessage(params, source);
    }

	public static class ChangeTaskSelectionParams {
		
		private final boolean select;
		
		private final boolean all;
		
		private final boolean applyToYourself;
		
		private final List<TaskDto> tasks;

		public ChangeTaskSelectionParams(final boolean select, final boolean all, final boolean applyToYourself, final List<TaskDto> tasks) {
			super();
			this.select = select;
			this.all = all;
			this.tasks = tasks;
			this.applyToYourself = applyToYourself;
		}

		public boolean isSelect() {
			return select;
		}

		public boolean isAll() {
			return all;
		}

		public List<TaskDto> getTasks() {
			return tasks;
		}
		
		public boolean isApplyToYourself() {
			return applyToYourself;
		}
		
	}
	
}
