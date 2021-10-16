package com.dropbox.web.controller;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.web.data.Download;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DropboxService {
	private final DbxClientV2 dropboxClient;

    public DropboxService(DbxClientV2 client) {
        this.dropboxClient = client;
    }
    
	public List<Map<String, Object>> getFileList(String target){
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			List<Metadata> entries = dropboxClient.files().listFolder(target).getEntries();
			for (Metadata entry : entries ) {
				log.info("{} is file", entry.getName());
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(entry.toString(), new TypeReference<Map<String, Object>>(){});
				result.add(map);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void uploadFile(MultipartFile file, String filePath){
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(file.getBytes());
			Metadata uploadMetaData = dropboxClient.files().uploadBuilder(filePath).uploadAndFinish(inputStream);
			log.info("upload meta data ==> {}", uploadMetaData.toString());
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadFile(HttpServletResponse response, Download download){
		try {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(download.getFileName(), "UTF-8")+"\";");
			response.setHeader("Content-Transfer-Encoding", "binary");
	
			ServletOutputStream outputStream = response.getOutputStream();
			dropboxClient.files().downloadBuilder(download.getFilePath()).download(outputStream);
	
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String filePath){
		try {
			dropboxClient.files().deleteV2(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
