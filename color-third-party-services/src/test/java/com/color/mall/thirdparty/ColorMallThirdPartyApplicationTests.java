package com.color.mall.thirdparty;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import org.junit.jupiter.api.Test;
import com.aliyun.oss.OSSClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class ColorMallThirdPartyApplicationTests {

    @Test
    void contextLoads() {


    }

    @Autowired(required = false)
    private OSSClient ossClient;

    @Test
    public void testUpload(){
        String bucketName = "color-anta-mall";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "example/image2.jpg";

        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);


        try {
            FileInputStream inputStream = new FileInputStream("D:\\图片\\0c0f3f16880511ebb6edd017c2d2eca2.jpg");
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException | FileNotFoundException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
            System.out.println("上传成功");
        }
    }
}
