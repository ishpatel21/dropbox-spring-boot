package com.dropbox.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.web.data.Download;

	
@RestController
@RequestMapping("/dropbox")
public class DropboxController {

	@Autowired
	private DropboxService service;

    @GetMapping("/getFileList")
	public List<Map<String, Object>> getFileList(@RequestParam String target){
		return service.getFileList(target);
	}
    
    @PostMapping("/upload")
    public void uploadFile(@RequestBody MultipartFile file, @RequestParam String filePath) {
    	service.uploadFile(file, filePath);
    }
    
    @PostMapping("/download")
    public void downloadFile(HttpServletResponse response, @RequestBody Download download) {
    	service.downloadFile(response, download);
    }
    
    @PostMapping("/delete")
    public void deleteFile(@RequestParam String filePath) {
    	service.deleteFile(filePath);
    }
}
