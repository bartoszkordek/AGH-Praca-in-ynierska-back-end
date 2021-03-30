package com.healthy.gym.trainings.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainings")
public class SignedClientTrainingsController {

    @RequestMapping("/group/enroll/{trainingId}")
    public void enrollToGroupTraining(@PathVariable("trainingId") final Object trainingId,
                                      @RequestParam(required = true) final String clientId){
        System.out.println(clientId);
        System.out.println(trainingId);

    }
}
