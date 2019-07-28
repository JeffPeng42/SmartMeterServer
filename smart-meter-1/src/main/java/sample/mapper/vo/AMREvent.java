package sample.mapper.vo;


import lombok.Data;

@Data
public class AMREvent {
	
	private String				   Device_id;
	private String 				   Meter_id ;
	private long				   Timestamp;
	private int					   Amr_event; 
}
