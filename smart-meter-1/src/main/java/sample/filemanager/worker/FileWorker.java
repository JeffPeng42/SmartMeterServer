package sample.filemanager.worker;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sample.filemanager.io.IOManager;
import sample.filemanager.queue.InnerQueue;
import sample.filemanager.vo.FileContent;

@Slf4j
@Component
@Scope(value = "prototype")
public class FileWorker implements Runnable {
	
	@Autowired
	InnerQueue<FileContent> fileDataQueue;
	
	@Autowired
	IOManager ioManager;
	
	@Override
	public void run() {
		log.info("<FileManager> <" + Thread.currentThread().getName() + "> init success.");
		while (true) {
			try {
				// 等待換日結束，再往下做
				if (ioManager.checkIsChangeingDate()) {
					Thread.sleep(5000);
					continue;
				}
				
				FileContent takeData = fileDataQueue.takeData();
				String fileName = takeData.getFileName();
				String jsonContent = takeData.getJsonContent();
				try {
					// 寫檔案
					ioManager.appendLineToFile(fileName, jsonContent);
				}
				catch (IOException e) {
					log.error("IOException raised while write data to file", e);
				}
				
			}
			catch (InterruptedException e) {
				log.error("InterruptedException raised while take data from fileDataQueue", e);
			}
		}
		
	}
	
}