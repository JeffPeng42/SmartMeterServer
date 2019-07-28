package sample.filemanager.io;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;
import sample.filemanager.worker.FileWorker;

@Component
@Slf4j
public class IOManager {
	
	private boolean isInChangeingDate = false;
	
	@Value("${output.file.dir}")
	private String outFileDirect;
	
	private Map<String, File> mapFiles = new Hashtable<String, File>();
	
	private String date;
	
	public boolean checkIsChangeingDate() {
		return isInChangeingDate;
	}
	
	//	@Scheduled(cron = teString)
	@Scheduled(cron = "10 0 0 * * *")
	public void initFileDir() {
		log.info("<FileManager> Prepare to init file directory");
		isInChangeingDate = true;
		
		// 開始進行換日換檔動作
		mapFiles.clear();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		date = dateFormat.format(System.currentTimeMillis());
		
		log.info("<FileManager> Init file directory done, dir:<" + outFileDirect + "/" + date + ">");
		isInChangeingDate = false;
	}
	
	public void appendLineToFile(String fileName, String line) throws IOException {
		File file = getFile(fileName);
		Files.write("".getBytes(), file);
		CharSink chs = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
		chs.write(line);
		chs.write("\r\n");
	}
	
	private File getFile(String fileName) throws IOException {
		File file;
		if (mapFiles.containsKey(fileName)) {
			file = mapFiles.get(fileName);
		}
		else {
			file = createFile(fileName);
		}
		return file;
	}
	
	private File createFile(String fileName) throws IOException {
		File file = new File(outFileDirect + "/" + date + "/" + fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			log.info("<FileManager> Create new file:<" + file.getAbsolutePath() + ">");
		}
		mapFiles.put(fileName, file);
		return file;
	}
}
