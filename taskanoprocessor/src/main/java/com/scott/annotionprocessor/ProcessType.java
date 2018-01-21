package com.scott.annotionprocessor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 15:49</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public enum ProcessType {
    TYPE_ADD_TASK,
    TYPE_ADD_TASKS,
    TYPE_DELETE_TASK,
    TYPE_DELETE_TASKS_GROUP,
    TYPE_DELETE_TASKS_SOME,
    TYPE_DELETE_TASKS_ALL,
    TYPE_DELETE_TASKS_COMPLETED,
    TYPE_DELETE_TASKS_STATE,
    TYPE_QUERY_TASK,
    TYPE_QUERY_TASKS_SOME,
    TYPE_QUERY_TASKS_ALL,
    TYPE_QUERY_TASKS_GROUP,
    TYPE_QUERY_TASKS_STATE,
    TYPE_QUERY_TASKS_COMPLETED,
    TYPE_DEFAULT,
    TYPE_UPDATE_TASK,
    TYPE_UPDATE_TASK_WTIHOUT_SAVE,
    TYPE_START_TASK,
    TYPE_START_GROUP,
    TYPE_START_ALL,
    TYPE_STOP_TASK,
    TYPE_STOP_GROUP,
    TYPE_STOP_ALL
}
