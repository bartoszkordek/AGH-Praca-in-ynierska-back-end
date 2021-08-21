package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.ImageValidator;
import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.pojo.response.AvatarResponse;
import com.healthy.gym.account.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.UnsupportedDataTypeException;
import java.time.Duration;

@RestController
@RequestMapping("/photos/{id}/avatar")
public class PhotoController {

    private static final String EXCEPTION_ACCOUNT_NOT_FOUND = "exception.account.not.found";
    private static final String REQUEST_FAILURE = "request.failure";

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvatarResponse> setAvatar(
            @PathVariable("id") String userId,
            @RequestParam("avatar") MultipartFile multipartFile
    ) {
        try {
            imageValidator.isFileSupported(multipartFile);
            String avatarLocation = photoService.setAvatar(userId, multipartFile);
            String message = translator.toLocale("avatar.update.success");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AvatarResponse(message, avatarLocation));

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_ACCOUNT_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (PhotoSavingException exception) {
            String reason = translator.toLocale("avatar.update.failure");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UnsupportedDataTypeException exception) {
            String reason = translator.toLocale("avatar.update.data.exception");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{version}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable("id") String userId, @PathVariable String version) {
        try {
            byte[] image = photoService.getAvatar(userId);
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

        } catch (UserAvatarNotFoundException exception) {
            String reason = translator.toLocale("avatar.not.found.exception");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_ACCOUNT_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvatarResponse> deleteAvatar(@PathVariable("id") String userId) {
        try {
            photoService.removeAvatar(userId);
            String message = translator.toLocale("avatar.removed");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new AvatarResponse(message, null));

        } catch (UserAvatarNotFoundException exception) {
            String reason = translator.toLocale("avatar.not.found.exception");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UsernameNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_ACCOUNT_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
