package sample.mapper.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UnaConnectData {
	
	private String 			Device_id;
	private String 			Meter_id;
	private int	     	    count;
	
	public List<MeteringData> data	= new ArrayList<MeteringData>();
	
	public List<MeteringData> test_data	= new ArrayList<MeteringData>();
	
	public List<MeteringData> resend_data	= new ArrayList<MeteringData>();
	
	
}
