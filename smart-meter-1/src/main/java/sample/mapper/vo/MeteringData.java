package sample.mapper.vo;

import lombok.Data;

@Data
public class MeteringData {
	
	private String timestamp;
	private String forward_totalize;
	private String reverse_totalize;
	private String flow_totalize;
	private String signal_quality;
	private String magnet_effect_day;
	private String meter_battery_low_day;
	private String amr_battery_low_day;
	private String checksum;
	private String l_day;
	private String n_day;
	private String o_day;
	private String u_day;
	private String flow_status;
	private String overflow_counter;
	private String flag;
	private String index;
	private String check_status;
	private String check_result;
}
