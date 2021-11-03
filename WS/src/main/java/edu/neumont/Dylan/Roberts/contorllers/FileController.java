package edu.neumont.Dylan.Roberts.contorllers;

import edu.neumont.Dylan.Roberts.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    private ImageService service;

    @Autowired
    public FileController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public String testAPI() {
        return "API is working";
    }

    @GetMapping(value = "/image/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String name, @RequestParam(required = false) String transform) throws IOException {
        try {
            byte[] image = null;

            if (transform != null) {
                switch (transform) {
                    case "grayscale":
                        image = service.grayScaleImage(name);
                        break;
                    case "rotate":
                        image = service.roatateImage(name);
                        break;
                }
            }else{
                image = service.getImage(name);
            }

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);

        } catch (IOException ioe) {
            return null;
        }
    }


    @PostMapping("/image")
    public ResponseEntity<String> createImage(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String imageName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String imageDirectory = "images/";

        try {
            service.createImage(imageDirectory, imageName, multipartFile);
            return new ResponseEntity<String>("image saved", HttpStatus.OK);

        } catch (IOException ioe) {
            System.out.println(ioe);
            return new ResponseEntity<String>("Error: the name already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/image/{name}")
    public ResponseEntity<HttpStatus> deleteImage(@PathVariable String name) throws IOException {
        service.deleteImage(name);

        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }
}
