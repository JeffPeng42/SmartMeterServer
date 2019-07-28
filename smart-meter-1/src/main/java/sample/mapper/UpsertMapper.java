package sample.mapper;

import org.apache.ibatis.annotations.Update;

import sample.mapper.vo.MeteringData_Data;

public interface UpsertMapper {
	@Update({"IF EXISTS (SELECT * FROM MeteringData WHERE device_id=#{device_id} AND timestamp =#{timestamp})"
	   +" UPDATE MeteringData" 
	   +" SET "
	   + "data_index 				=	#{data_index},"
	   + "meter_id   				=	#{meter_id},"
	   + "data_type  				=	#{data_type},"
	   + "timestamp 				=	#{timestamp},"
	   + "ts						=	#{ts},"
	   + "forward_totalize			=	#{forward_totalize},"
	   + "reverse_totalize			=	#{reverse_totalize},"
	   + "flow_totalize		   		=	#{flow_totalize},"
	   + "signal_quality 			=	#{signal_quality},"
	   + "magnet_effect_day 		=	#{magnet_effect_day},"
	   + "meter_battery_low_day 	=	#{meter_battery_low_day},"
	   + "amr_battery_low_day 		=	#{amr_battery_low_day},"
	   + "data_checksum 			=	#{data_checksum},"
	   + "l_day 					=	#{l_day},"
	   + "n_day 					=	#{n_day},"
	   + "o_day 					=	#{o_day},"
	   + "u_day 					=	#{u_day},"
	   + "flow_status 				=	#{flow_status},"
	   + "overflow_counter			=	#{overflow_counter},"
	   + "flag						=	#{flag},"
	   + "flow_rate 				=	#{flow_rate},"
	   + "forward_rate 				=	#{forward_rate},"
	   + "reverse_rate 				=	#{reverse_rate},"
	   + "check_status 				=	#{check_status},"
	   + "check_result				=	#{check_result},"
	   + "Synchronize_Time 			=	#{Synchronize_Time} "
	   + "WHERE device_id=#{device_id} AND timestamp =#{timestamp} "
	+"ELSE "
	+"INSERT INTO MeteringData" 
	+"	VALUES (  #{device_id}, #{data_index}, #{meter_id}, #{data_type}, #{timestamp},#{ts},#{forward_totalize}" 
	+"			, #{reverse_totalize}, #{flow_totalize}, #{signal_quality}, #{magnet_effect_day}, #{meter_battery_low_day}"
	+"			, #{amr_battery_low_day}, #{data_checksum}, #{l_day}, #{n_day}, #{o_day}, #{u_day}, #{flow_status}"
	+"			, #{overflow_counter}, #{flag}, #{flow_rate}, #{forward_rate}, #{reverse_rate}, #{check_status}, #{check_result},#{Synchronize_Time})"
	}) 
	void upsert(MeteringData_Data meteringData_Data);
}

