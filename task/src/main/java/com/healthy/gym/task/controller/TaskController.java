package com.healthy.gym.task.controller;

import com.healthy.gym.task.component.Translator;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.ManagerOrderRequest;
import com.healthy.gym.task.pojo.response.TaskResponse;
import com.healthy.gym.task.service.TaskService;
import com.healthy.gym.task.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class TaskController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private final Translator translator;
    private final TaskService taskService;

    @Autowired
    public TaskController(
            Translator translator,
            TaskService taskService
    ){
        this.translator = translator;
        this.taskService = taskService;
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody final ManagerOrderRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {

        try{
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("task.created");

            TaskDTO taskDTO = taskService.createTask(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new TaskResponse(message, taskDTO));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ManagerNotFoundException exception){
            String reason = translator.toLocale("exception.manager.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale("exception.employee.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (RetroDueDateException exception){
            String reason = translator.toLocale("exception.retro.due.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable("id") @ValidIDFormat final String id,
            @Valid @RequestBody final ManagerOrderRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {

        try{
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("task.updated");

            TaskDTO taskDTO = taskService.updateTask(id, request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponse(message, taskDTO));

        } catch (TaskNotFoundException exception){
            String reason = translator.toLocale("exception.task.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ManagerNotFoundException exception){
            String reason = translator.toLocale("exception.manager.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale("exception.employee.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (RetroDueDateException exception){
            String reason = translator.toLocale("exception.retro.due.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(
            @PathVariable("id") @ValidIDFormat final String id
    ){
        try {
            String message = translator.toLocale("task.removed");

            TaskDTO taskDTO = taskService.deleteTask(id);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponse(message, taskDTO));

        } catch (TaskNotFoundException exception){
            String reason = translator.toLocale("exception.task.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
