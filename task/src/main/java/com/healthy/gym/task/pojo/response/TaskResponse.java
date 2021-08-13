package com.healthy.gym.task.pojo.response;

import com.healthy.gym.task.dto.TaskDTO;

import java.util.Objects;

public class TaskResponse extends AbstractResponse{

    private TaskDTO task;

    public TaskResponse() {
    }

    public TaskResponse(String message, TaskDTO task) {
        super(message);
        this.task = task;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "EmployeeReportResponse{" +
                "task=" + task +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TaskResponse that = (TaskResponse) o;
        return Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), task);
    }
}
