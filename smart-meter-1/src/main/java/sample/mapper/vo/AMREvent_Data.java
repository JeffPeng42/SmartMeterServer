package sample.mapper.vo;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class AMREvent_Data {
	
	private String 			Device_id;
	private String 			Meter_id;
	private long 			Timestamp;
	private int 			AMR_Event;
	private String   Notification_Time;
	private String		    Ts;
}
