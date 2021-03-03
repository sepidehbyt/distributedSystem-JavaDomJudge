package com.distributed.systems.dom_judge.service;

import com.distributed.systems.dom_judge.enumuration.AnswerStatus;
import com.distributed.systems.dom_judge.enumuration.IO;
import com.distributed.systems.dom_judge.model.Answer;
import com.distributed.systems.dom_judge.model.Question;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

@Service
@Transactional
public class ResourceService {

    @Value("${dom.judge.path.files}")
    private String sourceDirectory;

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase());
    }

    public String getFileActualName(String originalFilename) {
        return FilenameUtils.removeExtension(originalFilename);
    }

    public String saveNewResourceInFile(MultipartFile uploadFile, String fileName, String folderName, String extension) {
        BufferedOutputStream writer = null;
        BufferedInputStream reader = null;
        try {
            String randomDir = Integer.toString((int) ((Math.random() * ( 99 - 10 )) + 10));
            String directory = sourceDirectory.
                    concat("/").concat(Character.toString(randomDir.charAt(0))).
                    concat("/").concat(Character.toString(randomDir.charAt(1)));
            File folder = new File(directory, folderName);
            if (!folder.exists()) {
                folder.mkdirs();
                folder.setReadable(true, false);
                folder.setExecutable(true, false);
                folder.setWritable(true, false);
            }
            String filePath = Paths.get(String.valueOf(folder), fileName.concat(".").concat(extension)).toString();
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
            return filePath.replaceAll(sourceDirectory, "");
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

    public AnswerStatus checkUploadedCode(Answer answer, Question question) {
        try {
            String codePath = sourceDirectory.concat(answer.getCodePath());
            File codeFile = new File(codePath);
            String codeName = codeFile.getName();
            // compile Code
            runProcess("javac ".concat(codeName), codeFile.getParentFile());
            // run code
            File inputFile = new File(sourceDirectory.concat(question.getInputPath()));
            File outputFile = new File(sourceDirectory.concat(question.getOutputPath()));
            Scanner inputReader = new Scanner(inputFile);
            Scanner outputReader = new Scanner(outputFile);
            while (inputReader.hasNextLine() && outputReader.hasNextLine()) {
                String input = inputReader.nextLine();
                String output = outputReader.nextLine().trim();
                // test code
                String userOutput = runProcess("java ".concat(codeName.replaceAll(".java", ""))
                        .concat(" ").concat(input), codeFile.getParentFile());
                System.out.println(output + "   " + userOutput);
                if(!output.equals(userOutput)) return AnswerStatus.FAILURE;
            }
            inputReader.close();
            outputReader.close();
            return AnswerStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AnswerStatus.FAILURE;
    }

    private String runProcess(String command, File codeFile) throws Exception {
        System.out.println(codeFile);
        Process pro = Runtime.getRuntime().exec(command, null, codeFile);
        String out = printLines(pro.getInputStream());
        String error = printLines(pro.getErrorStream());
        pro.waitFor();
        int exitCode = pro.exitValue();
        System.out.println(command + " exitCode: " + exitCode);
        if(exitCode == 0) {
            if(error.equals("")) {
                return out.trim();
            }
        }
        return "";
    }

    private String printLines(InputStream ins) throws Exception {
        StringBuilder print = new StringBuilder();
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            print.append(line);
        }
        System.out.println(print.toString());
        return print.toString();
    }
}
