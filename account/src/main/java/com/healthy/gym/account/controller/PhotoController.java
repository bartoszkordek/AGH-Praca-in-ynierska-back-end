package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.ImageValidator;
import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.response.AvatarResponse;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.shared.ImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.UnsupportedDataTypeException;

@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final Translator translator;
    private final PhotoService photoService;
    private final ImageValidator imageValidator;

    @Autowired
    public PhotoController(
            Translator translator,
            PhotoService photoService,
            ImageValidator imageValidator
    ) {
        this.translator = translator;
        this.photoService = photoService;
        this.imageValidator = imageValidator;
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @PostMapping(
            value = "/{id}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvatarResponse> setAvatar(
            @PathVariable("id") String userId,
            @RequestParam("avatar") MultipartFile multipartFile
    ) {
        try {
            imageValidator.isFileSupported(multipartFile);
            ImageDTO savedAvatar = photoService.setAvatar(userId, multipartFile);
            String message = translator.toLocale("avatar.update.success");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AvatarResponse(message, savedAvatar));

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (PhotoSavingException exception) {
            String reason = translator.toLocale("avatar.update.failure");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UnsupportedDataTypeException exception) {
            String reason = translator.toLocale("avatar.update.data.exception");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(
            value = "/{id}/avatar",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvatarResponse> getAvatar(@PathVariable("id") String userId) {
        try {
            ImageDTO imageDTO = photoService.getAvatar(userId);
            String message = translator.toLocale("avatar.get.found");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AvatarResponse(message, imageDTO));

        } catch (UserAvatarNotFoundException exception) {
            String reason = translator.toLocale("avatar.not.found.exception");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @DeleteMapping(
            value = "/{id}/avatar",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvatarResponse> deleteAvatar(@PathVariable("id") String userId) {
        try {
            ImageDTO imageDTO = photoService.removeAvatar(userId);
            String message = translator.toLocale("avatar.removed");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AvatarResponse(message, imageDTO));

        } catch (UserAvatarNotFoundException exception) {
            String reason = translator.toLocale("avatar.not.found.exception");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale("exception.account.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("request.failure");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
