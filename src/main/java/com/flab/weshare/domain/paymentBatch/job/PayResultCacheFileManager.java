package com.flab.weshare.domain.paymentBatch.job;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.paymentBatch.PayResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayResultCacheFileManager {
	private static final DateTimeFormatter fileNameformatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
	private static final String EXTENSION = ".json";

	private final List<JSONObject> cacheList = new ArrayList<>();
	private final String pathDirectory;
	private final int MAX_CACHE_SIZE;
	private final ObjectMapper objectMapper;

	public PayResultCacheFileManager(String path, int MAX_CACHE_SIZE, LocalDate payDate, ObjectMapper objectMapper) {
		this.MAX_CACHE_SIZE = MAX_CACHE_SIZE;
		this.objectMapper = objectMapper;
		this.pathDirectory = path + payDate.toString();
	}

	public void add(PayResult payResult) {
		JSONObject jsonObject = convertToJsonObject(payResult);
		cacheList.add(jsonObject);

		if (cacheList.size() == MAX_CACHE_SIZE) {
			log.info("array {}", cacheList);
			flushCache();
		}
	}

	public void flushCache() {
		if (this.cacheList.isEmpty()) {
			return;
		}
		fileSave();
		this.cacheList.clear();
	}

	private void fileSave() {
		File directory = getDirectory();
		File childFile = new File(directory,
			LocalDateTime.now().format(fileNameformatter) + EXTENSION);
		try (FileWriter fileWriter = new FileWriter(childFile)) {
			childFile.createNewFile();
			JSONArray jsonArray = new JSONArray(cacheList);
			fileWriter.write(jsonArray.toString());
			fileWriter.flush();
		} catch (Exception e) {
			log.error("payResult 파일 저장 중 에러", e);
		}
	}

	private File getDirectory() {
		File directory = new File(pathDirectory);
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	public JSONObject convertToJsonObject(PayResult payResult) {
		JSONObject convertedJson = null;
		try {
			convertedJson = new JSONObject(objectMapper.writeValueAsString(payResult));
		} catch (JSONException | JsonProcessingException e) {
			log.error("pay result json 파싱중 에러", e);
		}
		return convertedJson;
	}
}
