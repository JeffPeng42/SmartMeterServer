package sample.seq;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sample.mapper.TestAMapper;

@Component
@Slf4j
public class SeqManager {
	@Autowired
	TestAMapper testAMapper;
	
	public AtomicLong seqNumber;
	
	private String strToday;
	
	public void initSeq() {
		getDate();
		
		String byschedule_no = testAMapper.getByschedule_no(strToday + "%");
		seqNumber = new AtomicLong();
		if (byschedule_no != null) {
			String schedule_no = byschedule_no;
			long seq = Long.parseLong(schedule_no);
			seqNumber.set(seq);
		}
		else {
			seqNumber.set(Long.valueOf((strToday + "0000")));
		}
		
		log.info("Init seq done, current seq:<" + seqNumber.get() + ">");
	}
	
	private void getDate() {
		if (strToday == null || strToday.trim().isEmpty()) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				strToday = dateFormat.format(System.currentTimeMillis());
			}
			catch (NumberFormatException e) {
				log.error("NumberFormatException raised while getStrTodat", e);
			}
		}
	}
	
	public String getStrSeq() {
		long incrementAndGet = seqNumber.incrementAndGet();
		String result = String.valueOf(incrementAndGet);
		return result;
	}
	
	@Scheduled(cron = "20 0 0 * * *")
	public void scheduleNo() {
		initSeq();
	}
	
}
