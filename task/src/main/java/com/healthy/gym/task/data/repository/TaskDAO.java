package com.healthy.gym.task.data.repository;

import com.healthy.gym.task.data.document.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskDAO  extends MongoRepository<TaskDocument, String> {
}
