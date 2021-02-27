package com.distributed.systems.dom_judge.config;

import com.distributed.systems.dom_judge.model.Role;
import com.distributed.systems.dom_judge.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;

@Component
public class DomJudgeApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${dom.judge.path.files}")
    private String sourceDirectory;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (alreadySetup)
            return;
        createAllFiles();
        createRoles();
        alreadySetup = true;
    }

    private void createAllFiles() {
        for (int i = 0; i < 10; i++) {
            File firstLevel = new File(sourceDirectory, String.valueOf(i));
            if (!firstLevel.exists()) {
                firstLevel.mkdirs();
                firstLevel.setReadable(true, false);
                firstLevel.setExecutable(true, false);
                firstLevel.setWritable(true, false);
            }
            for (int j = 0; j < 10; j++) {
                File secondLevel = new File(firstLevel, String.valueOf(j));
                if (!secondLevel.exists()) {
                    secondLevel.mkdirs();
                    secondLevel.setReadable(true, false);
                    secondLevel.setExecutable(true, false);
                    secondLevel.setWritable(true, false);
                }
            }
        }
    }

    private void createRoles() {
        if(!roleRepository.findByName("ROLE_ADMIN").isPresent())
            roleRepository.save(new Role("ROLE_ADMIN"));
        if(!roleRepository.findByName("ROLE_USER").isPresent())
            roleRepository.save(new Role("ROLE_USER"));
    }

}
