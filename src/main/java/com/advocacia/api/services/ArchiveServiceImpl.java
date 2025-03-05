package com.advocacia.api.services;

import com.advocacia.api.domain.archive.Archive;
import com.advocacia.api.repositories.ArchiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.io.File;

@Service
public class ArchiveServiceImpl implements IArchiveService {

    final ArchiveRepository archiveRepository;

    private String destSendDocument = Paths.get("").toAbsolutePath().toString();
    private String destModel = Paths.get("").toAbsolutePath().toString();

    public ArchiveServiceImpl(ArchiveRepository archiveRepository) {
        this.archiveRepository = archiveRepository;
    }

    @Override
    public Archive findByPath(String path) {
        return archiveRepository.findByLinkArchive(path);
    }

    @Override
    public Optional<Archive> findById(String id) {
        return archiveRepository.findById(id);
    }

    @Override
    public Archive sendDocument(MultipartFile file, String fileName) {
        try{
            String destFolder = destSendDocument;
            String name = fileName;
            Path path = Paths.get(destFolder + File.separator + name);
            Archive fileExists = findByPath(path.toString());
            if(fileExists == null){
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                Archive arch = new Archive();
                arch.setLinkArchive(path.toString());
                arch.setName(name);
                return archiveRepository.save(arch);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Archive saveModel(MultipartFile file) {
        try{
            String destFolder = destModel;
            Path path = Paths.get(destFolder + File.separator + file.getOriginalFilename());
            Archive arqExist = findByPath(path.toString());
            if(arqExist == null){
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                Archive arch = new Archive();
                arch.setLinkArchive(path.toString());
                arch.setName(file.getOriginalFilename());
                return archiveRepository.save(arch);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        archiveRepository.deleteById(id);
    }

    @Override
    public List<Archive> findAllModel() {
        return archiveRepository.findAllModel(destModel);
    }

    @Override
    public String readArchive(String name) {
        StringBuilder strOut = new StringBuilder();
        try{
            String[] command;
            command = new String[]{"sh", "-c", "cat " + name};
//            command = new String[]{"cmd.exe", "/c", "type " + name};

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);
            int result = proc.waitFor();
            if(result != 0){
                throw new IOException("Processo encontrou um erro: " + result);
            }
            InputStream in = (result == 0) ? proc.getInputStream():proc.getErrorStream();
            int c;
            while((c=in.read())!= -1){
                strOut.append((char) c);
            }
            return strOut.toString();

        }catch(Exception e){
            return e.toString();
        }
    }

    @Override
    public boolean archiveExists(String name) {
        Archive archive = archiveRepository.findByName(name);
        return archive != null;
    }
}