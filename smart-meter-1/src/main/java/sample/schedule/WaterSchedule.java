package sample.schedule;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sample.mapper.TestAMapper;
import sample.mapper.vo.Checkdb;
import sample.mapper.vo.MeteringData_Data;
import sample.request.HttpRequestHandler;


@Component 
@Slf4j
public class WaterSchedule {

	@Autowired
	private HttpRequestHandler httpRequestHandler;

	@Autowired
	private TestAMapper testAMapper;

	public String key(long date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			return dateFormat.format(date);
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String yesterday() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
			return dateFormat.format(date);
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String today() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			date = calendar.getTime();
			return dateFormat.format(date);
		} catch (NumberFormatException e) {
			return "";
		}
	}
//	@Scheduled(cron = "0 6 18 * * *")
//	public void scheduleTask() {
//
//		Checkdb data = new Checkdb();
//		data.setStarttime(yesterday());
//		data.setEndtime(today());
//		System.out.println(data);
//		List<MeteringData_Data> getdatas = testAMapper.getdatas(data);
//		System.out.println(getdatas);
//		String url = "http://localhost:8080//syscom/taipeiwater";
//		String responsed = httpRequestHandler.sendmeterPost(url, getdatas, HttpServletResponse.class);
//		System.out.println(responsed);

//		log.info("Prepare to execute scheduleFixedDelayTask");
//	}

}
