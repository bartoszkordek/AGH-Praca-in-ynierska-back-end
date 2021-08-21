package com.healthy.gym.task.controller;

import com.healthy.gym.task.component.Translator;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.pojo.request.EmployeeAcceptDeclineTaskRequest;
import com.healthy.gym.task.pojo.request.EmployeeReportRequest;
import com.healthy.gym.task.pojo.request.ManagerReportVerificationRequest;
import com.healthy.gym.task.pojo.request.ManagerTaskCreationRequest;
import com.healthy.gym.task.pojo.response.TaskResponse;
import com.healthy.gym.task.service.TaskService;
import com.healthy.gym.task.validation.ValidDateFormat;
import com.healthy.gym.task.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class TaskController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private static final String REQUEST_BIND_EXCEPTION = "request.bind.exception";
    private static final String TASK_NOT_FOUND_EXCEPTION = "exception.task.not.found";
    private static final String MANAGER_NOT_FOUND_EXCEPTION = "exception.manager.not.found";
    private static final String EMPLOYEE_NOT_FOUND_EXCEPTION = "exception.employee.not.found";
    private static final String TASK_DECLINED_BY_EMPLOYEE_EXCEPTION = "exception.declined.employee";
    private static final String INVALID_STATUS_EXCEPTION = "exception.invalid.status";
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
            @Valid @RequestBody final ManagerTaskCreationRequest request,
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
            String reason = translator.toLocale(REQUEST_BIND_EXCEPTION);
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ManagerNotFoundException exception){
            String reason = translator.toLocale(MANAGER_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale(EMPLOYEE_NOT_FOUND_EXCEPTION);
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
            @Valid @RequestBody final ManagerTaskCreationRequest request,
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
            String reason = translator.toLocale(TASK_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (BindException exception) {
            String reason = translator.toLocale(REQUEST_BIND_EXCEPTION);
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ManagerNotFoundException exception){
            String reason = translator.toLocale(MANAGER_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale(EMPLOYEE_NOT_FOUND_EXCEPTION);
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
    @DeleteMapping("/{id}")
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
            String reason = translator.toLocale(TASK_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    @PreAuthorize("principal==#userId")
    @PutMapping("/{taskId}/employee/{userId}/approvalStatus")
    public ResponseEntity<TaskResponse> acceptDeclineTaskByEmployee(
            @PathVariable("taskId") @ValidIDFormat final String taskId,
            @PathVariable("userId") @ValidIDFormat final String userId,
            @Valid @RequestBody final EmployeeAcceptDeclineTaskRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {

        try{
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message;
            TaskDTO taskDTO = taskService.acceptDeclineTaskByEmployee(taskId, userId, request);
            AcceptanceStatus taskStatus = taskDTO.getEmployeeAccept();
            if(taskStatus.equals(AcceptanceStatus.ACCEPTED)){
                message = translator.toLocale("task.approved.employee");
            } else {
                message = translator.toLocale("task.declined.employee");
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponse(message, taskDTO));

        } catch (BindException exception) {
            String reason = translator.toLocale(REQUEST_BIND_EXCEPTION);
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TaskNotFoundException exception){
            String reason = translator.toLocale(TASK_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale(EMPLOYEE_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (InvalidStatusException exception){
            String reason = translator.toLocale(INVALID_STATUS_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("principal==#userId")
    @PutMapping("/{taskId}/employee/{userId}/report")
    public ResponseEntity<TaskResponse> sendReport(
            @PathVariable("taskId") @ValidIDFormat final String taskId,
            @PathVariable("userId") @ValidIDFormat final String userId,
            @Valid @RequestBody final EmployeeReportRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            String message = translator.toLocale("report.sent");

            TaskDTO taskDTO = taskService.sendReport(taskId, userId, request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponse(message, taskDTO));

        } catch (BindException exception) {
            String reason = translator.toLocale(REQUEST_BIND_EXCEPTION);
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TaskNotFoundException exception){
            String reason = translator.toLocale(TASK_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TaskDeclinedByEmployeeException exception){
            String reason = translator.toLocale(TASK_DECLINED_BY_EMPLOYEE_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DueDateExceedException exception){
            String reason = translator.toLocale("exception.due.date.exceed");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ReportAlreadySentException exception){
            String reason = translator.toLocale("exception.already.sent.report");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{taskId}/reportVerification")
    public ResponseEntity<TaskResponse> verifyReport(
            @PathVariable("taskId") @ValidIDFormat final String taskId,
            @Valid @RequestBody ManagerReportVerificationRequest request,
            final BindingResult bindingResult
    ) throws RequestBindException {
        try{
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            TaskDTO taskDTO = taskService.verifyReport(taskId, request);

            String message = translator.toLocale("report.declined.manager");
            AcceptanceStatus managerAcceptanceStatus = taskDTO.getManagerAccept();
            if(managerAcceptanceStatus.equals(AcceptanceStatus.ACCEPTED))
                message = translator.toLocale("report.approved.manager");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new TaskResponse(message, taskDTO));


        } catch (BindException exception) {
            String reason = translator.toLocale(REQUEST_BIND_EXCEPTION);
            throw new RequestBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TaskNotFoundException exception) {
            String reason = translator.toLocale(TASK_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (InvalidMarkException exception) {
            String reason = translator.toLocale("exception.invalid.mark");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (InvalidStatusException exception){
            String reason = translator.toLocale(INVALID_STATUS_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TaskDeclinedByEmployeeException exception){
            String reason = translator.toLocale(TASK_DECLINED_BY_EMPLOYEE_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (ReportNotSentException exception){
            String reason = translator.toLocale("exception.report.not.sent");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/page/{page}")
    public List<TaskDTO> getTasks(
            @ValidDateFormat @RequestParam(value = "startDueDate", required = false) final String startDueDate,
            @ValidDateFormat @RequestParam(value = "endDueDate", required = false) final String endDueDate,
            @RequestParam(defaultValue = "10", required = false) final int size,
            @PathVariable("page") final int page
    ){
        try{
            Pageable paging = PageRequest.of(page, size);
            return taskService.getTasks(startDueDate, endDueDate, paging);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NoTasksException exception) {
            String reason = translator.toLocale("exception.no.tasks");
            throw new ResponseStatusException(HttpStatus.OK, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
