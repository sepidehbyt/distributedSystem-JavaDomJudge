package com.distributed.systems.dom_judge.service;

import com.distributed.systems.dom_judge.model.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
public class UploadService {

    @Value("${dom.judge.path.files}")
    private String sourceDirectory;

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase());
    }

    public String saveNewResourceInFile(MultipartFile uploadFile, String name, String extension) {
        BufferedOutputStream writer = null;
        BufferedInputStream reader = null;
        try {
            String randomDir = Integer.toString((int) ((Math.random() * ( 99 - 10 )) + 10));
            String directory = sourceDirectory.
                    concat("/f").concat(Character.toString(randomDir.charAt(0))).
                    concat("/f").concat(Character.toString(randomDir.charAt(1)));
            String filePath = Paths.get(directory, name.concat(".").concat(extension)).toString();
            File file = new File(filePath);
            writer = new BufferedOutputStream(new FileOutputStream(file, true));
            reader = new BufferedInputStream(uploadFile.getInputStream());
            final byte[] buffer = new byte[100000];
            while (true) {
                final int amountRead = reader.read(buffer);
                if (amountRead == -1) {
                    break;
                }
                writer.write(buffer, 0, amountRead);
            }
            file.setReadable(true, false);
            file.setExecutable(true, false);
            file.setWritable(true, false);
            writer.flush();
            return filePath;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
