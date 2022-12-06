package com.utosoftware.fileupdown.controller;

import com.utosoftware.fileupdown.entity.Attachment;
import com.utosoftware.fileupdown.model.ResponseData;
import com.utosoftware.fileupdown.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/")
public class AttachmentController {
    private AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file")MultipartFile file) throws Exception {
        Attachment attachment = null;
        // DB에 있는 파일을 다운받을 위치 설정
        String downloadURI = "";

        attachment = service.saveAttachment(file);
        downloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();
        return new ResponseData(attachment.getFileName(),downloadURI,file.getContentType(),file.getSize());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId) throws Exception {
        Attachment attachment = null;
        attachment = service.getAttachment(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + attachment.getFileName()
                + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

}
