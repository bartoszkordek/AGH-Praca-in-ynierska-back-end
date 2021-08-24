package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.notfound.ImageNotFoundException;
import com.healthy.gym.trainings.service.PhotoService;
import org.springframework.http.*;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
public class PhotoController {

    private final Translator translator;
    private final PhotoService photoService;

    public PhotoController(Translator translator, PhotoService photoService) {
        this.translator = translator;
        this.photoService = photoService;
    }

    @GetMapping("/trainingType/image/{imageId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String imageId) {
        try {
            byte[] image = photoService.getImage(imageId);
            String etag = DigestUtils.md5DigestAsHex(image);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(image.length);
            headers.setContentType(MediaType.IMAGE_JPEG);

            CacheControl cacheControl = CacheControl.maxAge(Duration.ofMinutes(10));
            cacheControl.cachePrivate();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .cacheControl(cacheControl)
                    .eTag(etag)
                    .body(image);

        } catch (ImageNotFoundException exception) {
            String reason = translator.toLocale("avatar.not.found.exception");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
