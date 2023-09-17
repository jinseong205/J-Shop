package com.shop.server.common.test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataGeneratorRunner implements CommandLineRunner {

    private final DataGenerator dataGenerator;

    public DataGeneratorRunner(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void run(String... args) throws Exception {
        //테스트용 계정을 생성합니다. 실 사용을 위해서는 주석 처리 해주세요.
    	try {
        	dataGenerator.generateData();
        }catch(Exception e) {}
    }
}