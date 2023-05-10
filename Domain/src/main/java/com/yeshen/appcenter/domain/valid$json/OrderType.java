package com.yeshen.appcenter.domain.valid$json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.lang.reflect.Type;

/**
 * Date 2022/06/30  17:30
 * author  by HuBingKuan
 */
// 方法1:继承JSONSerializable
// 方法2:@JSONType(serializeEnumAsJavaBean = true)  需要1.2.24之后的版本
// 方法3:直接修改SerializeConfig配置Enum当做JavaBean序列化，需要1.2.24之后的版本，这个方法的好处是不需要修改Enum类的代码
//SerializeConfig.globalInstance.configEnumAsJavaBean(OrderType.class);
public enum OrderType implements JSONSerializable {
    PayOrder(1, "支付订单"),
    SettleBill(2, "结算单");

    public final int    value;
    public final String remark;

     OrderType(int value, String remark){
        this.value = value;
        this.remark = remark;
    }

    // 将枚举类型当作JavaBean序列化输出
    /*
    * Model model = new Model();
      model.id = 1001;
      model.orderType = OrderType.PayOrder;
      String text = JSON.toJSONString(model);
      Assert.assertEquals("{\"id\":1001,\"orderType\":{\"remark\":\"支付订单\",\"value\":1}}", text);
    *
    * */
    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) {
        JSONObject json = new JSONObject();
        json.put("value", value);
        json.put("remark", remark);
        serializer.write(json);
    }
}