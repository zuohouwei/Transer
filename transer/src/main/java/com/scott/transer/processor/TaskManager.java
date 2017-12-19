package com.scott.transer.processor;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.task.ITaskHandler;
import com.scott.transer.task.ITaskHandlerHolder;
import com.scott.transer.task.ITaskHandlerListenner;
import com.scott.transer.task.ITaskHolder;
import com.scott.transer.task.TaskState;
import com.scott.annotionprocessor.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 16:13</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskManager implements ITaskManager , ITaskHandlerListenner{

    private ITaskProcessor mProcessorProxy;
    private ITaskProcessCallback mCallback;
    private Map<TaskType,ThreadPoolExecutor> mThreadPool = new HashMap<>();
    private Map<TaskType,Class<? extends ITaskHandler>> mTaskHandlers = new HashMap<>();
    private Map<String,String> mParams;
    private Map<String,String> mHeaders;
    private List<ITaskHolder> mTasks = new ArrayList<>(); //task list


    @Override
    public void process(ITaskCmd cmd) {
        switch (cmd.getOperationType()) {
            case TYPE_ADD_TASKS:
                mProcessorProxy.addTasks(cmd.getTasks());
                break;
            case TYPE_ADD_TASK:
                mProcessorProxy.addTask(cmd.getTask());
                break;
            case TYPE_DELETE_TASK:
                mProcessorProxy.deleteTask(cmd.getTaskId());
                break;
            case TYPE_DELETE_TASKS_SOME:
                mProcessorProxy.deleteTasks(cmd.getTaskIds());
                break;
            case TYPE_DELETE_TASKS_GROUP:
                mProcessorProxy.deleteGroup(cmd.getGroupId());
                break;
            case TYPE_DELETE_TASKS_ALL:
                mProcessorProxy.deleteAll(cmd.getTaskType());
                break;
            case TYPE_DELETE_TASKS_COMPLETED:
                mProcessorProxy.deleteCompleted(cmd.getTaskType());
                break;
            case TYPE_DELETE_TASKS_STATE:
                mProcessorProxy.delete(cmd.getTask().getState(),cmd.getTaskType());
                break;
            case TYPE_CHANGE_TASK:
                mProcessorProxy.changeTasksState(cmd.getState(),cmd.getTaskIds());
                break;
            case TYPE_CHANGE_TASK_GROUP:
                mProcessorProxy.changeTasksState(cmd.getState(),cmd.getGroupId());
                break;
            case TYPE_QUERY_TASK:
                mProcessorProxy.getTask(cmd.getTaskId());
                break;
            case TYPE_QUERY_TASKS_SOME:
                mProcessorProxy.getTasks(cmd.getTaskIds());
                break;
            case TYPE_QUERY_TASKS_ALL:
                mProcessorProxy.getAllTasks(cmd.getTaskType());
                break;
            case TYPE_QUERY_TASKS_COMPLETED:
                mProcessorProxy.getTasks(TaskState.STATE_FINISH,cmd.getTaskType());
                break;
            case TYPE_QUERY_TASKS_GROUP:
                mProcessorProxy.getGroup(cmd.getGroupId());
                break;
            case TYPE_QUERY_TASKS_STATE:
                mProcessorProxy.getTasks(cmd.getState(),cmd.getTaskType());
                break;
            case TASK_CHANGE_TASK_ALL:
                mProcessorProxy.changeAllTasksState(cmd.getState(),cmd.getTaskType());
                break;
            case TASK_CHANGE_TASK_SOME:
                mProcessorProxy.changeTasksState(cmd.getState(),cmd.getTaskIds());
                break;
        }
        mCallback.onFinished(cmd.getTaskType(),cmd.getOperationType(),null);
    }

    @Override
    public void setTaskProcessor(ITaskProcessor operation) {
        mProcessorProxy = operation;
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
        mThreadPool.put(taskType,threadPool);
    }

    @Override
    public ThreadPoolExecutor getTaskThreadPool(TaskType type) {
        return mThreadPool.get(type);
    }

    @Override
    public ITaskHandler getTaskHandler(TaskType taskType) {
        Class<? extends ITaskHandler> handlerClzz = mTaskHandlers.get(taskType);
        try {
            ITaskHandler taskHandler = handlerClzz.newInstance();
            taskHandler.setThreadPool(getTaskThreadPool(taskType));
            taskHandler.setHeaders(mHeaders);
            taskHandler.setParams(mParams);
            return taskHandler;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setTaskHandler(TaskType type, Class<? extends ITaskHandler> handler) {
        mTaskHandlers.put(type,handler);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    @Override
    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    @Override
    public List<ITaskHolder> getTasks() {
        return mTasks;
    }

    @Override
    public void onStart(ITask params) {
        mProcessorProxy.changeTasksState(TaskState.STATE_START,params.getTaskId());
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onStop(ITask params) {
        mProcessorProxy.changeTaskState(TaskState.STATE_START,params.getTaskId());
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onError(int code, ITask params) {
        mProcessorProxy.changeTaskState(TaskState.STATE_START,params.getTaskId());
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onSpeedChanged(long speed, ITask params) {
        mProcessorProxy.updateTaskWithoutSave(params);
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onPiceSuccessful(ITask params) {
        mProcessorProxy.updateTask(params);
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onResume(ITask params) {
        mProcessorProxy.changeTaskStateWithOutSave(TaskState.STATE_RESUME,params.getTaskId());
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onPause(ITask params) {
        mProcessorProxy.changeTaskStateWithOutSave(TaskState.STATE_PAUSE,params.getTaskId());
        mCallback.onFinished(params.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }

    @Override
    public void onFinished(ITask task) {

        //完成的任务释放handler,减少占用的内存
        for(ITaskHolder holder : mTasks) {
            if(holder.getTask().getTaskId() == task.getTaskId()) {
                ITaskHandlerHolder handlerHolder = (ITaskHandlerHolder) holder;
                if(handlerHolder.getTaskHandler() != null) {
                    handlerHolder.setTaskHandler(null);
                    break;
                }
            }
        }

        mProcessorProxy.updateTask(task);
        mCallback.onFinished(task.getType(),ProcessType.TYPE_CHANGE_TASK,null);
    }
}
