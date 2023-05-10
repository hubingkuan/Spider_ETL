package com.yeshen.appcenter.domain.valid$json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Date 2022/04/21  22:59
 * author by HuBingKuan
 * 注意使用@JsonField注解是FastJson的 要想注解效果生效必须将springboot默认
 * 指定的Jackson消息转换器替换为FastJson的消息转换器 同时Jackson的注解将无效
 * 比如@JsonInclude @JsonFormat @JsonAlies等等
 * @JsonInclude(value= JsonInclude.Include.NON_NULL) 忽略null字段
 */
@Data
// 序列化的时候，不序列化id sex
@JSONType(ignores = {"id","sex"})
@TableName(value = "user", autoResultMap = true)
public class User {
    private int iddd;

    @TableId(type=IdType.AUTO)
    private Long id;

    // 设置序列化顺序  反序列化名称  序列化名称(返回前端的名称)  默认值
    @JSONField(ordinal = 3, alternateNames = {"nam", "nae"}, name = "na", defaultValue = "maguahu")
    @Length(min = 10,max = 20)
    @NotBlank
    private String name;

    @NotNull
    @Range(min = 1,max = 20)
    @JSONField(ordinal = 2, name = "ag", defaultValue = "55")
    private Integer age;

    @JSONField(ordinal = 1, format = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime bir;

    // 限制条件还可以进行分组
    @Max(value = 50, groups = {UpdateGroup.class, Default.class})
    @Min(value = 10, groups = {UpdateGroup.class, Default.class})
    @Max(value = 200, groups = {InsertGroup.class})
    @Min(value = 100, groups = {UpdateGroup.class})
    @JSONField(ordinal = 0, defaultValue = "0.03")
    private Double money;

    @Sex
    @NotBlank
    private String sex;

    // 枚举类型字段使用@EnumValue
    private GradeEnum gradeEnum;


    /*
    * 同时这里的@TableName必须加上autoResultMap = true
    * 这里使用FastjsonTypeHandler将List的类型转为String类型保存到数据库中
    * 同理 反序列的时候也会将String反序列化为List
    * */
    @Valid
    @NotNull(message = "list不能为null",groups = InsertGroup.class)
    @TableField(typeHandler = FastjsonTypeHandler.class)
    List<String> wishlist;

    // 不序列化此字段
    @JSONField(serialize = false)
    private String test;

    @JSONField(serializeUsing = ModelValueSerializer.class,label = "normal")
    private Integer value;


    /*
    * 自定义序列化过滤器
    * PropertyPreFilter 根据PropertyName判断是否序列化
    * PropertyFilter 根据PropertyName和PropertyValue来判断是否序列化
    * NameFilter 修改Key，如果需要修改Key,process返回值则可
    * ValueFilter 修改Value
    * ContextValueFilter 修改Value(多了BeanContext参数可用)
    * BeforeFilter 序列化时在最前添加内容
    * AfterFilter 序列化时在最后添加内容
    * */
    public static void main(String[] args) {
        SerializeConfig serializeConfig = new SerializeConfig(false);
        User user = new User();
        user.setSex("man");
        user.setBir(LocalDateTime.now());
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("sex");
        // SerializerFeature.PrettyFormat格式化输出
        System.out.println(JSON.toJSONString(user,serializeConfig, filter, SerializerFeature.PrettyFormat));
        // 分组序列化字段
        JSON.toJSONString(user, Labels.excludes("normal"));
/*        反序列化带泛型
        Type type = new TypeReference<List<Model>>() {}.getType();
        List<Model> list = JSON.parseObject(jsonStr, type);

        //将json转为带泛型的map对象
        Map<String,String> map=JSON.parseObject(msg,new TypeReference<Map<String,String>>(){},null);
        */

        // 设置成全局:SerializeConfig.getGlobalInstance().addFilter(Person.class, formatName);

    }

    class ModelValueSerializer implements ObjectSerializer{
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
                          int features) throws IOException {
            Integer value = (Integer) object;
            String text = value + "元";
            serializer.write(text);
        }
    }
}