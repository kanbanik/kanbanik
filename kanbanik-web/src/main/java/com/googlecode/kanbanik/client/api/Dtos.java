package com.googlecode.kanbanik.client.api;

import java.util.List;

public class Dtos {

    public static interface BaseDto {
        String getCommandName();
        void setCommandName(String commandName);

        String getSessionId();
        void setSessionId(String sessionId);
    }

    public static interface LoginDto extends BaseDto {
        String getUserName();
        void setUserName(String userName);

        String getPassword();
        void setPassword(String password);
    }

    public static interface SessionDto extends BaseDto {
    }

    public static interface StatusDto {
        Boolean isSuccess();
        void setSuccess(Boolean success);

        void setReason(String reason);
        String getReason();
    }

    public static interface UserDto extends BaseDto {

        void setUserName(String userName);
        String getUserName();

        void setRealName(String realName);
        String getRealName();

        int getVersion();
        public void setVersion(int version);

        public String getPictureUrl();
        public void setPictureUrl(String pictureUrl);

        public String getSessionId();
        public void setSessionId(String sessionId);
    }

    public static interface UserManipulationDto extends UserDto, BaseDto {
        String getPassword();
        void setPassword(String password);

        String getNewPassword();
        void setNewPassword(String newPassword);
    }

    public static interface ClassOfServiceDto extends BaseDto {
        String getId();
        void setId(String id);

        String getName();
        void setName(String name);

        String getDescription();
        void setDescription(String description);

        String getColour();
        void setColour(String colour);

        int getVersion();
        void setVersion(int version);
    }

    public static interface ClassOfServicesDto {
        List<ClassOfServiceDto> getResult();
        void setResult(List<ClassOfServiceDto> result);
    }

    public static interface ProjectDto extends BaseDto {
        String getId();
        void setId(String id);

        String getName();
        void setName(String name);

        int getVersion();
        void setVersion(int version);

        List<String> getBoardIds();
        void setBoardIds(List<String> boardIds);
    }

    public static interface ProjectsDto {
        List<ProjectDto> getResult();
        void setResult(List<ProjectDto> result);
    }

    public static interface ProjectWithBoardDto extends BaseDto {
        void setProject(ProjectDto project);
        ProjectDto getProject();

        void setBoardId(String boardId);
        String getBoardId();
    }

    public static interface ErrorDto {
        String getErrorMessage();
        void setErrorMessage(String errorMessage);
    }

    public static interface UsersDto {
        List<UserDto> getResult();
        void setResult(List<UserDto> result);
    }

    public static interface TaskDto extends BaseDto {
        String getId();
        void setId(String id);

        String getName();
        void setName(String name);

        String getDescription();
        void setDescription(String description);

        ClassOfServiceDto getClassOfService();
        void setClassOfService(ClassOfServiceDto classOfService);

        String getTicketId();
        void setTicketId(String ticketId);

        String getWorkflowitemId();
        void setWorkflowitemId(String workflowitemId);

        int getVersion();
        void setVersion(int version);

        String getProjectId();
        void setProjectId(String projectId);

        UserDto getAssignee();
        void setAssignee(UserDto assignee);

        String getOrder();
        void setOrder(String order);

        String getDueDate();
        void setDueDate(String dueDate);

        String getBoardId();
        void setBoardId(String boardId);
    }

    public static interface TasksDto extends BaseDto {
        List<TaskDto> getValues();
        void setValues(List<TaskDto> result);
    }

    public static interface MoveTaskDto extends BaseDto {
        TaskDto getTask();
        void setTask(TaskDto task);

        String getPrevOrder();
        void setPrevOrder(String prevOrder);

        String getNextOrder();
        void setNextOrder(String nextOrder);
    }

    public static interface EmptyDto {

    }

}
