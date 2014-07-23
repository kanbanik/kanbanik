package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.googlecode.kanbanik.client.api.Dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskFilter {

    private List<Dtos.ClassOfServiceDto> classesOfServices = new ArrayList<Dtos.ClassOfServiceDto>();

    private List<Dtos.UserDto> users = new ArrayList<Dtos.UserDto>();

    private String shortDescription = null;

    private String longDescription = null;

    private String id = null;

    private Date dateFrom;

    private Date dateTo;

    private int dateCondition;

    private static final int DATE_CONDITION_UNSET = 0;
    private static final int DATE_CONDITION_LESS = 1;
    private static final int DATE_CONDITION_EQALS = 2;
    private static final int DATE_CONDITION_MORE = 3;
    private static final int DATE_CONDITION_BETWEEN = 4;

    public boolean matches(Dtos.TaskDto task) {

        if (!stringMatches(id, task.getTicketId())) {
            return false;
        }

        if (!stringMatches(shortDescription, task.getName())) {
            return false;
        }

        if (!checkDueDates(task)) {
            return false;
        }

        if (!stringMatches(longDescription, new HTML(task.getDescription()).getText())) {
            return false;
        }

        boolean classOfServiceMatches = classesOfServices.isEmpty() || (task.getClassOfService() != null && findById(task.getClassOfService()) != -1);
        if (!classOfServiceMatches) {
            return false;
        }

        boolean userMatches = users.isEmpty() || (task.getAssignee() != null && findById(task.getAssignee()) != -1);

        return userMatches;
    }

    private boolean checkDueDates(Dtos.TaskDto task) {
        if (dateCondition == DATE_CONDITION_UNSET) {
            return true;
        }

        if (task.getDueDate() == null || "".equals(task.getDueDate())) {
            return false;
        }

        if (dateFrom == null) {
            return false;
        }

        if (dateCondition == DATE_CONDITION_BETWEEN && dateTo == null) {
            return false;
        }

        Date taskDueDate = null;
        try {
            taskDueDate = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).parse(task.getDueDate());
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (dateCondition == DATE_CONDITION_LESS) {
            return taskDueDate.before(dateFrom);
        }

        if (dateCondition == DATE_CONDITION_MORE) {
            return taskDueDate.after(dateFrom);
        }

        if (dateCondition == DATE_CONDITION_EQALS) {
            return taskDueDate.equals(dateFrom);
        }

        if (dateCondition == DATE_CONDITION_BETWEEN) {
            return taskDueDate.after(dateFrom) && taskDueDate.before(dateTo);
        }

        return true;

    }

    private boolean stringMatches(String pattern, String real) {
        boolean patternEmpty = pattern == null || "".equals(pattern);
        boolean realEmpty = real == null || "".equals(real);

        if (patternEmpty) {
            return true;
        }

        if (realEmpty) {
            return false;
        }

        return real.contains(pattern);
    }

    public void add(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = findById(classOfServiceDto);
        if (id == -1) {
            classesOfServices.add(classOfServiceDto);
        }
    }

    public void remove(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = findById(classOfServiceDto);
        if (id != -1) {
            classesOfServices.remove(id);
        }
    }

    public void add(Dtos.UserDto userDto) {
        int id = findById(userDto);
        if (id == -1) {
            users.add(userDto);
        }
    }

    public void remove(Dtos.UserDto userDto) {
        int id = findById(userDto);
        if (id != -1) {
            users.remove(id);
        }
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setId(String id) {
        this.id = id;
    }

    private int findById(Dtos.UserDto userDto) {
        int id = 0;

        for (Dtos.UserDto candidate : users) {
            if (candidate.getUserName().equals(userDto.getUserName())) {
                return id;
            }

            id ++;
        }

        return -1;
    }

    private int findById(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = 0;

        for (Dtos.ClassOfServiceDto candidate : classesOfServices) {
            if (candidate.getId().equals(classOfServiceDto.getId())) {
                return id;
            }

            id ++;
        }

        return -1;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateCondition(int dateCondition) {
        this.dateCondition = dateCondition;
    }
}
