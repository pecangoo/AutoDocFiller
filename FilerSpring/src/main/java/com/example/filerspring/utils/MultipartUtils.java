package com.example.filerspring.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@UtilityClass
@Slf4j
public class MultipartUtils {

    public File convertToFile(MultipartFile multipartFile) throws IOException {
        File file = null;
        if (Objects.nonNull(multipartFile.getOriginalFilename())) {
            file = new File(multipartFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }
        } else {
            log.warn("getOriginalFilename is null");
        }
        return file;
    }
}
