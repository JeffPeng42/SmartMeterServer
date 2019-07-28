package sample.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sample.configuration.AppProperties;

@Component
@Slf4j
public class ScheduleManager {
	
	@Autowired
	AppProperties appProperties;
	
	// key : scheduleName, value : [ScheduledFuture, ThreadPoolTaskScheduler, Cron, runnable]
	private Map<String, Object[]> mapSchedules = new Hashtable<String, Object[]>();
	
	public void setSchedule(String scheduleName, Runnable runnable) {
		if (mapSchedules.containsKey(scheduleName)) {
			stopAndRemoveSchedule(scheduleName);
			
			addAndStartSchedule(scheduleName, runnable);
		}
		else {
			addAndStartSchedule(scheduleName, runnable);
		}
		
	}
	
	private void addAndStartSchedule(String scheduleName, Runnable runnable) {
		ThreadPoolTaskScheduler schedule = new ThreadPoolTaskScheduler();
		schedule.setPoolSize(1);
		schedule.initialize();
		String cron = appProperties.getProperty(scheduleName);
		log.info("<ScheduleManager> Prepare to set schedule, name:<" + scheduleName + ">, Cron:<" + cron + ">");
		ScheduledFuture<?> future = schedule.schedule(runnable, new CronTrigger(cron));
		mapSchedules.put(scheduleName, new Object[] { future, schedule, cron, runnable });
		log.info("<ScheduleManager> Set schedule done, name:<" + scheduleName + ">, Cron:<" + cron + ">");
	}
	
	private void stopAndRemoveSchedule(String scheduleName) {
		Object[] objects = mapSchedules.get(scheduleName);
		ScheduledFuture<?> existFuture = (ScheduledFuture<?>) objects[0];
		ThreadPoolTaskScheduler existSchedule = (ThreadPoolTaskScheduler) objects[1];
		existFuture.cancel(false);
		existSchedule.getScheduledThreadPoolExecutor().shutdown();
		mapSchedules.remove(scheduleName);
		log.debug("<ScheduleManager> stop schedule , name:<" + scheduleName + ">");
	}
	
	@Scheduled(fixedDelay = 30000)
	private void monScheduleChange() {
		List<String> lstPrepareUpdateSchedule = new ArrayList<String>();
		Set<Entry<String, Object[]>> entrySet = mapSchedules.entrySet();
		for (Entry<String, Object[]> entry : entrySet) {
			String scheduleName = entry.getKey();
			Object[] value = entry.getValue();
			String oldCron = (String) value[2];
			String newCron = appProperties.getProperty(scheduleName);
			if (!newCron.equals(oldCron)) {
				lstPrepareUpdateSchedule.add(scheduleName);
			}
		}
		
		processUpdateSchedule(lstPrepareUpdateSchedule);
	}
	
	private void processUpdateSchedule(List<String> lstPrepareUpdateSchedule) {
		for (String scheduleName : lstPrepareUpdateSchedule) {
			Object[] objects = mapSchedules.get(scheduleName);
			String Cron = (String) objects[2];
			log.info("<ScheduleManager> Prepare to update schedule, name:<" + scheduleName + ">, Cron:<" + Cron + ">");
			stopAndRemoveSchedule(scheduleName);
			addAndStartSchedule(scheduleName, (Runnable) objects[3]);
			log.info("<ScheduleManager> Update schedule, name:<" + scheduleName + ">, Cron:<" + Cron + ">");
		}
	}
	
}
