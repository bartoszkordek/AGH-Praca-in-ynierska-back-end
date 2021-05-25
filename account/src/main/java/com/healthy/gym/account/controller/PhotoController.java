package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.ImageValidator;
import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.exception.PhotoSavingException;
import com.healthy.gym.account.pojo.response.SetAvatarResponse;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.shared.PhotoDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
    private final AccountService accountService;
    private final Translator translator;
    private final ModelMapper modelMapper;
    private final PhotoService photoService;
    private final ImageValidator imageValidator;

    @Autowired
    public PhotoController(
            AccountService accountService,
            Translator translator,
            PhotoService photoService,
            ImageValidator imageValidator
    ) {
        this.accountService = accountService;
        this.translator = translator;
        this.photoService = photoService;
        this.imageValidator = imageValidator;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @PostMapping(
            value = "/{id}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SetAvatarResponse> setAvatar(
            @PathVariable("id") String userId,
            @RequestParam("avatar") MultipartFile multipartFile
    ) {
        try {
            imageValidator.isFileSupported(multipartFile);
            PhotoDTO photoDTO = new PhotoDTO(userId, multipartFile.getOriginalFilename(), multipartFile.getBytes());
            photoService.setAvatar(photoDTO);
            String message = translator.toLocale("avatar.update.success");
            return ResponseEntity.status(HttpStatus.OK).body(new SetAvatarResponse(message));

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
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getAvatar(@PathVariable("id") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userId);
    }
}
