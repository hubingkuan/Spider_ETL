package com.yeshen.appcenter.alioss;

import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.enums.ResultCode;
import com.yeshen.appcenter.domain.vo.response.ImageInfo;
import com.yeshen.appcenter.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.MessageFormat;

/**
 * Date 2022/07/07  19:52
 * author  by HuBingKuan
 */
@Slf4j
@Component
@ConditionalOnBean(OssProperties.class)
public class OssModule {
    @Autowired
    private OssProperties ossProperties;

    private OSS oSSClient;


    @PostConstruct
    public void initModule() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(200);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(50000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(60000);
        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(3);
        // 设置是否支持将自定义域名作为Endpoint，默认支持。
        conf.setSupportCname(true);
        // 设置是否开启二级域名的访问方式，默认不开启。
        conf.setSLDEnabled(true);
        // 设置连接OSS所使用的协议（HTTP或HTTPS），默认为HTTP。
        conf.setProtocol(Protocol.HTTP);
        oSSClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), conf);
    }

    /**
     * 判断文件是否存在 (判断文件元信息是否相同 不同则删除文件)
     *
     * @param objectName  oss中对象名称(比如appcenter/en/gp-game-image/0002901d96e956adcf2ecbb30238228c)
     * @param contentType 文件的MediaType
     * @return
     */
    public boolean isExistFile(String objectName, String contentType) {
        boolean found;
        try {
            found = oSSClient.doesObjectExist(ossProperties.getBucketName(), objectName);
            // 存在则判断原信息
            if (found) {
                ObjectMetadata metadata = oSSClient.getObjectMetadata(ossProperties.getBucketName(), objectName);
                if (!metadata.getContentType().equals(contentType)) {
                    // 删除文件
                    deleteFile(objectName);
                    found = false;
                }
            }
        } catch (OSSException e) {
            log.warn("oss拒绝了请求,Error Code:{},Error Message:{}", e.getErrorCode(), e.getMessage());
            throw new BusinessException(ResultCode.OSS_REQUEST_ERROR);
        } catch (ClientException e) {
            log.warn("客户端错误,错误信息:{}", e.getMessage());
            throw new BusinessException(ResultCode.OSS_NETWORK_ERROR);
        }
        return found;
    }


    /**
     * 通过Url上传文件.返回上传之后的链接
     *
     * @param region   上传到oss的国家代码
     * @param photoUrl gp图片链接
     * @return ImageInfo
     */
    public ImageInfo uploadPhotoByUrl(String region, String photoUrl) {
        byte[] bytes = null;
        String contentType = "image/webp";
        if (photoUrl.startsWith("http")) {
            ResponseEntity<byte[]> responseEntity = HttpClientUtil.getPhotoBytesByUrl(photoUrl);
            bytes = responseEntity.getBody();
            contentType = responseEntity.getHeaders().getContentType().toString();
        } else {
            try (InputStream in = new FileInputStream(photoUrl)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 2];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                bytes = out.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uploadPhotoByUrl(region, bytes, contentType, photoUrl);
    }

    public ImageInfo uploadPhotoByUrl(String region, byte[] bytes, String mediaType, String photoUrl) {
        ImageInfo result = null;
        String contentType;
        if (StringUtils.hasText(mediaType)) {
            contentType = mediaType;
        } else {
            contentType = "image/webp";
        }
        try {
            String md5sum = DigestUtils.md5Hex(bytes);
            String ossPath = MessageFormat.format("appcenter/{0}/gp-game-image/", region);
            String objectName = ossPath + md5sum;
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if (log.isDebugEnabled()) {
                log.debug("上传的文件名:{},MediaType:{},图片宽度:{},高度:{}", objectName, contentType, width, height);
            }
            if (isExistFile(objectName, contentType)) {
                if (log.isDebugEnabled()) {
                    log.debug("文件已存在,文件名:{}", objectName);
                }
            } else {
                // 创建上传文件的元信息，可以通过文件元信息设置HTTP header
                ObjectMetadata meta = new ObjectMetadata();
                // 指定上传的内容类型
                meta.setContentType(contentType);
                // 注意要重新创建一个流 上面的流已关闭 不然上传的图片有问题(空白)
                oSSClient.putObject(ossProperties.getBucketName(), objectName, new ByteArrayInputStream(bytes), meta);
            }
            result = ImageInfo.builder()
                    .gpUrl(photoUrl)
                    .ossUrl(ossProperties.getHostname() + objectName)
                    .width(width)
                    .height(height).build();
        } catch (IOException e) {
            log.warn("IO异常:{}", e.getMessage());
        } catch (OSSException e) {
            log.warn("oss拒绝了请求,Request ID:{},Host ID:{},Error Code:{},Error Message:{}", e.getRequestId(), e.getHostId(), e.getErrorCode(), e.getMessage());
            throw new BusinessException(ResultCode.OSS_REQUEST_ERROR);
        } catch (ClientException e) {
            log.warn("客户端错误,错误信息:{}", e.getMessage());
            throw new BusinessException(ResultCode.OSS_NETWORK_ERROR);
        }
        return result;
    }

    /**
     * 下载文件到本地
     *
     * @param objectName oss中对象名称(比如appcenter/en/gp-game-image/0002901d96e956adcf2ecbb30238228c)
     * @param pathName   本地路径 (比如 D:\\localpath\\examplefile.txt)
     * @return
     */
    public void downloadFileToLocal(String objectName, String pathName) {
        try {
            oSSClient.getObject(new GetObjectRequest(ossProperties.getBucketName(), objectName), new File(pathName));
        } catch (OSSException e) {
            log.warn("oss拒绝了请求,Error Code:{},Error Message:{}", e.getErrorCode(), e.getMessage());
            throw new BusinessException(ResultCode.OSS_REQUEST_ERROR);
        } catch (ClientException e) {
            log.warn("客户端错误,错误信息:{}", e.getMessage());
            throw new BusinessException(ResultCode.OSS_NETWORK_ERROR);
        }
    }

    /**
     * 删除单个文件
     *
     * @param objectName oss中对象名称(比如appcenter/en/gp-game-image/0002901d96e956adcf2ecbb30238228c)
     */
    public void deleteFile(String objectName) {
        try {
            log.info("删除oss文件:{}", objectName);
            oSSClient.deleteObject(ossProperties.getBucketName(), objectName);
        } catch (OSSException e) {
            log.warn("oss拒绝了请求,Error Code:{},Error Message:{}", e.getErrorCode(), e.getMessage());
            throw new BusinessException(ResultCode.OSS_REQUEST_ERROR);
        } catch (ClientException e) {
            log.warn("客户端错误,错误信息:{}", e.getMessage());
            throw new BusinessException(ResultCode.OSS_NETWORK_ERROR);
        }
    }

    /**
     * 拷贝文件(将文件复制到同一地域下相同或不同目标的Bucket中)
     * 1、拷贝文件必须拥有源文件的读权限和目标Bucket的读写权限
     * 2、确保源Bucket和目标Bucket均为设置合规保留策略
     * 3、不支持跨地域拷贝，比如不能将华东1(杭州)存储空间的文件拷贝到华北1(青岛)地域
     *
     * @param sourceKey             源文件名
     * @param destinationBucketName 目标BucketName
     * @param destinationKey        目标文件名
     * @return
     */
    public boolean copyFile(String sourceKey, String destinationBucketName, String destinationKey) {
        boolean result;
        // 创建OSSClient实例  三个参数分别为 Bucket所在地域对应的Endpoint，AccessKey，accessKeySecret
        try {
            //key：oss源文件的地址
            //path：oss目标文件的地址
            //ossWinnerProperties.getBucketName():oss上的存储空间
            CopyObjectResult copyObject = oSSClient.copyObject(ossProperties.getBucketName(), sourceKey, destinationBucketName, destinationKey);
            log.info("拷贝文件成功,文件名:{},最后的修改时间:{}", destinationKey, copyObject.getLastModified());
            result = true;
        } catch (OSSException e) {
            log.warn("oss拒绝了请求,Error Code:{},Error Message:{}", e.getErrorCode(), e.getMessage());
            throw new BusinessException(ResultCode.OSS_REQUEST_ERROR);
        } catch (ClientException e) {
            log.warn("客户端错误,错误信息:{}", e.getMessage());
            throw new BusinessException(ResultCode.OSS_NETWORK_ERROR);
        }
        return result;
    }
}