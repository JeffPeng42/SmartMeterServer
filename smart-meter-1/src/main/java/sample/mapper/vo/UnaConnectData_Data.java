package sample.mapper.vo;

import lombok.Data;

@Data
public class UnaConnectData_Data {

	private String Schedule_no;
	private String Device_id;
	private String Meter_id;
	private String Data_type;
	private int    Data_count;
	private String Reply_body;
	private String Through_time;
	private String Check_status;
	private String Check_result;

}
