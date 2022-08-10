package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.service.UserAccountsServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class ProfileImageController {

    @Autowired
    private UserAccountsServerService userAccountsServerService;

    /**
     * Finds the request profile picture if it exists and returns a byte array
     * @param filename the filename of the profile picture to retrieve
     * @return a byte array containing the image data
     */
    @GetMapping("/ProfilePicture-{filename}")
    public ResponseEntity<byte[]> getProfilePicture(
            @PathVariable("filename") String filename
    ) {
        try {
            byte[] bytes = userAccountsServerService.getProfilePicture(filename);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }
    }
}