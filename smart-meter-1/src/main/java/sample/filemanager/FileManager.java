package sample.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sample.filemanager.io.IOManager;
import sample.filemanager.queue.InnerQueue;
import sample.filemanager.vo.FileContent;
import sample.filemanager.worker.FileWorker;

@Component
@Slf4j
public class FileManager {
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	IOManager ioManager;
	
	@Autowired
	InnerQueue<FileContent> fileDataQueue;
	
	@Value("${output.file.workCount}")
	private String fileWorkThreadCount = "1";
	
	public void initFileManager() {
		log.info("<FileManager> Prepare to init initFileManager");
		ioManager.initFileDir();
		
		int workCount = Integer.valueOf(fileWorkThreadCount);
		for (int i = 0; i < workCount; i++) {
			Thread thread = new Thread(context.getBean(FileWorker.class));
			thread.setName("FileWorker-" + i);
			thread.start();
		}
		log.info("<FileManager> Prepare to init initFileManager done");
	}
	
	public void putJsonData(String fileName, String jsonContent) {
		log.debug("<FileManager> prepare to process data, file<" + fileName + ", jsonContent:<" + jsonContent + ">");
		FileContent fileContent = new FileContent();
		fileContent.setFileName(fileName);
		fileContent.setJsonContent(jsonContent);
		try {
			fileDataQueue.putData(fileContent);
		}
		catch (InterruptedException e) {
			log.error("InterruptedException raised while putJsonData", e);
		}
	}
	
}
