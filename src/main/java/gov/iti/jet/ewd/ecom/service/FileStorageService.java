package gov.iti.jet.ewd.ecom.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    public String storeFile(MultipartFile file);


}
