package sample.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import sample.mapper.vo.AMREvent_Data;
import sample.mapper.vo.BAS_AMR;
import sample.mapper.vo.Checkdb;
import sample.mapper.vo.MeteringData_Data;
import sample.mapper.vo.UnaConnectData_Data;

public interface TestAMapper {
	
	@Select("select * from BAS_AMR where Device_id=#{rate}")
	BAS_AMR getrate(String rate);
	
	@Insert({ "insert into UnaConnectData" + " values(#{Schedule_no},#{Device_id},#{Meter_id},#{Data_type},#{Data_count},#{Reply_body},#{Through_time},#{check_status},#{Check_result})" })
	void install(UnaConnectData_Data api);
	
	@Insert({ "insert into AMREvent(Device_id,Meter_id,Timestamp,AMR_Event,Notification_Time,Ts)" + " values(#{Device_id},#{Meter_id},#{Timestamp},#{AMR_Event},#{Notification_Time},#{Ts})" })
	void Eventinstall(AMREvent_Data event);
	
	//	Synchroinzed
	@Select("select schedule_no from UnaConnectData where schedule_no like #{key} order by schedule_no DESC limit 0,1")
	@Results({ @Result(property = "schedule_no", column = "schedule_no") })
	String getByschedule_no(String key);
	
	@Insert({ "insert into UnaConnectData (Schedule_no)" + " values(#{Schedule_no})" })
	void testInsUCDD(UnaConnectData_Data ucdd);
	
	@Select("select * from MeteringData where check_status='S' and ts between #{Starttime} and #{Endtime}")
	List<MeteringData_Data> getdatas(Checkdb data);
	
	@Select("select * from MeteringData where device_id = #{device_id} and check_status='S' and ts between #{Starttime} and #{Endtime}")
	List<MeteringData_Data> resenddata(Checkdb data);
	
}
