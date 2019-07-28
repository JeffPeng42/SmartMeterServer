package sample.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import sample.checksum.SHA256;
import sample.mapper.TestAMapper;
import sample.mapper.UpsertMapper;
import sample.mapper.vo.AMREvent;
import sample.mapper.vo.AMREvent_Data;
import sample.mapper.vo.BAS_AMR;
import sample.mapper.vo.Checkdb;
import sample.mapper.vo.MeteringData;
import sample.mapper.vo.MeteringData_Data;
import sample.mapper.vo.UnaConnectData;
import sample.mapper.vo.UnaConnectData_Data;
import sample.request.HttpRequestHandler;
import sample.seq.SeqManager;

@RestController
@Slf4j
class IwaterController {
	// private final EmployeeRepository repository;
	@Autowired
	private SHA256 sha256;
	
	@Autowired
	private HttpRequestHandler httpRequestHandler;
	
	@Autowired
	private TestAMapper testAMapper;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private SeqManager sqlManager;
	
	public String transTime(long timeStamp) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return dateFormat.format(timeStamp);
		}
		catch (NumberFormatException e) {
			return "";
		}
	}
	
	public String key(long date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			return dateFormat.format(date);
		}
		catch (NumberFormatException e) {
			return "";
		}
	}
	
	public boolean isNumeric(String string) {
		return string.matches("^[-+]?\\d+(\\.\\d+)?$");
	}
	
	//	@Scheduled(cron = "0 0 0 * * *")
	//	public void scheduleNo() {
	//		String key = key(System.currentTimeMillis());
	//		AtomicLong sch = null;
	//		
	//		sch = new AtomicLong();
	//		String seqNo = String.valueOf(sch.getAndIncrement());
	//		while (seqNo.length() < 4) {
	//			seqNo = "0" + seqNo;
	//		}
	//		Initializer.out = key + seqNo;
	//		System.out.println(Initializer.out);
	//		
	//		log.info("Prepare to execute scheduleNo");
	//	}
	
	// localhost:8080/syscom/MeteringDate
	@PostMapping(path = "/syscom/MeteringDate", consumes = "application/json", produces = "application/json")
	UnaConnectData_Data addMeteringDate(HttpEntity<String> httpEntity) {
		try {
			String jsonStr = httpEntity.getBody();
			JSONObject jObj = new JSONObject(jsonStr);
			log.info("Received : " + httpEntity);
			String events = null;
			try {
				Object object = jObj.get("amr_event");
			}
			catch (Exception e) {
				events = "1";
			}
			
			if (events != null) {
				
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(httpEntity);
				}
				catch (Exception e) {
					System.out.println(jsonObject);
				}
				
				ObjectMapper objectMapper = new ObjectMapper();
				UnaConnectData metering = new UnaConnectData();
				try {
					metering = objectMapper.readValue(jsonStr, UnaConnectData.class);
				}
				catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				
				//
				
				UnaConnectData_Data api = new UnaConnectData_Data();
				
				//				AtomicLong sch = null;
				//				long seq = Long.parseLong(Initializer.out) + 1;
				//				sch = new AtomicLong(seq);
				//				System.out.println(seq + "   " + Initializer.out);
				//				String seqNo = String.valueOf(sch.getAndIncrement());
				//				Initializer.out = seqNo;
				//				String Schedule = seqNo;
				
				api.setSchedule_no(sqlManager.getStrSeq());
				api.setDevice_id(metering.getDevice_id());
				api.setMeter_id(metering.getMeter_id());
				api.setData_count(metering.getCount());
				
				List<MeteringData> datas = null;
				if (metering.getData().size() > 0) {
					
					api.setData_type("data");
					datas = metering.getData();
					
				}
				
				if (metering.getResend_data().size() > 0) {
					api.setData_type("resentdata");
					datas = metering.getResend_data();
				}
				
				if (metering.getTest_data().size() > 0) {
					api.setData_type("testdata");
					
					datas = metering.getTest_data();
				}
				
				StringBuilder sb = new StringBuilder();
				for (MeteringData meteringData : datas) {
					
					sb.append(meteringData.toString());
				}
				
				BAS_AMR rate = null;
				try {
					rate = testAMapper.getrate(metering.getDevice_id());
				}
				catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				
				if (false) {//metering.getCount() != (rate.getSample_rate() / rate.getUplink_rate())) {
					api.setCheck_result("FU1");
					api.setCheck_status("F");
				}
				else if (metering.getCount() != datas.size()) {
					api.setCheck_result("FU2");
					api.setCheck_status("F");
				}
				else {
					api.setCheck_status("S");
				}
				
				api.setReply_body(sb.toString());
				
				String transTime = transTime(System.currentTimeMillis());
				api.setThrough_time(transTime);
				
				try {
					testAMapper.install(api);
				}
				catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				
				// check datas		
				
				for (MeteringData meteringData : datas) {
					if (api.getDevice_id().length() != 6) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					if (api.getMeter_id().length() != 7) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					String checksum = sha256.generateSHA256(meteringData.getTimestamp());
					if (meteringData.getChecksum() != checksum) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM1");
						continue;
					}
					
					if (meteringData.getFlag().length() != 1) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean index = isNumeric(meteringData.getIndex());
					if (!index) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean timestamp = isNumeric(meteringData.getTimestamp());
					if (!timestamp) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean forward_totalize = isNumeric(meteringData.getForward_totalize());
					if (!forward_totalize) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean reverse_totalize = isNumeric(meteringData.getReverse_totalize());
					if (!reverse_totalize) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						
						continue;
					}
					
					boolean flow_totalize = isNumeric(meteringData.getFlow_totalize());
					if (!flow_totalize) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean signal_quality = isNumeric(meteringData.getSignal_quality());
					if (!signal_quality) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean magnet_effect_day = isNumeric(meteringData.getMagnet_effect_day());
					if (!magnet_effect_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean meter_battery_low_day = isNumeric(meteringData.getMeter_battery_low_day());
					if (!meter_battery_low_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean amr_battery_low_day = isNumeric(meteringData.getAmr_battery_low_day());
					if (!amr_battery_low_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean l_day = isNumeric(meteringData.getL_day());
					if (!l_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean n_day = isNumeric(meteringData.getN_day());
					if (!n_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean o_day = isNumeric(meteringData.getO_day());
					if (!o_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean u_day = isNumeric(meteringData.getU_day());
					if (!u_day) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean flow_status = isNumeric(meteringData.getFlow_status());
					if (!flow_status) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					boolean overflow_counter = isNumeric(meteringData.getOverflow_counter());
					if (!overflow_counter) {
						meteringData.setCheck_status("F");
						meteringData.setCheck_result("FM3");
						continue;
					}
					
					meteringData.setCheck_status("S");
				}
				
				List<MeteringData_Data> maindata = new ArrayList<MeteringData_Data>();
				for (MeteringData meteringData : datas) {
					long time = Integer.valueOf(meteringData.getTimestamp());
					long javaTimeStamp = 1000 * time;
					String transtime = transTime(javaTimeStamp);
					
					MeteringData_Data data = new MeteringData_Data();
					maindata.add(data);
					data.setDevice_id(api.getDevice_id());
					data.setData_index(meteringData.getIndex());
					data.setMeter_id(api.getMeter_id());
					data.setData_type(api.getData_type());
					data.setTimestamp(meteringData.getTimestamp());
					data.setTs(transtime);
					data.setForward_totalize(meteringData.getForward_totalize());
					data.setReverse_totalize(meteringData.getReverse_totalize());
					data.setFlow_totalize(meteringData.getFlow_totalize());
					data.setSignal_quality(meteringData.getSignal_quality());
					data.setMagnet_effect_day(meteringData.getMagnet_effect_day());
					data.setMeter_battery_low_day(meteringData.getMeter_battery_low_day());
					data.setAmr_battery_low_day(meteringData.getAmr_battery_low_day());
					data.setData_checksum(meteringData.getChecksum());
					data.setL_day(meteringData.getL_day());
					data.setN_day(meteringData.getN_day());
					data.setO_day(meteringData.getO_day());
					data.setU_day(meteringData.getU_day());
					data.setFlow_status(meteringData.getFlow_status());
					data.setOverflow_counter(meteringData.getOverflow_counter());
					data.setFlag(meteringData.getFlag());
					data.setFlow_rate(null);
					data.setForward_rate(null);
					data.setReverse_rate(null);
					data.setCheck_result(meteringData.getCheck_result());
					data.setCheck_status(meteringData.getCheck_status());
					String checkTime = transTime(System.currentTimeMillis());
					
					data.setSynchronize_Time(checkTime);
					
				}
				SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
				UpsertMapper mapper = sqlSession.getMapper(UpsertMapper.class);
				try {
					for (MeteringData_Data meteringData_Data : maindata) {
						mapper.upsert(meteringData_Data);
					}
					sqlSession.commit();
				}
				catch (Exception ex) {
					sqlSession.rollback();
					ex.printStackTrace();
					log.debug(ex.getMessage(), ex);
				}
				
			}
			
			// AMREvent
			else {
				ObjectMapper objectMapper = new ObjectMapper();
				AMREvent amrevent = new AMREvent();
				try {
					amrevent = objectMapper.readValue(jsonStr, AMREvent.class);
				}
				catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				AMREvent_Data event = new AMREvent_Data();
				
				long javaTimeStamp = 1000 * amrevent.getTimestamp();
				String transTime = transTime(javaTimeStamp);
				
				event.setDevice_id(amrevent.getDevice_id());
				event.setMeter_id(amrevent.getMeter_id());
				event.setTimestamp(amrevent.getTimestamp());
				event.setAMR_Event(amrevent.getAmr_event());
				String checkTime = transTime(System.currentTimeMillis());
				event.setNotification_Time(checkTime);
				event.setTs(transTime);
				System.out.println(event);
				
				try {
					testAMapper.Eventinstall(event);
				}
				catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
				
			}
		}
		catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return null;
	}
	//
	//	@PostMapping(path = "/syscom/Event", consumes = "application/json", produces = "application/json")
	//	AMREvent_Data addEventDate(@RequestBody AMREvent data) {
	//		AMREvent_Data event = new AMREvent_Data();
	//
	//		long javaTimeStamp = 1000 * data.getTimestamp();
	//		String transTime = transTime(javaTimeStamp);
	//
	//		event.setDevice_id(data.getDevice_id());
	//		event.setTimestamp(data.getTimestamp());
	//		event.setAMR_Event(data.getAmr_event());
	//		event.setTs(transTime);
	//		System.out.println(event);
	//		testAMapper.Eventinstall(event);
	//
	//		return null;
	//
	//	}
	
	@PostMapping(path = "/syscom/checkdb", consumes = "application/json", produces = "application/json")
	MeteringData_Data[] addCheckdb(HttpEntity<String> httpEntity) {
		
		//		String url = "http://localhost:8080//syscom/taipeiwaterdb";
		//		MeteringData_Data[] responsed = httpRequestHandler.sendHttpPost(url, httpEntity, MeteringData_Data[].class);
		//
		log.info("If you want init some thing, do here ");
		
		//		return responsed;
		return null;
	}
	
	@PostMapping(path = "/syscom/resenddata", consumes = "application/json", produces = "application/json")
	String addCheckdb(@RequestBody Checkdb data) {
		
		List<MeteringData_Data> resenddata = testAMapper.resenddata(data);
		
		String url = "http://localhost:11034//syscom/taipeiwater";
		String responsed = httpRequestHandler.sendmeterPost(url, resenddata, String.class);
		
		log.info("If you want init some thing, do here ");
		
		return responsed;
		
	}
	
	//	private String verifyMeteringDate(HttpEntity<String> httpEntity) {
	//		String json = httpEntity.getBody();
	//		JSONObject jObj = new JSONObject(json);
	//
	//		Object object = jObj.get("amr_event");
	//		System.out.println(object);
	//		if (object != null) {
	//		} else {
	//			// record error message
	//		}
	//
	//		return json;
	//	}
}
