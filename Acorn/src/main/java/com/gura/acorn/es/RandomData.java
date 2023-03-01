package com.gura.acorn.es;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;

public class RandomData {
	
	private static RestHighLevelClient client;
	
	
	public static void main(String[] args) {
		//Util을 끌어다 쓰기에는 속도가 걱정되므로 새로 client를 정의하자.
		//id와 password 입력
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "acorn"));
		//ip와 host 입력 후 http 방식으로 사용할 것이라고 schema를 정의
		String hostname = "34.125.190.255";
		int port = 9200;
		HttpHost host = new HttpHost(hostname, port, "http");
		//위 정보를 바탕으로 client에 값을 주입
		client = new RestHighLevelClientBuilder(RestClient.builder(host).setHttpClientConfigCallback(
				httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
				).build()).
				//7.17버전이라 8.6.1버전인 ES와 맞지 않아서 호환성을 맞춰준다.
				setApiCompatibilityMode(true).build();
		
		//랜덤 데이터가 들어갈 BulkRequest를 작성
		BulkRequest rq=new BulkRequest();
		
		for (int i=0;i<3000;i++) {
			//시간과 날짜를 랜덤으로 정한다.(오늘로부터 1년 후까지)
			String time = LocalTime.of((int)(Math.random()*24), 0).toString();
			String date= LocalDate.ofEpochDay((long) (LocalDate.now().toEpochDay()+Math.random()*365)).toString();
			//7개의 id 중 하나를 랜덤으로 정하는데 필요한 값
			int ran1=(int)(Math.random()*7);
			String id = null;
			//55개의 URL중에 하나를 랜덤으로 정하는데 필요한 값
			int ran2=(int)(Math.random()*55);
			String url=null;
			String cate=null;
			
			switch (ran1) {
				case 6:
					id = "acorn";
					break;
				case 5:
					id = "tTest";
					break;
				case 4:
					id = "song1";
					break;
				case 3:
					id = "yg1234";
					break;
				case 2:
					id = "seodae";
					break;
				case 1:
					id = "maple";
					break;
				case 0:
					id = "admin";
					break;
			}
			
			if (ran2==0) {//0일 경우 home
				url="http://localhost:9200/";
			}else if(ran2<47) {//47 미만일 경우 각 번호의 상점 detail페이지에 들어간것으로  
				url="http://localhost:9200/shop/detail?num="+ran2;
			}else {//47 이상일 경우 카테고리 페이지로
				switch (ran2-47) {
					case 7:
						cate="기타";
						break;
					case 6:
						cate="패스트푸드";
						break;
					case 5:
						cate="양식";
						break;
					case 4:
						cate="분식";
						break;
					case 3:
						cate="일식";
						break;
					case 2:
						cate="중식";
						break;
					case 1:
						cate="한식";
						break;
					case 0:
						cate="";
						break;
				}//정해진 categorie로 URL을 결정한다.
				url="http://localhost:9200/shop/list?category="+cate;
			}
			
			//데이터를 mapping한다.
			Map<String, Object> map2= new HashMap<>();
			map2.put("id", id);
			map2.put("url", url);
			map2.put("date", date+" "+time);
			//데이터를 {"index":{"_index":"testlog"}
			//		 {"id": xx, "url" : xx, "date" : xx}
			//형식으로 만들어 bulkrequest에 입력한다.
			rq.add(new IndexRequest("testlog").source(map2));
		}
		
		
		
		BulkResponse rp=null;

		try {
			//request를 client에 넣어 작동시킨다.
			rp=client.bulk(rq, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(rp);
	}
}