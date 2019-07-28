package sample.mapper.vo;

import lombok.Data;

@Data
public class MeteringData_Data {
	
	private String device_id;
	private String data_index;
	private String meter_id;
	private String data_type;
	private String timestamp;
	private String ts;
	private String forward_totalize;
	private String reverse_totalize;
	private String flow_totalize;
	private String signal_quality;
	private String magnet_effect_day;
	private String meter_battery_low_day;
	private String amr_battery_low_day; 
	private String data_checksum;
	private String l_day;
	private String n_day;
	private String o_day;
	private String u_day;
	private String flow_status;
	private String overflow_counter;
	private String flag;
	private String flow_rate;
	private String forward_rate;
	private String reverse_rate;
	private String check_status;
	private String check_result;
	private String Synchronize_Time;
	
	
	
}
