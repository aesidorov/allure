package tests.swaggertests;

import io.qameta.allure.Attachment;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.FileService;

import java.io.File;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;


public class FileTests {
    private static FileService fileService;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/api";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        fileService = new FileService();
    }

    @Test
    public void positiveDownloadTest() {
        byte[] file = fileService.downloadBaseImage().asResponse().asByteArray();
        File expextedFile = new File("src/test/resources/threadqa.jpeg");

        Assertions.assertEquals(expextedFile.length(), file.length);
    }

    @Attachment(value = "downloaded", type = "image/png")
    private byte[] attachFile(byte[] bytes) {
        return bytes;
    }

    @Test
    public void positiveUploadTest() {
        byte[] file = fileService.downloadBaseImage().asResponse().asByteArray();
        attachFile(file);
        File expextedFile = new File("src/test/resources/threadqa.jpeg");
        fileService.upload(expextedFile)
                .should(hasStatusCode(200))
                .should(hasMessage("file uploaded to server"));

        byte[] actualFile = fileService.downloadLastFile().asResponse().asByteArray();
        Assertions.assertTrue(actualFile.length != 0);
        Assertions.assertEquals(expextedFile.length(), actualFile.length);
    }


}
