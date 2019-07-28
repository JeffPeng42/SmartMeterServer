package sample.mapper.vo;

import lombok.Data;

@Data
public class ApplicationLog {
	
	private String 			Log_no;
	private String 			Log_date;
	private String 			sample_rate;
	private String			Log_Function;
	private String			Log_class;
	private String			Log_message;
	private String			Log_exception;
}