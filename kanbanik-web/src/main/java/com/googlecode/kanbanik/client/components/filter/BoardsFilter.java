package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTML;
import com.googlecode.kanbanik.client.api.Dtos;

import java.util.Date;
import java.util.List;

public class BoardsFilter {

    private static final int DATE_CONDITION_UNSET = 0;
    private static final int DATE_CONDITION_LESS = 1;
    private static final int DATE_CONDITION_EQALS = 2;
    private static final int DATE_CONDITION_MORE = 3;
    private static final int DATE_CONDITION_BETWEEN = 4;

    private Dtos.FilterDataDto filterDataDto;

    public boolean matches(Dtos.TaskDto task) {

        List<Dtos.FilteredEntity> filteredEntities = filterDataDto.getFullTextFilter().getFilteredEntities();

        if (filteredEntities != null && filteredEntities.size() != 0) {
            boolean matches = false;

            if (filteredEntities.contains(Dtos.FilteredEntity.SHORT_DESCRIPTION)) {
                if (stringMatches(filterDataDto.getFullTextFilter(), task.getName())) {
                    matches = true;
                }

                if (!matches && filteredEntities.contains(Dtos.FilteredEntity.LONG_DESCRIPTION)) {
                    if (stringMatches(filterDataDto.getFullTextFilter(), new HTML(task.getDescription()).getText())) {
                        matches = true;
                    }
                }

                if (!matches && filteredEntities.contains(Dtos.FilteredEntity.TICKET_ID)) {
                    if (stringMatches(filterDataDto.getFullTextFilter(), task.getTicketId())) {
                        matches = true;
                    }
                }

                if (!matches) {
                    return false;
                }
            }
        }

        if (!checkDueDates(task)) {
            return false;
        }

        boolean classOfServiceMatches = filterDataDto.getClassesOfServices().isEmpty() || (task.getClassOfService() != null && findById(task.getClassOfService()) != -1);
        if (!classOfServiceMatches) {
            return false;
        }

        boolean userMatches = filterDataDto.getUsers().isEmpty() || (task.getAssignee() != null && findById(task.getAssignee()) != -1);

        return userMatches;
    }

    private Date parseDate(String date) {
        if (date == null || "".equals(date)) {
            return null;
        }

        try {
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).parse(date);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean checkDueDates(Dtos.TaskDto task) {
        if (filterDataDto.getDueDate() == null) {
            return true;
        }

        int dateCondition = filterDataDto.getDueDate().getCondition();

        if (dateCondition == DATE_CONDITION_UNSET) {
            return true;
        }

        if (task.getDueDate() == null || "".equals(task.getDueDate())) {
            return false;
        }

        Date dateFrom = parseDate(filterDataDto.getDueDate().getDateFrom());
        if (dateFrom == null) {
            return false;
        }

        Date dateTo = parseDate(filterDataDto.getDueDate().getDateTo());
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

    private boolean stringMatches(Dtos.FullTextMatcherDataDto pattern, String real) {
        boolean patternEmpty = pattern == null || pattern.getString() == null;
        boolean realEmpty = real == null || "".equals(real);

        if (patternEmpty) {
            return true;
        }

        if (realEmpty) {
            return false;
        }

        String actual = real;
        String expected = pattern.getString();

        boolean matches = false;

        if (pattern.isRegex()) {
            try {
                RegExp regExp = RegExp.compile(expected);
                matches = regExp.exec(actual) != null;
            } catch (Exception e) {
                // incorrect regex - not matches
                matches = false;
            }
        } else {
            if (!pattern.isCaseSensitive()) {
                actual = actual.toLowerCase();
                expected = expected.toLowerCase();
            }
            matches = actual.contains(expected);
        }

        return pattern.isInverse() ? !matches : matches;
    }

    public void add(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = findById(classOfServiceDto);
        if (id == -1) {
            filterDataDto.getClassesOfServices().add(classOfServiceDto);
        }
    }

    public void remove(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = findById(classOfServiceDto);
        if (id != -1) {
            filterDataDto.getClassesOfServices().remove(id);
        }
    }

    public void add(Dtos.UserDto userDto) {
        int id = findById(userDto);
        if (id == -1) {
            filterDataDto.getUsers().add(userDto);
        }
    }

    public void remove(Dtos.UserDto userDto) {
        int id = findById(userDto);
        if (id != -1) {
            filterDataDto.getUsers().remove(id);
        }
    }

    private int findById(Dtos.UserDto userDto) {
        int id = 0;

        for (Dtos.UserDto candidate : filterDataDto.getUsers()) {
            if (candidate.getUserName().equals(userDto.getUserName())) {
                return id;
            }

            id ++;
        }

        return -1;
    }

    private int findById(Dtos.ClassOfServiceDto classOfServiceDto) {
        int id = 0;

        for (Dtos.ClassOfServiceDto candidate : filterDataDto.getClassesOfServices()) {
            if (candidate.getId().equals(classOfServiceDto.getId())) {
                return id;
            }

            id ++;
        }

        return -1;
    }


    public Dtos.FilterDataDto getFilterDataDto() {
        return filterDataDto;
    }

    public void setFilterDataDto(Dtos.FilterDataDto filterDataDto) {
        this.filterDataDto = filterDataDto;
    }
}
