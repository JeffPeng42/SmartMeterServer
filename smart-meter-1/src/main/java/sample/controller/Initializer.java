package sample.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;
import sample.filemanager.FileManager;
import sample.mapper.TestAMapper;
import sample.mapper.vo.MeteringData_Data;
import sample.request.HttpRequestHandler;
import sample.schedule.ScheduleManager;
import sample.seq.SeqManager;

@Configuration
@Slf4j
class Initializer {
	@Autowired
	TestAMapper testAMapper;
	
	@Autowired
	SeqManager seqManager;
	
	@Autowired
	FileManager fileManager;
	
	@Autowired
	private HttpRequestHandler httpRequestHandler;
	
	@Value("${test.value}")
	int testValue;
	
	@Autowired
	Environment evn;
	
	@Autowired
	ScheduleManager scheduleManager;
	
	@Bean
	CommandLineRunner afterInitServer() {
		return args -> {
			seqManager.initSeq();
			
			fileManager.initFileManager();
			
			httpRequestHandler.init();
			
			scheduleSample();
			
			//			fileManager.putJsonData("fileName111", "jsonConent1111");
			//			
			//			fileManager.putJsonData("fileName111", "jsonConent2222");
			
			//			System.out.println("=============== Test:<" + evn.getProperty("test.value") + ">");
			//			
			//			Thread.sleep(15000);
			//			
			//			System.out.println("=============== Test:<" + evn.getProperty("test.value") + ">");
			
			//			testValue(env.getProperty("test.value"));
			
			//			testWriteFile();
			//			
			//			testHttps();
			
			//			httpRequestHandler.sendmeterMd5Post(url, meteringData_Data.toString(), String.class);
			
			log.info("If you want init some thing, do here ");
		};
	}
	
	private void scheduleSample() {
		// scheduleName 需定義再application.properties中，且值為對應的cron表示式 ex : test.schedule=0/20 * * * * ?
		scheduleManager.setSchedule("test.schedule", new Runnable() {
			@Override
			public void run() {
				System.out.println("The job you want to do.");
			}
		});
	}
	
	private void testHttps() {
		List<MeteringData_Data> lstData = new ArrayList<>();
		MeteringData_Data meteringData_Data = new MeteringData_Data();
		meteringData_Data.setDevice_id("A");
		lstData.add(meteringData_Data);
		
		String url = "https://192.168.0.4:11044//syscom/taipeiwater";
		String responsed = httpRequestHandler.sendmeterHttpsPost(url, lstData, String.class);
	}
	
	private void testWriteFile() throws JSONException {
		String jsonStr = "{\"device_id\":\"11D6655\",\n" + "\"meter_id\":0,\"count\":2,\n" + "\"data\":[{\"timestamp\":1564044506,\n" + "	\"forward_totalize\":0.13,\n" + "	\"reverse_totalize\":0,\n" + "	\"flow_totalize\":0.13,\n" + "	\"signal_quality\":3,\n" + "	\"magnet_effect_day\":0,\n" + "	\"meter_battery_low_day\":0,\n" + "	\"amr_battery_low_day\":1014,\n" + "	\"checksum\":\"85dfaf610fa17053fab76d87a1023e6a3889d014bf67691aa2692b651c057e34\",\n" + "	\"l_day\":0,\n" + "	\"n_day\":0,\n" + "	\"o_day\":12,\n" + "	\"u_day\":0,\n" + "	\"flow_status\":155,\n" + "	\"overflow_counter\":0,\n" + "	\"flag\":60,\n" + "	\"index\":1023},\n" + "	\n" + "	{\"timestamp\":1564043606,\n" + "	\"forward_totalize\":0.13,\n" + "	\"reverse_totalize\":0,\n" + "	\"flow_totalize\":0.13,\n" + "	\"signal_quality\":3,\n" + "	\"magnet_effect_day\":0,\n" + "	\"meter_battery_low_day\":0,\n" + "	\"amr_battery_low_day\":1014,\n" + "	\"checksum\":\"4b7f43279176acc977572f99c41774e00744761872697192d63dc8c106008931\",\n" + "	\"l_day\":0,\n" + "	\"n_day\":0,\n" + "	\"o_day\":12,\n" + "	\"u_day\":0,\n" + "	\"flow_status\":155,\n" + "	\"overflow_counter\":0,\n" + "	\"flag\":60,\"index\":1023}]}";
		JSONObject jObj = new JSONObject(jsonStr);
		String sDevice_id = jObj.getString("device_id");
		JSONArray jsonArray = jObj.getJSONArray("data");
		for (int i = 0; i < jsonArray.length(); i++) {
			String data = jsonArray.getString(i);
			fileManager.putJsonData(sDevice_id, data);
		}
	}
	
}
