package sample.filemanager.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import sample.filemanager.vo.FileContent;

/**
 * @author User
 *         在這邊用@Configuration初始話與客製化設定threadpool為bean 讓其他程式使用
 */
@Configuration
@Slf4j
public class FileQueueConfiguration {
	InnerQueue<FileContent> fileQueue;
	
	@Bean(name = "fileDataQueue")
	public InnerQueue<FileContent> initFileQueue() {
		if (fileQueue == null) {
			fileQueue = new InnerQueue<FileContent>();
			log.info("Init file queue done");
		}
		return fileQueue;
	}
	
}