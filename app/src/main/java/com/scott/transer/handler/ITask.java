package com.scott.transer.handler;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 12:52</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITask {

    String getDataSource();

    String getDestSource();

    String getSesstionId();

    long getLength();

    long getStartOffset();

    long getEndOffset();

    String getTaskId();

    String getGroupId();

    String getGroupName();

    long getCompleteTime();

    long getCompleteLength();
}
